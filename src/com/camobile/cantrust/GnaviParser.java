package com.camobile.cantrust;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.camobile.cantrust.struct.GPSItem;
import com.camobile.cantrust.struct.Gnavi;


public class GnaviParser
{
  private InputStream is;
  private ArrayList<GPSItem> items;
  private XmlPullParser xpp;
  private int eventType;

  public GnaviParser (InputStream is)
  {
    this.is = is;
    this.items = new ArrayList<GPSItem> ();
  }

  public ArrayList<GPSItem> parse (String enc)
  {
    if (is == null)
      return null;

    Gnavi item = null;
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

          if (tag.compareTo (Gnavi.REST) == 0)
            item = new Gnavi ();
          else if (tag.compareTo (Gnavi.NAME) == 0)
            item.name = getText ();
          else if (tag.compareTo (Gnavi.ADDRESS) == 0)
            item.address = getText ();
          else if (tag.compareTo (Gnavi.CATEGORY) == 0)
            item.category = getText ();
          else if (tag.compareTo (Gnavi.LATITUDE) == 0)
            //item.lat = (int)(Double.valueOf (getText ()) * 1e6);
            item.lat = Double.valueOf (getText ());
          else if (tag.compareTo (Gnavi.LONGITUDE) == 0)
            //item.lng = (int)(Double.valueOf (getText ()) * 1e6);
            item.lng = Double.valueOf (getText ());
          else if (tag.compareTo (Gnavi.PR_SHORT) == 0)
            item.pr_short = getText ();
          else if (tag.compareTo (Gnavi.TEL) == 0)
            item.tel = getText ();
          else if (tag.compareTo (Gnavi.URL) == 0)
            item.url = getText ();
          else if(tag.compareTo (Gnavi.SHOP_IMAGE) == 0)
            item.shop_image = getText ();
        }
        else if (eventType == XmlPullParser.END_TAG)
        {
          end_tag = xpp.getName ();
          if (end_tag.compareTo (Gnavi.REST) == 0)
          {
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
}
