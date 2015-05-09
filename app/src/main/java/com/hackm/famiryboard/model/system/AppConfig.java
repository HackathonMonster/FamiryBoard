package com.hackm.famiryboard.model.system;

public class AppConfig {

	private AppConfig(){
		//restrict instantiation
	}
	//デバッグ常態化どうかのフラグ
	public static final boolean DEBUG = true;
	public static final boolean REAL_DEVICE = true;

    //Some Activity name
    public static final int ID_ACTIVITY_MAIN = 1001;
    public static final int ID_ACTIVITY_DRAWER = 1002;
    public static final int ID_ACTIVITY_SPLASH = 1003;
    public static final int ID_ACTIVITY_SELECT_CAKE = 1004;
    public static final int ID_ACTIVITY_MAKE_CAKE = 1005;
    public static final int ID_ACTIVITY_CONFILM = 1006;
    public static final int ID_ACTIVITY_SELECT_STAMP_CATEGORY = 1007;
    public static final int ID_ACTIVITY_SELECT_STAMP = 1008;
    public static final int ID_INTENT_CAMERA = 2001;
    public static final int ID_INTENT_GALLERY = 2002;

    //SharedPreference
    public static final String PREF_NAME = "pref_pictcake";
    public static final String PREF_SAVED_IMAGE = "pref_saved_image";
    public static final String PREF_SAVED_COOKIE = "pref_saved_cookie";

    //DialogName
    public static final String TAG_TEXTDECO_DIALOG = "tag_textdeco_dialog";
    public static final String TAG_MESSAGE_DIALOG = "tag_message_dialog";
    public static final String TAG_WEBPAGE_FRAGMENT = "tag_webpage_fragment";

    public static final String DOMAIN_NAME = "";
    public static final String PAGE_COLLECTION = "";
    public static final String PAGE_CART = "";
    public static final String PAGE_CONTACT = "";
    public static final String PAGE_MYPAGE = "";
    public static final String PAGE_QUESTION = "";
    public static final String PAGE_CHANGE_COMPLETE = "";
    public static final String PAGE_TOP = "";
    public static final String PAGE_TOP_INDEX = "";
}