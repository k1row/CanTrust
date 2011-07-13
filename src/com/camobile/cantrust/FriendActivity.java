package com.camobile.cantrust;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import java.util.List;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import android.content.Intent;
import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.LayoutInflater;

import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;

import com.camobile.cantrust.util.Util;


public class FriendActivity extends Activity
{
  private FriendListAdapter m_list_view;
  private List<Friend> m_item_list;

  private Facebook m_facebook;
  private AsyncFacebookRunner m_facebook_runner;

  private static class Friend
  {
    String id;
    String name;
    String img_url;

    Friend (String _id, String _name)
    {
      this.id = _id;
      this.name = _name;
      this.img_url = "https://graph.facebook.com/" + id + "/picture";
    }
  }
  private static class ViewHolder
  {
    //ImageView image;
    TextView name;
  }

  @Override
    protected void onCreate(Bundle bundle)
    {
      super.onCreate (bundle);

      requestWindowFeature (Window.FEATURE_INDETERMINATE_PROGRESS);

      setContentView (R.layout.friend);
      setProgressBarIndeterminateVisibility (true);
      setProgressBarIndeterminate (true);

      m_item_list = new ArrayList<Friend> ();
      connect_facebook ();

      setProgressBarIndeterminate (false);
      setProgressBarIndeterminateVisibility (false);
    }

  @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data)
    {
      super.onActivityResult (requestCode, resultCode, data);
      m_facebook.authorizeCallback (requestCode, resultCode, data);
    }

  private void connect_facebook ()
  {
    m_facebook = new Facebook (Const.FACEBOOK_APP_ID);
    m_facebook.authorize (this, new LoginListener ());
  }

  public class LoginListener implements DialogListener
  {
    @Override
      public void onComplete (Bundle values)
      {
        m_facebook_runner = new AsyncFacebookRunner (m_facebook);
        m_facebook_runner.request ("me/friends", new FriendsRequestListener ());
      }

    @Override
      public void onFacebookError (FacebookError e)
      {
        String str = "authorize : Facebook Error:" + e.getMessage ();
        Log.e ("Facebook", str);
        Util.show_toast (getApplicationContext (), str);
      }

    @Override
      public void onError (DialogError e)
      {
        String str = "authorize : Error:" + e.getMessage ();
        Log.e ("Facebook", str);
        Util.show_toast (getApplicationContext (), str);
      }

    @Override
      public void onCancel () {}
  }

  public class FriendsRequestListener implements com.facebook.android.AsyncFacebookRunner.RequestListener
  {
    @Override
      public void onComplete(String response, final Object state)
      {
        try
        {
          Log.d ("Facebook", "Friends-Request : response.length(): " + response.length ());
          Log.d ("Facebook", "Friends-Request : Response: " + response);

          final JSONObject json = new JSONObject (response);
          JSONArray d = json.getJSONArray ("data");
          int l = (d != null ? d.length () : 0);
          Log.d ("Facebook-Friends-Request", "d.length (): " + l);

          for (int i = 0; i < l; i++)
          {
            JSONObject o = d.getJSONObject (i);

            String id = o.getString ("id");
            String name = o.getString ("name");

            m_item_list.add (new Friend (id, name));
          }

          FriendActivity.this.runOnUiThread (new Runnable () {
            public void run ()
            {
              ListView lv = new ListView (FriendActivity.this);
              setContentView (lv);
              m_list_view = new FriendListAdapter (FriendActivity.this);
              lv.setAdapter (m_list_view);
              m_list_view.notifyDataSetChanged ();
            }
          });
        }
        catch (JSONException e)
        {
          Log.d ("Facebook", "Friends-Request : JSON Error in response");
        }
      }

    @Override
      public void onFacebookError (FacebookError e, final Object state)
      {
        String str = "Friends-Request : Facebook Error:" + e.getMessage ();
        Log.e ("Facebook", str);
        Util.show_toast (getApplicationContext (), str);
      }

    @Override
      public void onFileNotFoundException (FileNotFoundException e, final Object state)
      {
        String str = "Friends-Request : Resource not found:" + e.getMessage ();
        Log.e ("Facebook", str);
        Util.show_toast (getApplicationContext (), str);
      }

    @Override
      public void onIOException (IOException e, final Object state)
      {
        String str = "Friends-Request : Network Error:" + e.getMessage ();
        Log.e ("Facebook", str);
        Util.show_toast (getApplicationContext (), str);
      }

    @Override
      public void onMalformedURLException (MalformedURLException e, final Object state)
      {
        String str = "Friends-Request : Invalid URL:" + e.getMessage ();
        Log.e ("Facebook", str);
        Util.show_toast (getApplicationContext (), str);
      }
  }

  private class FriendListAdapter extends BaseAdapter
  {
    private LayoutInflater m_layoutInflater;

    public FriendListAdapter (Context context)
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
          convertView = m_layoutInflater.inflate (R.layout.friend, null);
          holder = new ViewHolder ();

          //holder.image = (ImageView) convertView.findViewById (R.id.image);
          holder.name = (TextView) convertView.findViewById (R.id.name);

          convertView.setTag (holder);
        }
        else
        {
          holder = (ViewHolder)convertView.getTag ();
        }

        Friend data = (Friend)getItem (position);

        // バックグラウンドで画像取得
        ImageView iv = (ImageView) convertView.findViewById (R.id.image);
        //GetImageAsyncTask task = new GetImageAsyncTask (getApplicationContext (), holder.image);
        GetImageAsyncTask task = new GetImageAsyncTask (getApplicationContext (), iv);
        task.execute (data.img_url);

        holder.name.setText (data.name);
        return convertView;
      }
  }
}
