package com.camobile.cantrust;

import com.google.android.maps.*;

import java.util.List;
import java.util.Date;

import android.os.Bundle;
import android.util.Log;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;


//import android.location.LocationManager;
//import android.location.Location;

//import android.widget.Toast;

//import android.content.DialogInterface;
import android.content.Intent;
//import android.content.ActivityNotFoundException;
//import android.app.AlertDialog;


import android.view.MotionEvent;
import android.view.Window;

import com.camobile.cantrust.struct.GPSItem;



public class Map extends MapActivity
{
  private static final String APP_ID = "0foLGf5ln8_jy1hhHYaXv5cjmhjSdHriZNNmpsQ";
  private static final int INITIAL_ZOOM_LEVE = 16;

  private MapView m_map_view;
  private MapController m_mc;
  //private MyLocationOverlay m_overlay;

  private double m_lat;
  private double m_lng;

  public Map () {}
  public Map (double _lat, double _lng)
  {
    this.m_lat = _lat;
    this.m_lng = _lng;
  }

  @Override
    public void onCreate (Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);

      // setContentView() より先に記述します
      requestWindowFeature (Window.FEATURE_INDETERMINATE_PROGRESS);

      m_map_view = new MapView (this, APP_ID);
      setContentView (m_map_view);

      setProgressBarIndeterminateVisibility (true);
      setProgressBarIndeterminate (true);

      m_map_view.setClickable (true);
      m_map_view.setBuiltInZoomControls (true);
      m_mc = m_map_view.getController ();
      m_mc.setZoom (INITIAL_ZOOM_LEVE);

      // Intentで緯度経度が渡されたらそれが優先
      {
        Intent intent = getIntent ();
        if (intent == null)
          return;

        GPSItem data = (GPSItem)intent.getSerializableExtra ("GPSITEM");
        if (data == null)
          return;

        m_lat = data.lat;
        m_lng = data.lng;
      }

      m_mc.setCenter (new GeoPoint ((int)(m_lat * 1E6), (int)(m_lng * 1E6)));

      /*
      // GPS を使用する
      m_overlay = new MyLocationOverlay (getApplicationContext (), m_map_view);
      m_overlay.onProviderEnabled (LocationManager.GPS_PROVIDER);
      m_overlay.enableMyLocation ();
      m_overlay.runOnFirstFix (new Runnable () {
        @Override
          public void run ()
          {
            // 現在位置を自動追尾する
            //m_map_view.getController ().animateTo (m_overlay.getMyLocation ());
          }
      });
      m_map_view.getOverlays ().add (m_overlay);
      m_map_view.invalidate ();
        */

      add_pin ();

      setProgressBarIndeterminate (false);
      setProgressBarIndeterminateVisibility (false);
    }

  @Override
    protected void onResume ()
    {
      super.onResume ();
    }

  @Override
    protected void onPause ()
    {
      super.onPause ();
    }

  @Override
    protected boolean isRouteDisplayed ()
    {
      return false;
    }

  @Override
    public boolean onTouchEvent (MotionEvent event)
    {
      /*
      String action = "";
      switch (event.getAction ())
      {
      case MotionEvent.ACTION_DOWN:
        action = "ACTION_DOWN";
        break;
      case MotionEvent.ACTION_UP:
        action = "ACTION_UP";
        break;
      case MotionEvent.ACTION_MOVE:
        action = "ACTION_MOVE";
        break;
      case MotionEvent.ACTION_CANCEL:
        action = "ACTION_CANCEL";
        break;
      }

      String msg  = "action = " + action + ", " +
        "x = " + String.valueOf (event.getX ()) + ", " +
          "y = " + String.valueOf (event.getY ());

      Util.show_toast (getApplicationContext(), msg);
       */
      return super.onTouchEvent (event);
    }

  void setOverlay (GeoPoint point)
  {
    // アイコン取得
    Bitmap icon = BitmapFactory.decodeResource (getResources(), R.drawable.icon);

    // Overlayクラスを生成する
    IconOverlay overlay = new IconOverlay (icon, point);

    // OverlayクラスをMapViewクラスに追加する
    List<Overlay> overlays = m_map_view.getOverlays();
    overlays.add (overlay);
  }

  // 地図上に表示されるオーバーレイ
  private class IconOverlay extends Overlay
  {
    // 描画するアイコン
    Bitmap mIcon;
    int mOffsetX;
    int mOffsetY;

    // アイコンを表示する位置
    GeoPoint mPoint;

    // 地図のタップ時に呼び出される
    @Override
      public boolean onTap (GeoPoint point, MapView mapView)
      {
        // タップされた位置を記録する
        mPoint = point;
        Log.d ("CanTrust", "Point = " + point.getLatitudeE6 () + " , " + point.getLongitudeE6 ());
        return super.onTap (point, mapView);
      }

    // 地図の描画時に、shadow=true, shadow=falseと2回呼び出される
    @Override
      public void draw (Canvas canvas, MapView mapView, boolean shadow)
      {
        super.draw (canvas, mapView, shadow);
        if (!shadow)
        {
          // 地図上の場所と、描画用のCanvasの座標の変換
          Projection projection = mapView.getProjection ();
          Point point = new Point ();
          projection.toPixels (mPoint, point);
          point.offset (mOffsetX, mOffsetY);

          // アイコンを描画
          canvas.drawBitmap (mIcon, point.x, point.y, null);
        }
      }

    IconOverlay (Bitmap icon, GeoPoint initial)
    {
      // アイコンと、アイコンの中心のオフセット
      mIcon = icon;
      mOffsetX = 0 - icon.getWidth () / 2;
      mOffsetY = 0 - icon.getHeight () / 2;
      mPoint = initial;
    }
  };

  private void add_pin ()
  {
    GeoPoint point = new GeoPoint ((int)(m_lat * 1E6), (int)(m_lng * 1E6));
    Drawable pin = getResources ().getDrawable (R.drawable.pin);
    PinItemizedOverlay overlay = new PinItemizedOverlay (pin) {
      @Override
        protected boolean onTap(int index)
        {
          /*
          OverlayItem item = (OverlayItem)getItem(index);
          TextView title = (TextView)findViewById(R.id.text_location_title);
          title.setText(item.getTitle());
          TextView snippet = (TextView)findViewById(R.id.text_location_snippet);
          snippet.setText(item.getSnippet());
           */
          return super.onTap(index);
        }
    };

    m_map_view.getOverlays ().add (overlay);
    overlay.add_pin (point,
                     "位置情報:" + new Date ().getTime (),
                     "緯度:" + m_lat + "経度:" + m_lng);

    m_mc.setCenter (point);
    m_map_view.invalidate ();
  }
}
