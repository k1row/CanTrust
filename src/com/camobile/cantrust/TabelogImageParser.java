package com.camobile.cantrust;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;



public class TabelogImageParser
{
  private InputStream is;
  private XmlPullParser xpp;
  private int eventType;

  public TabelogImageParser (InputStream is)
  {
    this.is = is;
  }

  public String parse (String enc)
  {
    if (is == null)
      return null;

    String tag = null;
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
          if (tag.compareTo ("ImageUrlS") == 0)
            return getText ();
        }
        eventType = xpp.next ();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace ();
    }

    return "UnKnown";
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
