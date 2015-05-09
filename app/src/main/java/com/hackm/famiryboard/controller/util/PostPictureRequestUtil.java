package com.hackm.famiryboard.controller.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.hackm.famiryboard.controller.provider.NetworkTaskCallback;
import com.hackm.famiryboard.model.enumerate.NetworkTasks;
import com.hackm.famiryboard.model.system.AppConfig;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shunhosaka on 15/05/09.
 */
public class PostPictureRequestUtil extends AsyncTask<String, Integer, JSONObject> {
    private NetworkTaskCallback mCallback;
    private Bitmap mBitmap;
    private NetworkTasks mTask;
    private String mUrl;
    private List<NameValuePair> mParams;
    private List<NameValuePair> mHeader = new ArrayList<NameValuePair>();

    public PostPictureRequestUtil(final NetworkTasks task, final NetworkTaskCallback callback) {
        this.mTask = task;
        this.mCallback = callback;
    }

    public void setHeader(List<NameValuePair> header) {
        this.mHeader = header;
    }

    public void onRequest(String url, List<NameValuePair> params,Bitmap bitmap) {
        this.mUrl = url;
        this.mParams = params;
        this.mBitmap = bitmap;
        this.execute();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(mUrl);
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName(HTTP.UTF_8));
        try {
            ByteArrayBody byteArrayBitmap = ImageUtil.toByteArrayBody(mBitmap);
            entity.addPart("pictures", byteArrayBitmap);
            for (NameValuePair param : mParams) {
                entity.addPart(param.getName(), new StringBody(param.getValue(), Charset.forName(HTTP.UTF_8)));
            }
            post.setEntity(entity);
            post.addHeader("User-Agent", "Nexus5,6");
            for (NameValuePair header : mHeader) {
                post.addHeader(header.getName(), header.getValue());
            }
            //post.addHeader("Content-Type", "multipart/form-data");
            HttpResponse response = httpClient.execute(post);
            int status = response.getStatusLine().getStatusCode();

            if (status == HttpStatus.SC_OK) {
                ByteArrayOutputStream oStream = new ByteArrayOutputStream();
                response.getEntity().writeTo(oStream);
                if (AppConfig.DEBUG) {
                    Log.d(this.getClass().getSimpleName(), oStream.toString());
                }
                return new JSONObject(oStream.toString());
            } else {
                Log.d(this.getClass().getSimpleName(), "Status:" + Integer.toString(status));
                ByteArrayOutputStream oStream = new ByteArrayOutputStream();
                response.getEntity().writeTo(oStream);
                if (AppConfig.DEBUG) {
                    Log.d(this.getClass().getSimpleName(), oStream.toString());
                }
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("ERROR", "msg:" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject object) {
        if (object != null) {
            mCallback.onSuccessNetworkTask(mTask.id, object);
        } else {
            mCallback.onFailedNetworkTask(mTask.id, null);
        }
    }

}