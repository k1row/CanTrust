package com.camobile.cantrust.struct;


public class Gnavi extends GPSItem
{
  public final static String REST = "rest";
  public final static String NAME = "name";
  public final static String LATITUDE = "latitude";
  public final static String LONGITUDE = "longitude";
  public final static String CATEGORY = "category";
  public final static String URL = "url";
  public final static String SHOP_IMAGE = "shop_image";
  public final static String ADDRESS = "address";
  public final static String TEL = "tel";
  public final static String PR_SHORT = "pr_short";

  public String category;
  public String url;
  public String shop_image;
  public String address;
  public String tel;
  public String pr_short;

  public Gnavi ()
  {
    name = NULL;
    url = NULL;
    shop_image = NULL;
    address = NULL;
    tel = NULL;
    pr_short = NULL;
  }
}
