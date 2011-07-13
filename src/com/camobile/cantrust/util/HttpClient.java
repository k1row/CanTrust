package com.camobile.cantrust.util;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpResponseException;

import android.util.Log;


public class HttpClient
{
  private String url;
  public HttpClient (String _url)
  {
    this.url = _url;
  }

  public InputStream request ()
  {
    InputStream ret = null;

    try
    {
      Log.d ("CanTrust", "Start HttpClient::request url = " + url);

      DefaultHttpClient http = new DefaultHttpClient ();

      /*
      HttpHost proxy = new HttpHost ("gate.camobile.com", 12080);
      http.getParams ().setParameter (ConnRoutePNames.DEFAULT_PROXY, proxy);
       */

      HttpGet g = new HttpGet (url);
      HttpResponse res = http.execute (g);

      //if (statuscode != HttpStatus.SC_OK && statuscode != HttpStatus.SC_CREATED)
      //  throw new HttpResponseException (statuscode, "Response code is " + Integer.toString (statuscode));

      // httpレスポンスの400番台以降はエラーだから例外をthrow
      if (res.getStatusLine ().getStatusCode ()  >= 400 )
      {
        Log.d ("CanTrust", "HttpClient::request faild !! -> " + String.valueOf (res.getStatusLine ().getStatusCode ()));
        Log.d ("CanTrust", res.getStatusLine ().getReasonPhrase ());
        throw new HttpResponseException (res.getStatusLine ().getStatusCode (), res.getStatusLine ().getReasonPhrase ());
      }

      // httpレスポンスの400番台以降はエラーだから
      HttpEntity entity = res.getEntity ();

      ret = entity.getContent (); // = InputStream

      /*
      // レスポンス本体を取得
      InputStream inputstream = entity.getContent ();
      InputStreamReader inputstreamreader = new InputStreamReader (inputstream);

      BufferedReader bufferedreader = new BufferedReader (inputstreamreader);
      StringBuilder stringbuilder = new StringBuilder ();
      String sline;
      while ((sline = bufferedreader.readLine ()) != null)
      {
        stringbuilder.append (sline + "\r\n");
      }

      ret = stringbuilder.toString ();
      //Log.d ("CanTrust", "HttpClient::request ret = " + ret);
      Log.d ("CanTrust", "HttpClient::request Success !!");
      */
    }
    catch (Exception e)
    //catch (RuntimeException e)
    {
      Log.d ("CanTrust", "HttpClient::Exception = " + e.getMessage ());
    }

    return ret;
  }
}
