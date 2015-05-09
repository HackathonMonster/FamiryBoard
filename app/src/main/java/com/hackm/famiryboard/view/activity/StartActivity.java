package com.hackm.famiryboard.view.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.hackm.famiryboard.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NoTitle;

@NoTitle
@EActivity(R.layout.activity_start)
public class StartActivity extends ActionBarActivity {

    @Click(R.id.start_button_login)
    void clickLogin() {
        LoginActivity_.intent(this).start();
    }

    @Click(R.id.start_button_regist)
    void clickRegist() {
        RegistActivity_.intent(this).start();
    }
}
