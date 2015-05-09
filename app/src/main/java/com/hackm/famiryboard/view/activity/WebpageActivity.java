package com.hackm.famiryboard.view.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import com.hackm.famiryboard.model.system.AppConfig;
import com.hackm.famiryboard.R;
import com.hackm.famiryboard.view.fragment.WebpageFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_webpage)
public class WebpageActivity extends ActionBarActivity {

    @Extra("page_url")
    String mPageUrl;
    @ViewById(R.id.toolbar_actionbar)
    Toolbar mActionBarToolbar;

    @AfterViews
    void onAfterViews() {
        if (mPageUrl == null) {
            finish();
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = WebpageFragment_.builder().mPageUrl(mPageUrl).build();
            transaction.replace(R.id.webpage_layout_content, fragment, AppConfig.TAG_WEBPAGE_FRAGMENT);
            transaction.commit();
        }
        setActionBarToolbar();
    }

    private Toolbar setActionBarToolbar() {
        if (mActionBarToolbar != null) {
            mActionBarToolbar.setTitle("");
            setSupportActionBar(mActionBarToolbar);
            mActionBarToolbar.setNavigationIcon(R.drawable.ic_toolbar_close);
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        return mActionBarToolbar;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebpageFragment_ fragment = (WebpageFragment_) getSupportFragmentManager().findFragmentByTag(AppConfig.TAG_WEBPAGE_FRAGMENT);
        if (keyCode == KeyEvent.KEYCODE_BACK && fragment!=null && fragment.isGoBackPage()) return true;
        return super.onKeyDown(keyCode, event);
    }

    @Click(R.id.webpage_imageview_logo)
    void clickImageLogo() {
        WebpageFragment_ fragment = (WebpageFragment_) getSupportFragmentManager().findFragmentByTag(AppConfig.TAG_WEBPAGE_FRAGMENT);
        if (fragment != null) {
            fragment.reloadPage();
        }
    }

}
