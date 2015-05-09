package com.hackm.famiryboard.model.enumerate;

/**
 * Created by shunhosaka on 2015/01/21.
 */
public enum  TrackingEvents {
    Conversion("android", "appconvesion", "アプリコンバージョン"),
    LoginApp("android","loginapp", "アプリ内ログイン"),
    GoCart("android","gocart", "カート遷移"),
    Cartin("android","cartin", "カートに入れる"),
    Collection("android","collection","コレクションから選ぶ"),
    Makecake("android","makecake","ピクトケーキ製作"),
    Login("android","login", "ログイン"),
    ;
    public String category;
    public String id;
    public String name;
    TrackingEvents(String category,String id, String name){
        this.category = category;
        this.id = id;
        this.name = name;
    }
}
