package com.camobile.cantrust.util;


import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.http.HttpStatus;
import org.apache.http.HttpResponse;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpPost;

import android.util.Log;


public class HttpFileUpload
{
  private static final String m_upload_url = "http://node.k1row.dotcloud.com/";
  private String m_file_path;

  public HttpFileUpload (String _file_path)
  {
    this.m_file_path = _file_path;
  }

  //アップロード実行
  //private boolean do_upload ()
  public void do_upload ()
  {
    //boolean res = false;

    // UIスレッドとは別スレッドでアップロードを実行
    //new Thread (new Runnable () {
      //public void run ()
      //{
        try
        {
          // サーバ接続
          //HttpClient httpClient = new DefaultHttpClient ();
          DefaultHttpClient httpClient = new DefaultHttpClient ();
          HttpPost request = new HttpPost (m_upload_url);

          MultipartEntity entity = new MultipartEntity ();

          // 送信画像ファイルを Form にセット
          entity.addPart ("img_data", new FileBody (new File (m_file_path)));
          request.setEntity (entity);

          // 送信
          HttpResponse response = httpClient.execute (request);
          int status = response.getStatusLine ().getStatusCode ();

          if (status != HttpStatus.SC_OK)
          {
            // 送信エラー
            Log.d ("CanTrust", "ERROR : do_upload ():HttpStatus:" + status);
          }
          else
          {
            // レスポンス受信
            ByteArrayOutputStream stream = new ByteArrayOutputStream ();
            response.getEntity ().writeTo (stream);
            String stream_msg = stream.toString ();

            if (stream_msg.indexOf ("ERROR") == -1)
              //res = true;  // 送信成功
              Log.d ("CanTrust", "SUCCESS");
            else
              Log.d ("CanTrust", "ERROR : do_upload ():" + stream_msg);
          }
        }
        catch (Exception e)
        {
          Log.d ("CanTrust", "ERROR : do_upload ():" + e.getMessage ().toString ());
        }
      //}
    //}).start ();

    //return res;
  }
}
