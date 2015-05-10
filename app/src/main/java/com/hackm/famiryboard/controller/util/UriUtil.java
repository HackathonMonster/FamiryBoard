package com.hackm.famiryboard.controller.util;

import android.net.Uri;

import com.hackm.famiryboard.model.system.AppConfig;

/**
 * Created by shunhosaka on 2014/12/06.
 */
public class UriUtil {

    static private String baseUrl = AppConfig.DOMAIN_NAME;

    static private Uri.Builder getBaseUri(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.encodedAuthority(baseUrl);
        return builder;
    }

    static public String getSignalrUrl() {
        Uri.Builder builder = getBaseUri();
        builder.path("/");
        return builder.build().toString();
    }

    //TODO
    static public String postImageUrl() {
        Uri.Builder builder = getBaseUri();
        builder.path("/api/images");
        return builder.build().toString();
    }

    static public String getImagesUrl() {
        Uri.Builder builder = getBaseUri();
        builder.path("/api/images");
        return builder.build().toString();
    }

    static public String getItemsUrl(String boardId) {
        Uri.Builder builder = getBaseUri();
        builder.path("/api/items");
        builder.appendQueryParameter("boardId", boardId);
        return builder.build().toString();
    }

    static public String postLoginUrl() {
        Uri.Builder builder = getBaseUri();
        builder.path("/token");
        return builder.build().toString();
    }

    static public String postRegistUrl() {
        Uri.Builder builder = getBaseUri();
        builder.path("/api/Account/Register");
        return builder.build().toString();
    }

    static public String orderCakeUri() {
        Uri.Builder builder = getBaseUri();
        builder.path("/user_data/api.php");
        builder.appendQueryParameter("request", "ordercake");
        return builder.build().toString();
    }

    static public String getStampCategoryUri() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.encodedAuthority("www.pictcake.jp");
        builder.path("/api/stamps/categories.json");
        return builder.build().toString();
    }

    static public String getStampUri(String categoryId) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.encodedAuthority("www.pictcake.jp");
        builder.path("/api/stamps/"+categoryId+".json");
        return builder.build().toString();
    }



}
