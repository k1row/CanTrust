package com.camobile.cantrust.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.xmlpull.v1.XmlPullParserException;


public class Web
{
  public static int authUser (HttpClient client, String domain, String path,
                              int port, String name, String pass) throws HttpException,
                                                                         IOException {
    if (client == null)
      return -1;

    client.getHostConfiguration ().setHost (domain, port, "http");
    client.getState().setCredentials (new AuthScope (domain, 80),
                                      new UsernamePasswordCredentials (name, pass));
    GetMethod method = new GetMethod (path);
    method.getParams().setParameter (HttpMethodParams.RETRY_HANDLER,
                                     new DefaultHttpMethodRetryHandler (1, false));

    return client.executeMethod (method);
  }

  public static InputStream getHttpStream (String domain, String path, int port)
  {
    HttpClient client = new HttpClient ();
    client.getHostConfiguration ().setHost (domain, port, "http");

    GetMethod method = new GetMethod (path);
    method.getParams ().setParameter (HttpMethodParams.RETRY_HANDLER,
                                      new DefaultHttpMethodRetryHandler (1, false));

    int status = -1;
    try
    {
      status = client.executeMethod (method);
    }
    catch (Exception e)
    {
      //e.printStackTrace();
      return null;
    }

    if(status != 200)
      return null;

    try
    {
      return method.getResponseBodyAsStream ();
    }
    catch (IOException e)
    {
      return null;
    }
  }

  public static InputStream getHttpStream (String domain, String path)
  {
    return getHttpStream (domain, path, 80);
  }

  /**
   * クライアント（client)でpathのコンテンツを取得する
   *
   * @param client
   *            既にクライアントが設定されている必要がある
   * @param path
   * @return
   * @throws XmlPullParserException
   * @throws HttpException
   * @throws IOException
   */
  public static InputStream getStream (HttpClient client, String path)
    throws XmlPullParserException, HttpException, IOException
  {
    if (client == null)
      return null;

    GetMethod method = new GetMethod (path);
    method.getParams().setParameter (HttpMethodParams.RETRY_HANDLER,
                                     new DefaultHttpMethodRetryHandler (1, false));
    int status = client.executeMethod (method);
    if (status != 200)
      return null;

    return method.getResponseBodyAsStream ();
  }

  /**
   * クライアント（client)でpathのコンテンツを取得する
   *
   * @param client
   *            既にクライアントが設定されている必要がある
   * @param path
   * @return
   * @throws IOException
   * @throws XmlPullParserException
   * @throws IOException
   */
  public static String get (HttpClient client, String path) throws IOException
  {
    if (client == null)
      return null;

    GetMethod method = new GetMethod (path);
    method.getParams ().setParameter (HttpMethodParams.RETRY_HANDLER,
                                      new DefaultHttpMethodRetryHandler (1, false));
    int status = client.executeMethod (method);
    if (status != 200)
      return null;

    return method.getResponseBodyAsString ();
  }

  public static String post (HttpClient client, String path,
                             Map<String, String> hash) throws IOException
  {
    PostMethod method = new PostMethod (path);
    Set<Entry<String, String>> entry = hash.entrySet ();
    Entry<String, String> obj;
    Iterator<Entry<String, String>> iterator = entry.iterator ();

    while (iterator.hasNext ())
    {
      obj = iterator.next ();
      String key = obj.getKey ();
      String val = obj.getValue ();
      method.setParameter (key, val);
    }

    method.getParams ().setParameter (HttpMethodParams.RETRY_HANDLER,
                                      new DefaultHttpMethodRetryHandler (1, false));
    int status = client.executeMethod (method);
    if (status != 200)
      return null;

    return method.getResponseBodyAsString();
  }
}
