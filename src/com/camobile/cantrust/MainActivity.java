package com.camobile.cantrust;

import android.app.TabActivity;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.view.Window;

import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;



public class MainActivity extends TabActivity implements TabHost.TabContentFactory
{
  private TabHost m_tab;

  @Override
    public void onCreate (Bundle savedInstanceState)
    {
      super.onCreate (savedInstanceState);

      // setContentView() より先に記述します
      requestWindowFeature (Window.FEATURE_INDETERMINATE_PROGRESS);

      setContentView (R.layout.main);

      setProgressBarIndeterminateVisibility (true);
      setProgressBarIndeterminate (true);

      m_tab = getTabHost ();

      // Tab1の設定
      {
        Intent intent = new Intent ();
        intent.setClassName ("com.camobile.cantrust","com.camobile.cantrust.NeighbourhoodListActivity");
        intent.putExtra ("CALL_API", Const.CALL_TABELOG_API);
        TabSpec t = m_tab.newTabSpec ("NeighbourhoodList")
          .setIndicator ("お店", getResources ().getDrawable (R.drawable.tabelog))
            .setContent (intent);
        m_tab.addTab (t);
      }

      // Tab2の設定
      {
        Intent intent = new Intent ();
        intent.setClassName ("com.camobile.cantrust","com.camobile.cantrust.PhotoActivity");
        TabSpec t = m_tab.newTabSpec ("Photo")
          .setIndicator ("カメラ", getResources ().getDrawable (R.drawable.camera))
            .setContent (intent);
        m_tab.addTab (t);
      }

      // Tab3の設定
      {
        Intent intent = new Intent ();
        intent.setClassName ("com.camobile.cantrust","com.camobile.cantrust.FriendActivity");
        TabSpec t = m_tab.newTabSpec ("Friend")
          .setIndicator ("友達", getResources ().getDrawable (R.drawable.friend))
            .setContent (intent);
        m_tab.addTab (t);
      }

      m_tab.setCurrentTab (0);

      setProgressBarIndeterminate (false);
      setProgressBarIndeterminateVisibility (false);
    }

  public View createTabContent (String tag)
  {
    final TextView tv = new TextView (this);
    tv.setText ("This is " + tag);
    return tv;
  }
}
