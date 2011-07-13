package com.camobile.cantrust;

public class Hubeny
{
	/*
     * DEG表記の例
     * 新宿駅の場合
     * 緯度：35.689325302714984
     * 経度：139.70051229000092
     *
     * getDistance()で使用している公式は
     * 楕円体の原子，諸公式および定数について（国土地理院測地部）
     * http://vldb.gsi.go.jp/sokuchi/surveycalc/algorithm/ellipse/ellipse.htm
     * を利用
     */

	/*
    //出発地点（DEG表記）
    private double latStart;
    private double lonStart;

    //到着地点（DEG表記）
    private double latEnd;
    private double lonEnd;

    //長半径(WGS84)
    private final double a = 6378137D;

    //扁平率(WGS84)
    private final double f = 1D / 298.257222101D;

    //日本測地系の場合
    //private double a = 6377397.155D;
    //private double f = 1D / 299.152813D;

    public Hubeny() {
    }

    public Hubeny(GeoPoint pointStart, GeoPoint pointEnd) {
        setStartPoint(pointStart);
        setEndPoint(pointEnd);
    }

    public void setStartPoint(GeoPoint point) {
        //GeoPoint型の緯度経度を通常のDEG表記に変換する（100万分の1倍する＝1E6で除算）
        latStart = point.getLatitudeE6() / 1E6;
        lonStart = point.getLongitudeE6() / 1E6;
    }

    public void setEndPoint(GeoPoint point) {
        //GeoPoint型の緯度経度を通常のDEG表記に変換する（100万分の1倍する＝1E6で除算）
        latEnd = point.getLatitudeE6() / 1E6;
        lonEnd = point.getLongitudeE6() / 1E6;
    }

    public double getDistance() {
        //緯度経度をラジアンに変換
        double radLatStart = latStart * Math.PI/180D;
        double radLonStart = lonStart * Math.PI/180D;
        double radLatEnd = latEnd * Math.PI/180D;
        double radLonEnd = lonEnd * Math.PI/180D;

        //二点間の平均緯度（ラジアン）
        double avgLat = (radLatStart + radLatEnd) / 2D;

        //扁平率の逆数
        double F = 1D / f;

        //第一離心率
        double e = (Math.sqrt(2 * F - 1)) / F;

        double W = Math.sqrt(1 - Math.pow(e, 2) * Math.pow(Math.sin(avgLat), 2));

        //子午線曲率半径
        double M = (a*(1 - Math.pow(e, 2))) / Math.pow(W, 3);

        //卯酉線曲率半径
        double N = a / W;

        //2点間の緯度差(ラジアン)
        double dLat = radLatStart - radLatEnd;

        //2点間の経度差(ラジアン)
        double dLon = radLonStart - radLonEnd;

        //2点間の距離（メートル）
        double d = Math.sqrt(Math.pow(M*dLat, 2) + Math.pow(N * Math.cos(avgLat) * dLon, 2));

        return d;
    }
    */
}
