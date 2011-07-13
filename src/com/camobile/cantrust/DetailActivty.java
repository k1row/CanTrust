package com.camobile.cantrust;

import android.app.Activity;
import android.os.Bundle;
//import android.util.Log;
import android.net.Uri;

import android.content.Intent;

import android.view.View;
import android.view.Window;

import android.widget.TextView;
import android.widget.ImageButton;

import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;


//import com.camobile.cantrust.util.Util;

//import com.camobile.cantrust.struct.GPSItem;
//import com.camobile.cantrust.struct.Gnavi;
import com.camobile.cantrust.struct.Tabelog;


public class DetailActivty extends Activity
{
  private Tabelog m_data;

  @Override
    protected void onCreate(Bundle bundle)
    {
      super.onCreate (bundle);

      // setContentView() より先に記述します
      requestWindowFeature (Window.FEATURE_INDETERMINATE_PROGRESS);

      setContentView (R.layout.detail);

      setProgressBarIndeterminateVisibility (true);
      setProgressBarIndeterminate (true);

      Intent intent = getIntent ();
      if (intent == null)
        return;

      m_data = (Tabelog)intent.getSerializableExtra ("TABELOG");
      if (m_data == null)
        return;

      set_tabelog_item ();

      setProgressBarIndeterminate (false);
      setProgressBarIndeterminateVisibility (false);
    }

  private void set_tabelog_item ()
  {
    // 店舗名
    TextView name = (TextView)findViewById (R.id.name);
    name.setText (m_data.name);

    // 電話番号
    TextView tel = (TextView)findViewById (R.id.tel);
    tel.setText ( m_data.tel);

    // 電話掛けるボタン
    ImageButton button = (ImageButton)findViewById (R.id.telephone);
    button.setOnClickListener (onClickListener);

    TextView address = (TextView)findViewById (R.id.address);

    // 住所（クリックするとマップへ）
    String str_address = "■住所\n" + m_data.address + "\n";

    // クリッカブルにする為にSpannableStringとClickableSpanを使う
    SpannableString span_address = new SpannableString (str_address);
    span_address.setSpan (new ClickAddress (), 4, str_address.length (), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

    // ベタテキストのかわりに、SpannableStringテキストをセット
    address.setText (span_address);
    // ClickableSpan.onClick()を呼び出すための決まり文句みたい
    address.setMovementMethod (LinkMovementMethod.getInstance ());

    // 最寄駅
    TextView station = (TextView)findViewById (R.id.station);
    station.setText ("■最寄駅\n" + m_data.station + "\n");

    // その他情報
    TextView info = (TextView)findViewById (R.id.info);
    String str = "■店舗情報\n";
    str += m_data.category + "\n";
    str += "\n";
    str += "昼　：" + m_data.lunchprice + "\n";
    str += "夜　：" + m_data.dinnerprice + "\n";
    str += "\n";
    str += "■営業時間/休日など" + "\n";
    str += m_data.businesshours + "\n";
    str += m_data.holiday;
    info.setText (str);
  }

  // 住所のクリック時に呼ばれる
  private class ClickAddress extends ClickableSpan
  {
    @Override
      public void onClick (View widget)
      {
        // Map画面へ
        Intent intent = new Intent ();
        intent.setClassName ("com.camobile.cantrust","com.camobile.cantrust.Map");
        intent.putExtra ("GPSITEM", m_data);
        intent.setAction (Intent.ACTION_VIEW);
        startActivity (intent);
      }
  }

  private View.OnClickListener onClickListener = new View.OnClickListener ()
  {
    @Override
      public void onClick (View button)
      {
        TextView tel = (TextView)findViewById (R.id.tel);
        String str =  tel.getText ().toString ();
        Intent intent = new Intent (Intent.ACTION_CALL, Uri.parse ("tel:" + str));
        startActivity (intent);
      }
  };
}
