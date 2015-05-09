package com.hackm.famiryboard.controller.provider;

/**
 * Created by shunhosaka on 2015/01/12.
 */

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.hackm.famiryboard.model.system.AppConfig;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

public class MultipartRequest extends Request<String> {

    private MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    private HttpEntity mEntity;

    private final Response.Listener<String> mListener;
    private final Map<String, String> mStringParts;
    private final Map<String, File> mFileParts;

    private Priority mPriority = Priority.LOW;

    public MultipartRequest(String url, Response.Listener<String> listener,
                            Response.ErrorListener errorListener,
                            final Map<String, String> stringParts, final Map<String, File> fileParts) {
        super(Method.POST, url, errorListener);

        mListener = listener;
        mStringParts = stringParts;
        mFileParts = fileParts;
        buildMultipartEntity();
    }

    private void buildMultipartEntity() {
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(Charset.forName("UTF-8"));
        final ContentType textContentType = ContentType.create("text/plain", "UTF-8");
        for (Map.Entry<String, String> entry : mStringParts.entrySet()) {
            builder.addTextBody(entry.getKey(), entry.getValue());
            if (AppConfig.DEBUG) {
                Log.d("MultiPartRequest", "Key:" + entry.getKey() + " Value:" +entry.getValue());
            }
        }
        for (Map.Entry<String, File> entry : mFileParts.entrySet()) {
            builder.addPart(entry.getKey(), new FileBody(entry.getValue()));
        }
        mEntity = builder.build();
    }

    @Override
    public String getBodyContentType() {
        return mEntity.getContentType().getValue();
    }


    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mEntity.writeTo(bos);
        } catch (IOException e) {
            Log.d("MultipartRequest", "IOException writing to ByteArrayOutputStream");
        }
        Log.d("MultipartRequest", bos.toString());
        return bos.toByteArray();
    }


    public HttpEntity getEntity() {
        return mEntity;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String responseString = null;
        try {
            responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    /**
     * 優先順位を設定する
     * @param priority 優先順位設定
     */
    public void setPriority(final Priority priority) {
        this.mPriority = priority;
    }
    /**
     * 現在の優先順位を返却する
     * @return 優先順位
     */
    public Priority getPriority() {
        return mPriority;
    }

}