package com.camobile.cantrust;

import java.util.List;
import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.*;


public class PinItemizedOverlay extends ItemizedOverlay<OverlayItem>
{
  private List<OverlayItem> items = new ArrayList<OverlayItem>();

  public PinItemizedOverlay (Drawable defaultMarker)
  {
    super (boundCenterBottom (defaultMarker));
  }

  @Override
    protected OverlayItem createItem (int i)
    {
      return items.get (i);
    }

  @Override
    public int size ()
    {
      return items.size();
    }

   public void add_pin (GeoPoint point, String title, String snippet)
   {
     items.add (new OverlayItem (point, title, snippet));
     populate ();
  }
}
