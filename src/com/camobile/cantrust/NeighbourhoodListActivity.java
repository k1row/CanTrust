package com.camobile.cantrust;

import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;

import android.os.Bundle;
import android.os.Handler;

import android.util.Log;

import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ActivityNotFoundException;


import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.Window;

import android.widget.BaseAdapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.camobile.cantrust.util.Util;
import com.camobile.cantrust.util.HttpClient;

import com.camobile.cantrust.struct.GPSItem;
import com.camobile.cantrust.struct.Gnavi;
import com.camobile.cantrust.struct.Tabelog;


public class NeighbourhoodListActivity extends Activity
{
  private int m_target_api;
  private ListAdapter m_list_view;
  private List<GPSItem> m_item_list;

  LocationManager m_locationmanager;
  private LocationListener locationListener;
  private Timer locationTimer;
  long time;

  double m_lat;
  double m_lng;

  static class ViewHolder
  {
    ImageView image;
    TextView name;
    TextView category;
    TextView text;
  }

  @Override
    protected void onCreate(Bundle bundle)
    {
      super.onCreate (bundle);

      // 皇居で初期化でもしておく
      m_lat = 35.685826;
      m_lng = 139.756684;

      // 現在位置の取得
      get_location ();

      // setContentView() より先に記述します
      requestWindowFeature (Window.FEATURE_INDETERMINATE_PROGRESS);

      ListView lv = new ListView (this);
      setContentView (lv);

      setProgressBarIndeterminateVisibility (true);
      setProgressBarIndeterminate (true);

      m_list_view = new ListAdapter (this);
      m_item_list = new ArrayList<GPSItem> ();

      Intent intent = getIntent();
      m_target_api = intent.getIntExtra ("CALL_API", 0);

      if (m_target_api == Const.CALL_GNAVI_API)
        search_gnavi ();
      else if (m_target_api == Const.CALL_TABELOG_API)
        search_tabelog ();
      else
      {
        Util.show_toast (getApplicationContext (), "ERROR : Selected what is not understood. ");
        return;
      }

      lv.setAdapter (m_list_view);

      // ListView がクリックされた時に呼び出されるコールバックを登録
      lv.setOnItemClickListener (new AdapterView.OnItemClickListener() {
        public void onItemClick (AdapterView<?> parent, View view, int position, long id)
        {
          ListView l = (ListView)parent;
          Tabelog data = (Tabelog)l.getItemAtPosition(position);

          if (data == null)
            return;

          // 詳細画面へ
          Intent intent = new Intent ();
          intent.setClassName ("com.camobile.cantrust","com.camobile.cantrust.DetailActivty");
          intent.putExtra ("TABELOG", data);
          intent.setAction (Intent.ACTION_VIEW);
          startActivity (intent);
        }
      });

      setProgressBarIndeterminate (false);
      setProgressBarIndeterminateVisibility (false);
    }

  private void get_location ()
  {
    stop_location_service ();

    //GPSから座標を取得する

    //LocationManagerを取得する
    m_locationmanager = (LocationManager)getSystemService (LOCATION_SERVICE);

    final Criteria criteria = new Criteria ();
    criteria.setBearingRequired (false);	// 方位不要
    criteria.setSpeedRequired (false);	// 速度不要
    criteria.setAltitudeRequired (false);	// 高度不要

    final String provider = m_locationmanager.getBestProvider (criteria, true);
    if (provider == null)
    {
      // 位置情報が有効になっていない場合は、Google Maps アプリライクな [現在地機能を改善] ダイアログを起動します。
      notification_provider ();
      return;
    }

    // 最後に取得できた位置情報が5分以内のものであれば有効とします。
    final Location lastKnownLocation = m_locationmanager.getLastKnownLocation (provider);
    // XXX - 必要により判断の基準を変更してください。
    if (lastKnownLocation != null &&
        (new Date ().getTime () - lastKnownLocation.getTime ()) <= (5 * 60 * 1000L))
    {
      set_location (lastKnownLocation);
      return;
    }

    // Toast の表示と LocationListener の生存時間を決定するタイマーを起動します。
    locationTimer = new Timer (true);
    time = 0L;
    final Handler handler = new Handler ();
    locationTimer.scheduleAtFixedRate (new TimerTask () {
      @Override
        public void run ()
        {
          handler.post(new Runnable() {
            @Override
              public void run()
              {
                if (time == 1000L)
                {
                  //Util.show_toast (getApplicationContext (), "現在地を特定しています。");
                }
                else if (time >= (30 * 1000L))
                {
                  Util.show_toast (getApplicationContext (), "現在地を特定できませんでした。");
                  stop_location_service ();
                }
                time = time + 1000L;
              }
          });
        }
    }, 0L, 1000L);

    // 位置情報の取得を開始します。
    locationListener = new LocationListener () {
      @Override
        public void onLocationChanged (final Location location)
        {
          set_location (location);
        }
      @Override
        public void onProviderDisabled (final String provider) {}
      @Override
        public void onProviderEnabled (final String provider) {}
      @Override
        public void onStatusChanged (final String provider, final int status, final Bundle extras) {}
    };

    m_locationmanager.requestLocationUpdates (provider, 60000, 0, locationListener);
  }

  private void notification_provider ()
  {
    // 位置情報が有効になっていない場合は、Google Maps アプリライクな [現在地機能を改善] ダイアログを起動します。
    new AlertDialog.Builder (this)
      .setTitle ("現在地機能を改善")
        .setMessage ("現在、位置情報は一部有効ではないものがあります。次のように設定すると、もっともすばやく正確に現在地を検出できるようになります:\n\n● 位置情報の設定でGPSとワイヤレスネットワークをオンにする\n\n● Wi-Fiをオンにする")
          .setPositiveButton ("設定", new DialogInterface.OnClickListener () {
            @Override
              public void onClick (final DialogInterface dialog, final int which)
              {
                // 端末の位置情報設定画面へ遷移
                try
                {
                  startActivity (new Intent ("android.settings.LOCATION_SOURCE_SETTINGS"));
                }
                catch (final ActivityNotFoundException e)
                {
                  // 位置情報設定画面がない糞端末の場合は、仕方ないので何もしない
                }
              }
          })
            .setNegativeButton ("スキップ", new DialogInterface.OnClickListener () {
              @Override public void onClick (final DialogInterface dialog, final int which)
              {
                // 何も行わない
              }
            })
              .create ()
                .show ();

    stop_location_service ();
  }

  private void stop_location_service ()
  {
    if (locationTimer != null)
    {
      locationTimer.cancel ();
      locationTimer.purge ();
      locationTimer = null;
    }
    if (m_locationmanager != null)
    {
      if (locationListener != null)
      {
        //リスナを削除する
        m_locationmanager.removeUpdates (locationListener);
        locationListener = null;
      }
      m_locationmanager = null;
    }
  }

  private void set_location (final Location location)
  {
    stop_location_service ();

    //String s = "lat=" + String.valueOf (m_lat) + "lng=" + String.valueOf (m_lng);
    //Util.show_toast (getApplicationContext (), s);

    m_lat = location.getLatitude ();
    m_lng = location.getLongitude ();
  }

  private class ListAdapter extends BaseAdapter
  {
    private LayoutInflater m_layoutInflater;

    public ListAdapter (Context context)
    {
      m_layoutInflater = (LayoutInflater)context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
      public int getCount () { return m_item_list.size (); }
    @Override
      public Object getItem (int position) { return m_item_list.get (position); }
    @Override
      public long getItemId (int position) { return position; }
    @Override
      public View getView (int position, View convertView, ViewGroup parent)
      {
        ViewHolder holder;

        if (convertView == null)
        {
          convertView = m_layoutInflater.inflate (R.layout.neighbourhoodlist, null);
          holder = new ViewHolder ();

          holder.image = (ImageView) convertView.findViewById (R.id.image);
          holder.name = (TextView) convertView.findViewById (R.id.name);
          holder.category = (TextView) convertView.findViewById (R.id.category);
          holder.text = (TextView) convertView.findViewById (R.id.text);

          convertView.setTag (holder);
        }
        else
        {
          holder = (ViewHolder)convertView.getTag ();
        }

        if (m_target_api == Const.CALL_GNAVI_API)
        {
          Gnavi data = (Gnavi)getItem (position);
          set_gnavi_view (data, holder);
        }
        else if (m_target_api == Const.CALL_TABELOG_API)
        {
          Tabelog data = (Tabelog)getItem (position);
          set_tabelog_view (data, holder);
        }

        return convertView;
      }
  }

  private void search_gnavi ()
  {
    Log.d ("CanTrust", "search_gnavi = (" + m_lat + " , " + m_lng + ")");

    m_lat = m_lat - 0.0032361;
    m_lng = m_lng + 0.0032028;

    InputStream is = null;
    String req = "http://" + Const.GNAVI_DOMAIN + Const.GNAVI_RESTSEARCH + "&latitude=" + m_lat + "&longitude=" + m_lng;
    Log.d ("CanTrust", "req = " + req);
    HttpClient http = new HttpClient (req);
    is = http.request ();

    GnaviParser gp = new GnaviParser (is);
    m_item_list = gp.parse ("UTF-8");

    try
    {
      is.close ();
      gp = null;
    }
    catch (IOException e)
    {
      e.printStackTrace ();
    }
  }

  private void set_gnavi_view (Gnavi data, ViewHolder holder)
  {
    // バックグラウンドで画像取得
    GetImageAsyncTask task = new GetImageAsyncTask (getApplicationContext (), holder.image);
    task.execute (data.shop_image);

    holder.name.setText ("[" + Util.regex_lf2space (data.name) + "]");
    holder.category.setText (data.category);
    holder.text.setText (data.pr_short);
  }

  private void search_tabelog ()
  {
    Log.d ("CanTrust", "search_tabelog = (" + m_lat + " , " + m_lng + ")");

    //m_lat = m_lat - 0.0032361;
    //m_lng = m_lng + 0.0032028;

    InputStream is = null;
    String req = "http://" + Const.TABELOG_DOMAIN + Const.TABELOG_RESTSEARCH + "&Latitude=" + (float)m_lat + "&Longitude=" + (float)m_lng;
    Log.d ("CanTrust", "req = " + req);
    HttpClient http = new HttpClient (req);
    is = http.request ();

    TabelogParser tp = new TabelogParser (is);
    m_item_list = tp.parse ("UTF-8");

    try
    {
      is.close ();
      tp = null;
    }
    catch (IOException e)
    {
      e.printStackTrace ();
    }
  }

  private void set_tabelog_view (Tabelog data, ViewHolder holder)
  {
    // バックグラウンドで画像取得
    GetImageAsyncTask task = new GetImageAsyncTask (getApplicationContext (), holder.image);
    task.execute (data.shop_image);

    holder.name.setText ("[" + Util.regex_lf2space (data.name) + "]");
    holder.category.setText (data.category);
    holder.text.setText (data.situation);
  }
}
