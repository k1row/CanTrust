package com.camobile.cantrust.struct;

import java.io.Serializable;

public class GPSItem implements Serializable
{
  public double lat;
  public double lng;
  public String name;

  protected final static String NULL = "NULL";
}
