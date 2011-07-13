package com.camobile.cantrust;

import java.io.FileOutputStream;
import java.io.File;

import java.util.List;

import android.util.Log;

import android.os.Bundle;
import android.os.Environment;
import android.os.Build;

import android.app.Activity;
import android.app.ProgressDialog;


import android.content.Context;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.Configuration;


import android.provider.MediaStore;
import android.provider.MediaStore.Images;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.view.ViewGroup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import android.hardware.Camera;
import android.hardware.Camera.Size;


import com.camobile.cantrust.util.Util;
import com.camobile.cantrust.util.HttpFileUpload;


public class PhotoActivity extends Activity implements SurfaceHolder.Callback
{
  private SurfaceView m_surface_view;
  private SurfaceHolder m_holder;

  protected Camera m_camera;
  private Size m_size;
  private OverLayView m_overlay;

  private static final String SAVE_FOLDER_NAME = "/ct_camera/";

  // プレビュー時に中心に矩形を出したい
  public class OverLayView extends View
  {
    int m_width;
    int m_height;
    protected final Paint m_paint = new Paint ();

    public OverLayView(Context context)
    {
      super (context);
      setDrawingCacheEnabled (true);
      setFocusable (true);

      m_paint.setARGB (255, 255, 255, 255);
      m_paint.setStyle (Paint.Style.STROKE);
    }

    protected void onSizeChanged (int w, int h, int oldw, int oldh)
    {
      //ビューのサイズを取得
      m_width = w;
      m_height = h;
    }

    @Override
      protected void onDraw (Canvas canvas)
      {
        super.onDraw (canvas);

        int left = m_width / 2 - 75;
        int right = m_width / 2 + 75;

        int top = m_height / 2 - 75;
        int botton = m_height / 2 + 75;

        canvas.drawRect (new Rect (left, top, right, botton),  m_paint);
      }
  }

  @Override
    public void onCreate (Bundle savedInstanceState)
    {
      super.onCreate (savedInstanceState);

      getWindow().addFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN);
      requestWindowFeature (Window.FEATURE_NO_TITLE);

      m_surface_view = new SurfaceView (this);
      setContentView (m_surface_view);

      // 中心の矩形をOverLayしておく
      m_overlay = new OverLayView (this);
      addContentView (m_overlay, new ViewGroup.LayoutParams (ViewGroup.LayoutParams.FILL_PARENT,
                                                             ViewGroup.LayoutParams.FILL_PARENT));

      m_holder = m_surface_view.getHolder ();
      m_holder.addCallback (this);

      // これをしないと、setPreviewDisplayで落ちる
      m_holder.setType (SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

  @Override
    public void onRestoreInstanceState (Bundle savedInstanceState)
    {
      super.onRestoreInstanceState (savedInstanceState);
    }

  @Override
    public void onResume()
    {
      Log.e ("CanTrust", "onResume");
      super.onResume ();
    }

  @Override
    public void onSaveInstanceState (Bundle outState)
    {
      super.onSaveInstanceState (outState);
    }

  @Override
    public void onStop ()
    {
      Log.e ("CanTrust", "onStop");
      super.onStop ();
    }

  @Override
    public void surfaceCreated (SurfaceHolder holder)
    {
      try
      {
        // カメラインスタンスの生成
        m_camera = Camera.open ();

        // 撮影可能なサイズをきちんとセットしておく
        Camera.Parameters params = m_camera.getParameters ();
        List<Size> supportedSizes = Reflect.getSupportedPreviewSizes (params);

        if (supportedSizes != null && supportedSizes.size () > 0)
        {
          m_size = supportedSizes.get (0);
          params.setPreviewSize (m_size.width, m_size.height);
          m_camera.setParameters (params);
        }

        // プレビュー設定
        m_camera.setPreviewDisplay (holder);
      }
      catch (Exception e)
      {
        Util.show_toast (getApplicationContext (), "カメラの起動に失敗しました。(reason = " + e.getMessage () + ")");
        finish ();
      }
    }

  @Override
    public void surfaceChanged (SurfaceHolder holder, int format, int w, int h)
    {
      try
      {
        // プレビューを縦にする(setDisplayOrientationの前に必ずstopPreviewを呼ぶらしい)
        m_camera.stopPreview ();

        //Camera.Parameters params = m_camera.getParameters ();
        //params.setPreviewSize (m_size.width, m_size.height);
        //m_camera.setParameters (params);

        // Set orientation
        boolean portrait = is_portrait ();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO)
        {
          // 2.1 and before
          Camera.Parameters params = m_camera.getParameters ();
          if (portrait)
            params.set("orientation", "portrait");
          else
            params.set("orientation", "landscape");

          params.setPreviewSize (m_size.width, m_size.height);
          m_camera.setParameters (params);
        }
        else
        {
          // 2.2 and later
          if (portrait)
            m_camera.setDisplayOrientation (90);
          else
            m_camera.setDisplayOrientation (0);
        }

        // プレビュー設定
        m_camera.startPreview ();
      }
      catch (Exception e)
      {
        Util.show_toast (getApplicationContext (), "ERROR : surfaceChanged (reason = " + e.getMessage () + ")");
        finish ();
      }
    }

  @Override
    public void surfaceDestroyed (SurfaceHolder holder)
    {
      if (m_camera == null)
        return;

      m_camera.stopPreview ();
      m_camera.release ();
      m_camera = null;
    }

  @Override
    public boolean onTouchEvent (MotionEvent event)
    {
      if (event.getAction() == MotionEvent.ACTION_DOWN)
      {
        auto_focus ();

        /* auto_focus実装により下記が不要に
        try
        {
          //m_camera.takePicture (null, null, this);

          // ---------------------------------------------------------------------------
          // http://developer.android.com/reference/android/hardware/Camera.html
          // ---- 上記referenceより
          // After calling this method, you must not call startPreview() or take another picture until the JPEG callback has returned.

          // との事なので、この後にはstartPreview()しない
        }
        catch (Exception e)
        {
          Util.show_toast (getApplicationContext (), "ERROR : onTouchEvent (reason = " + e.getMessage () + ")");
          finish ();
        }
         */
      }
      return true;
    }

  // オートフォーカス
  public void auto_focus ()
  {
    if (m_camera == null)
      return;

    //　オートフォーカスのあと撮影に行くようにコールバック設定
    m_camera.autoFocus (new Camera.AutoFocusCallback () {
      @Override
        public void onAutoFocus (boolean success, Camera camera)
        {
          m_camera.autoFocus (null);
          m_camera.stopPreview ();
          m_camera.setDisplayOrientation (90);

          Thread t = new Thread (new Runnable () {
            public void run ()
            {
              try
              {
                Thread.sleep (1000);
              }
              catch (Exception e)
              {}

              m_camera.takePicture (null, null, m_picture_listener);
            };
          });
          t.start ();
        }
    });
  }

  private Camera.PictureCallback m_picture_listener = new Camera.PictureCallback () {
    @Override
      public void onPictureTaken (byte[] data, Camera camera)
      {
        m_camera.stopPreview ();

        if (data == null)
        {
          Util.show_toast (getApplicationContext (), "データが取得できませんでした");
          m_camera.startPreview ();
          return;
        }

        if (!sdcard_be_written ())
        {
          Util.show_toast (getApplicationContext (), "SDカードが認識できません");
          m_camera.startPreview ();
          return;
        }

        // SDカードへバイトデータを書込み
        FileOutputStream out = null;
        String path = Environment.getExternalStorageDirectory ().getPath ();
        Log.d ("CanTrust", path);

        File dir = new File (path + SAVE_FOLDER_NAME);

        //フォルダが存在しなかった場合にフォルダを作成します。
        if (!dir.exists ())
          dir.mkdir ();

        long date = System.currentTimeMillis ();

        // 他のとかぶらない名前の設定
        String file = path + SAVE_FOLDER_NAME + date +".jpg";
        Log.d ("CanTrust", file);

        try
        {
          // 普通に保存すると、previewだと縦なのに横向きで保存されてしまうので、向きを変えて保存する
          Bitmap tmp_bitmap = BitmapFactory.decodeByteArray (data, 0, data.length);
          int width = tmp_bitmap.getWidth ();
          int height = tmp_bitmap.getHeight ();

          Matrix matrix = new Matrix ();
          matrix.postRotate (90);

          Bitmap bitmap = Bitmap.createBitmap (tmp_bitmap, 0, 0, width, height, matrix, true);
          out = new FileOutputStream (file);
          //out.write (data);
          bitmap.compress (CompressFormat.JPEG, 100, out);
          out.close ();

          try
          {
            //コンテンツ登録（androidギャラリーへの登録）
            ContentValues values = new ContentValues ();
            ContentResolver contentResolver = getApplicationContext ().getContentResolver ();
            values.put (Images.Media.MIME_TYPE, "image/jpeg");
            values.put (Images.Media.DATA, file);
            values.put (Images.Media.SIZE, new File (file).length ());
            values.put (Images.Media.DATE_ADDED, date);
            values.put (Images.Media.DATE_TAKEN, date);
            values.put (Images.Media.DATE_MODIFIED, date);

            contentResolver.insert (MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
          }
          catch (Exception e)
          {
            Util.show_toast (getApplicationContext (), "再起動後に画像が認識されます(reason = " + e.getMessage () + ")");
            e.printStackTrace ();
          }
          finally
          {
            if (out != null)
              out.close ();
          }

          // サーバに画像をアップロード
          //file_upload (file);

          Util.show_toast (getApplicationContext (), "SDカードに保存しました");

          m_camera.setDisplayOrientation (90);
          m_camera.startPreview ();
        }
        catch (Exception e)
        {
          m_camera.release ();
          Util.show_toast (getApplicationContext (), "SDカードに保存に失敗しました(reason = " + e.getMessage () + ")");
          e.printStackTrace ();
        }
      }
  };

  //書き込みができるかどうかを判別する関数
  private boolean sdcard_be_written ()
  {
    String state = Environment.getExternalStorageState ();
    return (Environment.MEDIA_MOUNTED.equals (state));
  }

  private boolean is_portrait ()
  {
    return (getResources ().getConfiguration ().orientation == Configuration.ORIENTATION_PORTRAIT);
  }

  private void file_upload (String file)
  {
    // サーバへ画像のアップロード
    ProgressDialog progressDialog;
    progressDialog = new ProgressDialog (this);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.setMessage("処理を実行中です...");
    progressDialog.setCancelable(true);
    progressDialog.show();
    HttpFileUpload file_upload = new HttpFileUpload (file);
    file_upload.do_upload ();
    progressDialog.dismiss();
  }

  // Bitmapのサイズが大きすぎると落ちるので、max_sizeで調整できるように
  /*
  private Bitmap optimize_save_size (int max_size, byte[] data)
  {
    BitmapFactory.Options option = new BitmapFactory.Options ();

    // サイズだけロードするように設定して
    option.inJustDecodeBounds = true;

    // サイズ情報だけロード
    BitmapFactory.decodeByteArray (data, 0, data.length, option);

    int width = option.outWidth;
    int height = option.outHeight;

    // 大きい辺に合わせて読み込みスケールを調整
    if (max_size < Math.max (width, height))
    {
      int scale = Math.max (width, height) / max_size + 1;
      option.inSampleSize = scale;
    }

    // 調整した値で読み込む
    option.inJustDecodeBounds = false;
    return BitmapFactory.decodeByteArray (data, 0, data.length, option);
  }
   */
}
