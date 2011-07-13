package com.camobile.cantrust;

public class Const
{
  public static final int CALL_GNAVI_API = 1;
  public static final int CALL_TABELOG_API = 2;

  /* ぐるなび設定 */
  public static final String GNAVI_KEY = "f64f8e143f4bcb41ca4fc4371798947a";
  public static final String GNAVI_DOMAIN = "api.gnavi.co.jp";
  public static final String GNAVI_RESTSEARCH = "/ver1/RestSearchAPI/?keyid=" + GNAVI_KEY + "&range=2&hit_per_page=5";

  /* 食べログ設定 */
  public static final String TABELOG_KEY = "cd25dd0950f240669ea5eb4bb61c8fec2047c512";
  public static final String TABELOG_DOMAIN = "api.tabelog.com";
  public static final String TABELOG_RESTSEARCH = "/Ver2.1/RestaurantSearch/?Key=" + TABELOG_KEY + "&PageNum=5&ResultSet=large";
  public static final String TABELOG_IMAGESEARCH = "/Ver1/ReviewImageSearch/?Key=" + TABELOG_KEY;

  /* Facebook設定 */
  public static final String FACEBOOK_APP_ID = "118970374857806";
  public static final String FACEBOOK_API_KEY = "9da5e78953c2b12d9824c9b6cf6200d5";
  public static final String FACEBOOK_APP_SECRET = "183202606ae324385886e38edd0dee01";

}
