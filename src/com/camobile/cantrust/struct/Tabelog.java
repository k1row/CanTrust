package com.camobile.cantrust.struct;

public class Tabelog extends GPSItem
{
  public final static String ITEM = "Item";
  public final static String RCD = "Rcd";
  public final static String NAME = "RestaurantName";
  public final static String TABELOGURL = "TabelogUrl";
  public final static String TABELOGMOBILEURL = "TabelogMobileUrl";
  public final static String TOTALSCORE = "TotalScore";
  public final static String TASTESCORE = "TasteScore";
  public final static String SERVICESCORE = "ServiceScore";
  public final static String MOODSCORE = "MoodScore";
  public final static String SITUATION = "Situation";
  public final static String DINNERPRICE = "DinnerPrice";
  public final static String LUNCHPRICE = "LunchPrice";
  public final static String CATEGORY = "Category";
  public final static String STATION = "Station";
  public final static String ADDRESS = "Address";
  public final static String TEL = "Tel";
  public final static String BUSINESSHOURS = "BusinessHours";
  public final static String HOLIDAY = "Holiday";
  public final static String LATITUDE = "Latitude";
  public final static String LONGITUDE = "Longitude";

  public String rcd;
  public String totalscore;
  public String tastescore;
  public String servicescore;
  public String moodscore;
  public String situation;
  public String dinnerprice;
  public String lunchprice;
  public String category;
  public String station;
  public String address;
  public String tel;
  public String businesshours;
  public String holiday;

  public String shop_image;

  public Tabelog ()
  {
    rcd = NULL;
    name = NULL;
    totalscore = NULL;
    tastescore = NULL;
    servicescore = NULL;
    moodscore = NULL;
    situation = NULL;
    dinnerprice = NULL;
    lunchprice = NULL;
    category = NULL;
    station = NULL;
    address = NULL;
    tel = NULL;
    businesshours = NULL;
    holiday = NULL;

    shop_image = NULL;
  }
}
