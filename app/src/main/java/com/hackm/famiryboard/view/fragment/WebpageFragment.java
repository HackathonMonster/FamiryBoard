package com.hackm.famiryboard.view.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hackm.famiryboard.controller.util.CookiePreferencesUtil;
import com.hackm.famiryboard.model.system.AppConfig;
import com.hackm.famiryboard.view.activity.MainActivity;
import com.hackm.famiryboard.R;
import com.hackm.famiryboard.view.activity.MainActivity_;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_webpage)
public class WebpageFragment extends Fragment {

    @FragmentArg("page_url")
    String mPageUrl;
    @ViewById(R.id.webpage_webview)
    WebView mWebView;

    private boolean mIsMainActivity = false;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mIsMainActivity = activity instanceof MainActivity_ || activity instanceof MainActivity;
    }

    @AfterInject
    void onAfterInjects() {
        if (mPageUrl == null) return;
    }

    @AfterViews
    void onAfterView() {
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if(mIsMainActivity) {
                    if (url.equals(AppConfig.PAGE_TOP) || url.contains(AppConfig.PAGE_TOP_INDEX) || url.contains(AppConfig.PAGE_CHANGE_COMPLETE)) {
                        ((MainActivity_) getActivity()).backHome();
                        return ;
                    }
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains(AppConfig.DOMAIN_NAME)) {
                    if (AppConfig.DEBUG) {
                        Log.d(this.getClass().getSimpleName(), "URL:" + url);
                    }
                    // Cookieを取得
                    String[] cookies = CookieManager.getInstance().getCookie(url).split(";");
                    for (String cookie : cookies) {
                        if (cookie.contains("ECSESSID=")) {
                            if (getActivity() != null && !isDetached()) {
                                CookiePreferencesUtil.setCookie(getActivity(), cookie);
                            }
                        }
                    }
                }
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (mPageUrl != null) {
            mWebView.loadUrl(mPageUrl);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieSyncManager.createInstance(getActivity());

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeExpiredCookie();
        cookieManager.removeSessionCookie();
        setCookie(cookieManager);

    }

    @Override
    public void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().startSync();
    }

    @Override
    public void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
    }

    private void setCookie(CookieManager cookieManager) {
        cookieManager.setCookie(AppConfig.DOMAIN_NAME, CookiePreferencesUtil.getCookie(getActivity()));
        CookieSyncManager.getInstance().sync();
    }

    public boolean isGoBackPage() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

    public void reloadPage() {
        mWebView.reload();
    }

}
