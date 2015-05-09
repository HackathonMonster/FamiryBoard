package com.hackm.famiryboard.controller.util;

import java.util.HashMap;

/**
 * Created by shunhosaka on 15/05/09.
 */
public class SvgUtil {

    private final String TAG_SVG = "svg";
    private StringBuilder mSvgBuilder;

    public SvgUtil() {
        mSvgBuilder = new StringBuilder();
    }

    public String openSvg() {
        return startTag(TAG_SVG, new HashMap<String, String>());
    }

    public String closeSvg() {
        return endTag(TAG_SVG);
    }

    public String startTag(String tagName, HashMap<String, String> tagContent) {
        mSvgBuilder.append("<");
        mSvgBuilder.append(tagName);
        mSvgBuilder.append(" ");
        for (String key : tagContent.keySet()) {
            mSvgBuilder.append(key);
            mSvgBuilder.append("=");
            mSvgBuilder.append("\"");
            mSvgBuilder.append(tagContent.get(key));
            mSvgBuilder.append("\"");
        }
        mSvgBuilder.append(">");
        return mSvgBuilder.toString();
    }

    public String endTag(String tagName) {
        mSvgBuilder.append("</");
        mSvgBuilder.append(tagName);
        mSvgBuilder.append(">");
        return mSvgBuilder.toString();
    }

    public String addTag(String tagName, HashMap<String, String> tagContent) {
        mSvgBuilder.append("<");
        mSvgBuilder.append(tagName);
        mSvgBuilder.append(" ");
        for (String key : tagContent.keySet()) {
            mSvgBuilder.append(key);
            mSvgBuilder.append("=");
            mSvgBuilder.append("\"");
            mSvgBuilder.append(tagContent.get(key));
            mSvgBuilder.append("\"");
        }
        mSvgBuilder.append("/>");
        return mSvgBuilder.toString();
    }

}
