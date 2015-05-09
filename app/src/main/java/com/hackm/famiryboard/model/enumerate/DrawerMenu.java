package com.hackm.famiryboard.model.enumerate;

import com.hackm.famiryboard.R;
import com.hackm.famiryboard.model.system.AppConfig;

/**
 * Created by shunhosaka on 2014/12/10.
 */
public enum DrawerMenu {
    Home(0, R.drawable.ic_menu_home, R.string.nav_menu_home, null),
    Inquiry(2, R.drawable.ic_menu_mail, R.string.nav_menu_mail, AppConfig.PAGE_CONTACT),
    Mypage(3, R.drawable.ic_menu_user, R.string.nav_menu_user, AppConfig.PAGE_MYPAGE),
    Question(4, R.drawable.ic_menu_question, R.string.nav_menu_question, AppConfig.PAGE_QUESTION)
    ;
    public int id;
    public int icon;
    public int text;
    public String url;
    DrawerMenu(int id, int icon, int text, String url) {
        this.id = id;
        this.icon = icon;
        this.text = text;
        this.url = url;
    }
}
