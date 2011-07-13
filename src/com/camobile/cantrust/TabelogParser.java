package com.camobile.cantrust;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.camobile.cantrust.util.HttpClient;
import com.camobile.cantrust.struct.GPSItem;
import com.camobile.cantrust.struct.Tabelog;


public class TabelogParser
{
  private InputStream is;
  private ArrayList<GPSItem> items;
  private XmlPullParser xpp;
  private int eventType;

  public TabelogParser (InputStream is)
  {
    this.is = is;
    this.items = new ArrayList<GPSItem> ();
  }

  public ArrayList<GPSItem> parse (String enc)
  {
    if (is == null)
      return null;

    Tabelog item = null;
    String tag = null;
    String end_tag = null;

    try
    {
      XmlPullParserFactory factory = XmlPullParserFactory.newInstance ();
      factory.setNamespaceAware (true);
      this.xpp = factory.newPullParser ();
      xpp.setInput (is, enc);

      this.eventType = xpp.getEventType ();
      while (eventType != XmlPullParser.END_DOCUMENT)
      {
        if (eventType == XmlPullParser.START_TAG)
        {
          tag = xpp.getName ();

          if (tag.compareTo (Tabelog.ITEM) == 0)
            item = new Tabelog ();
          else if (tag.compareTo (Tabelog.RCD) == 0)
            item.rcd = getText ();
          else if (tag.compareTo (Tabelog.NAME) == 0)
            item.name = getText ();
          else if (tag.compareTo (Tabelog.TOTALSCORE) == 0)
            item.totalscore = getText ();
          else if (tag.compareTo (Tabelog.TASTESCORE) == 0)
            item.tastescore = getText ();
          else if (tag.compareTo (Tabelog.SERVICESCORE) == 0)
            item.servicescore = getText ();
          else if (tag.compareTo (Tabelog.MOODSCORE) == 0)
            item.moodscore = getText ();
          else if (tag.compareTo (Tabelog.SITUATION) == 0)
            item.situation = getText ();
          else if (tag.compareTo (Tabelog.DINNERPRICE) == 0)
            item.dinnerprice = getText ();
          else if (tag.compareTo (Tabelog.LUNCHPRICE) == 0)
            item.lunchprice = getText ();
          else if (tag.compareTo (Tabelog.CATEGORY) == 0)
            item.category = getText ();
          else if (tag.compareTo (Tabelog.STATION) == 0)
            item.station = getText ();
          else if (tag.compareTo (Tabelog.ADDRESS) == 0)
            item.address = getText ();
          else if (tag.compareTo (Tabelog.TEL) == 0)
            item.tel = getText ();
          else if (tag.compareTo (Tabelog.BUSINESSHOURS) == 0)
            item.businesshours = getText ();
          else if (tag.compareTo (Tabelog.HOLIDAY) == 0)
            item.holiday = getText ();
          else if (tag.compareTo (Tabelog.LATITUDE) == 0)
            //item.lat = (int)(Double.valueOf (getText ()) * 1e6);
            item.lat = Double.valueOf (getText ());
          else if (tag.compareTo (Tabelog.LONGITUDE) == 0)
            item.lng = Double.valueOf (getText ());
        }
        else if (eventType == XmlPullParser.END_TAG)
        {
          end_tag = xpp.getName ();
          if (end_tag.compareTo (Tabelog.ITEM) == 0)
          {
            // 最後に画像を取得する
            item.shop_image = get_shop_image (item.rcd);

            items.add (item);
            item = null;
          }
        }
        eventType = xpp.next ();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace ();
    }

    return items;
  }

  private String getText () throws XmlPullParserException, IOException
  {
    if (eventType != XmlPullParser.START_TAG)
    {
      eventType = xpp.next ();
      return "UnKnown";
    }

    while (eventType != XmlPullParser.TEXT)
    {
      eventType = xpp.next ();
    }

    return xpp.getText ();
  }

  private String get_shop_image (String rcd)
  {
    String req = "http://" + Const.TABELOG_DOMAIN + Const.TABELOG_IMAGESEARCH + "&Rcd=" + rcd;
    HttpClient http = new HttpClient (req);
    is = http.request ();

    TabelogImageParser tip = new TabelogImageParser (is);
    return tip.parse ("UTF-8");
  }
}
