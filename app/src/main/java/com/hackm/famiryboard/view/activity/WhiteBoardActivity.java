package com.hackm.famiryboard.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.Request;
import com.hackm.famiryboard.controller.provider.NetworkTaskCallback;
import com.hackm.famiryboard.controller.util.ImageUtil;
import com.hackm.famiryboard.controller.util.JSONArrayRequestUtil;
import com.hackm.famiryboard.controller.util.PicassoHelper;
import com.hackm.famiryboard.controller.util.PostPictureRequestUtil;
import com.hackm.famiryboard.controller.util.UriUtil;
import com.hackm.famiryboard.controller.util.VolleyHelper;
import com.hackm.famiryboard.model.enumerate.NetworkTasks;
import com.hackm.famiryboard.model.pojo.FontStyles;
import com.hackm.famiryboard.model.system.Account;
import com.hackm.famiryboard.model.system.AppConfig;
import com.hackm.famiryboard.model.viewobject.DecoImage;
import com.hackm.famiryboard.model.viewobject.DecoText;
import com.hackm.famiryboard.view.fragment.MessageDialogFragment;
import com.hackm.famiryboard.view.fragment.SignalRFragment;
import com.hackm.famiryboard.view.fragment.TextDecoDialogFragment;
import com.hackm.famiryboard.view.widget.WhiteBoardView;
import com.hackm.famiryboard.R;
import com.hackm.famiryboard.model.pojo.Stamp;
import com.hackm.famiryboard.model.viewobject.Deco;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

@EActivity(R.layout.activity_whiteboard)
public class WhiteBoardActivity extends Activity implements TextDecoDialogFragment.OnTextDecoratedListener, WhiteBoardView.OnTextTapListener, SignalRFragment.OnUpdateConnectionListener {

    @Extra("board_id")
    String mBoardId;

    //Main Content
    @ViewById(R.id.make_cakedecoview)
    WhiteBoardView mCakeDevoView;

    //Footer
    @ViewById(R.id.make_imagebutton_camera)
    ImageButton mCameraButton;
    @ViewById(R.id.make_imagebutton_stamp)
    ImageButton mStampButton;
    @ViewById(R.id.make_imagebutton_share)
    ImageButton mShareButton;
    @ViewById(R.id.make_layout_footer)
    LinearLayout mFooterLayout;

    @ViewById(R.id.make_swiperefreshlayout_progress)
    SwipeRefreshLayout mProgressLayout;

    //For GetImageButton's Navigation
    private LayoutInflater mInflater;
    private PopupWindow mFooterPopupMenu;
    //For Camera Intent
    private Uri mImageUri;
    private SignalRFragment mSignalRFragment;

    private DecoImage mNowGettingDecoImage;
    private DecoText mNowGettingDecoText;

    private android.os.Handler mHandler;

    @AfterInject
    void onAfterInject() {
        mSignalRFragment = SignalRFragment.newInstance("ApplicationHub", UriUtil.getSignalrUrl(), mBoardId);
        getFragmentManager().beginTransaction().add(mSignalRFragment, SignalRFragment.class.getSimpleName()).commit();
    }

    @AfterViews
    void onAfterViews() {
        mHandler = new android.os.Handler();
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCakeDevoView.setOnTextTapListener(this);
        //プログレスレイアウトの設定
        mProgressLayout.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2, R.color.refresh_progress_3);
        //ジェスチャを無効にする
        mProgressLayout.setEnabled(false);

        Account account = Account.getAccount(getApplicationContext());
        JSONArrayRequestUtil boardItemRequest = new JSONArrayRequestUtil(new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, Object object) {
                if (mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(false);
                JSONArray jsonArray = (JSONArray) object;
                for(int i=0; i< jsonArray.length(); i++) {
                    try {
                        createDecoItem(jsonArray.getJSONObject(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {
                if (mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(false);
            }
        }, WhiteBoardActivity.class.getSimpleName(),
                account.getAccountHeader());
        boardItemRequest.onRequest(VolleyHelper.getRequestQueue(getApplicationContext()), Request.Priority.HIGH, UriUtil.getItemsUrl(mBoardId), NetworkTasks.GetBoardItems);
        if (!mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //プログレスが動いてたら停止する。
        if (mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(false);
    }

    @Click(R.id.make_imagebutton_camera)
    void clickCamera() {
        if (mFooterPopupMenu == null || !mFooterPopupMenu.isShowing()) {
            View getpicMenuView = mInflater.inflate(R.layout.view_make_pic_menu, null);
            getpicMenuView.findViewById(R.id.make_getpic_layout_camera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runchCamera();
                }
            });
            getpicMenuView.findViewById(R.id.make_getpic_layout_gallery).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runchGallery();
                }
            });
            mFooterPopupMenu = new PopupWindow(getpicMenuView, WindowManager.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.whiteboard_getpic_nav_height));
            if (AppConfig.DEBUG) {
                Log.d(getClass().getSimpleName(), "FooterLayout Height:" + Integer.toString(mFooterLayout.getHeight()));
            }
            mFooterPopupMenu.showAsDropDown(mFooterLayout, 0, (mFooterLayout.getHeight() + getResources().getDimensionPixelSize(R.dimen.whiteboard_getpic_nav_height)) * -1);
            mCameraButton.setImageResource(R.drawable.ic_make_camera_true);
        } else {
            mCameraButton.setImageResource(R.drawable.ic_make_camera_false);
            mFooterPopupMenu.dismiss();
        }
    }

    @Click(R.id.make_imagebutton_stamp)
    void clickStamp() {
        mStampButton.setImageResource(R.drawable.ic_make_stamp_true);
        SelectStampCategoryActivity_.intent(this).startForResult(AppConfig.ID_ACTIVITY_SELECT_STAMP_CATEGORY);
    }

    @Click(R.id.make_imagebutton_text)
    void clickText() {
        if (getFragmentManager().findFragmentByTag(AppConfig.TAG_TEXTDECO_DIALOG) == null) {
            TextDecoDialogFragment dialog = TextDecoDialogFragment.newInstance(new FontStyles(getString(R.string.textdeco_edittext_hint)));
            dialog.setCallback(this);
            dialog.show(getFragmentManager(), AppConfig.TAG_TEXTDECO_DIALOG);
        }
    }

    @Click(R.id. make_imagebutton_share)
    void clickPreview() {
        mShareButton.setImageResource(R.drawable.ic_share_true);
        if (mCakeDevoView.getDecosCount() < 1) {
            if (getFragmentManager().findFragmentByTag(AppConfig.TAG_MESSAGE_DIALOG) == null) {
                MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance(getString(R.string.make_attention_title), getString(R.string.make_attention_message));
                messageDialogFragment.show(getFragmentManager(), AppConfig.TAG_MESSAGE_DIALOG);
            }
            //キャンセル
            return;
        }
        if (!mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(true);
        mShareButton.setImageResource(R.drawable.ic_make_preview_true);
        mShareButton.setEnabled(false);
        mCakeDevoView.removeFrame();
        //背景を白く設定
        mCakeDevoView.setBackgroundColor(Color.WHITE);
        mCakeDevoView.setDrawingCacheEnabled(false);
        mCakeDevoView.setDrawingCacheEnabled(true);
        Bitmap bitmap = transformImage(Bitmap.createBitmap(mCakeDevoView.getDrawingCache()));
        mCakeDevoView.setBackgroundColor(getResources().getColor(R.color.top_cake_background));
        try {
            String imagePath = ImageUtil.saveImage(bitmap, this);
            if (imagePath != null) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                //最初はimage/pngにしておく -> 画像を扱えるアプリたちが出てくる（facebookも出てくる）
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePath));
                startActivity(intent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap transformImage(Bitmap source) {
        int size = (int) (Math.min(source.getWidth(), source.getHeight()) * 27.0f / 32.0f);
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    private void runchCamera() {
        mImageUri = ImageUtil.getPhotoUri(getApplicationContext());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, AppConfig.ID_INTENT_CAMERA);
    }

    private void runchGallery() {
        // ギャラリー呼び出し
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, AppConfig.ID_INTENT_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConfig.ID_INTENT_CAMERA:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = ImageUtil.loadImage(getApplicationContext(), mImageUri, true);
                        postImage(bitmap, Deco.TYPE_CAMERA);
                        if (!mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    closeFooterMenu();
                }
                break;
            case AppConfig.ID_INTENT_GALLERY:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = ImageUtil.loadImage(getApplicationContext(), data.getData(), false);
                        postImage(bitmap, Deco.TYPE_GALLERY);
                        if (!mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    closeFooterMenu();

                }
                break;
            case AppConfig.ID_ACTIVITY_SELECT_STAMP_CATEGORY:
                mStampButton.setImageResource(R.drawable.ic_make_stamp_false);
                if (resultCode == RESULT_OK) {
                    Stamp stamp = new Gson().fromJson(data.getStringExtra("stamp"), Stamp.class);
                    int imageSize = (int) (mCakeDevoView.getWidth() * 0.6f);
                    DecoImage decoImage = new DecoImage(mCakeDevoView.getWidth() / 2, mCakeDevoView.getHeight() / 2, imageSize, imageSize, 0, Deco.TYPE_STAMP, stamp.stamp_url, mBoardId);
                    mSignalRFragment.createItem(decoImage);
                    if (!mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(true);
                }
                break;
            case AppConfig.ID_ACTIVITY_CONFILM:
                mShareButton.setImageResource(R.drawable.ic_make_preview_false);
                mShareButton.setEnabled(true);
                break;
        }
    }

    private void closeFooterMenu() {
        if (mFooterPopupMenu != null && mFooterPopupMenu.isShowing()) {
            mCameraButton.setImageResource(R.drawable.ic_make_camera_false);
            mFooterPopupMenu.dismiss();
        }
    }

    //TextDeco Dialog Callbacks
    @Override
    public void onTextDecorated(Bitmap bitmap, FontStyles fontStyles, int index) {
        DecoText decoText;
        if (index < 0) {
            //マイナスの値なら新しく追加をする
            decoText = new DecoText(mCakeDevoView.getWidth() / 2, mCakeDevoView.getHeight() / 2, bitmap.getWidth(), bitmap.getHeight(), 0, Deco.TYPE_TEXT, mBoardId, fontStyles);
            mSignalRFragment.createItem(decoText);
            mNowGettingDecoText = decoText;
            mNowGettingDecoText.bitmap = bitmap;
        } else {
            decoText = mCakeDevoView.replaceDecoItem(bitmap, Deco.TYPE_TEXT, fontStyles, index, mBoardId);
            //TODO Update Item
            mSignalRFragment.updateItem(decoText);
        }
    }

    @Override
    public void onCanceled() {
    }

    @Override
    public void onTextTaped(FontStyles fontStyles, int index) {
        if (getFragmentManager().findFragmentByTag(AppConfig.TAG_TEXTDECO_DIALOG) == null) {
            TextDecoDialogFragment dialog = TextDecoDialogFragment.newInstance(fontStyles, index);
            dialog.setCallback(this);
            dialog.show(getFragmentManager(), AppConfig.TAG_TEXTDECO_DIALOG);
        }
    }

    /**
     * オブジェクトが動かされた時に呼び出される
     * @param deco
     */
    @Override
    public void onMoved(Deco deco) {
        if (deco instanceof DecoImage) {
            mSignalRFragment.updateItem((DecoImage) deco);
        } else {
            mSignalRFragment.updateItem((DecoText) deco);
        }
    }

    @Override
    public void onDelete(String id) {
        mSignalRFragment.deleteItem(id);
    }

    private Target addStampTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //画像を読み込み完了したので追加
            if (mNowGettingDecoImage != null) {
                mNowGettingDecoImage.bitmap = bitmap;
                mCakeDevoView.addDecoItem(mNowGettingDecoImage);
                mNowGettingDecoImage = null;
            }
            if (mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(false);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Toast.makeText(getApplicationContext(), getString(R.string.faild_stamp_get), Toast.LENGTH_LONG).show();
            if (mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(false);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    private void postImage(final Bitmap bitmap, final int type) {
        PostPictureRequestUtil postPictureRequestUtil = new PostPictureRequestUtil(NetworkTasks.PostPicture, new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, Object object) {
                JSONObject jsonObject = (JSONObject) object;
                String imageUrl = null;
                try {
                    imageUrl = jsonObject.getString("Url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int imageSize = (int) (mCakeDevoView.getWidth() * 0.6f);
                DecoImage decoImage = new DecoImage(mCakeDevoView.getWidth() / 2, mCakeDevoView.getHeight() / 2, imageSize, imageSize, 0, Deco.TYPE_CAMERA, imageUrl, mBoardId);
                mSignalRFragment.createItem(decoImage);
            }

            @Override
            public void onFailedNetworkTask(int taskId, Object object) {
                if (mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(false);
                //TODO Show Dialog
            }
        });
        postPictureRequestUtil.setHeader(new ArrayList<NameValuePair>());
        postPictureRequestUtil.onRequest(UriUtil.postImageUrl(), new ArrayList<NameValuePair>(), bitmap);
    }

    private void createDecoItem(String json) {
        Log.d("CreateDecoItem", json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.getInt("type") != Deco.TYPE_TEXT) {
                final DecoImage decoImage = new DecoImage(
                        jsonObject.getInt("x"),
                        jsonObject.getInt("y"),
                        jsonObject.getInt("width"),
                        jsonObject.getInt("height"),
                        jsonObject.getInt("rotation"),
                        jsonObject.getInt("type"),
                        jsonObject.getString("imageUrl"),
                        jsonObject.getString("boardId"));
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mNowGettingDecoImage = decoImage;
                        int imageSize = (int) (mCakeDevoView.getWidth() * 0.6f);
                        PicassoHelper.with(getApplicationContext()).load(decoImage.imageUrl).resize(imageSize, imageSize).into(addStampTarget);
                    }
                });
            } else {

                JSONObject fontStyleObject = jsonObject.getJSONObject("styles");
                FontStyles fontStyles = new FontStyles(fontStyleObject.getString("text"));
                fontStyles.color = fontStyleObject.getInt("color");
                fontStyles.size = fontStyleObject.getInt("size");
                fontStyles.gravity = fontStyleObject.getInt("gravity");
                fontStyles.style = fontStyleObject.getInt("style");
                fontStyles.typefacePath = fontStyleObject.getString("typefacePath");
                final DecoText decoText = new DecoText(
                        jsonObject.getInt("x"),
                        jsonObject.getInt("y"),
                        jsonObject.getInt("width"),
                        jsonObject.getInt("height"),
                        jsonObject.getInt("rotation"),
                        jsonObject.getInt("type"),
                        jsonObject.getString("boardId"),
                        fontStyles);
                decoText.bitmap = DecoText.createBitmap(decoText.width, decoText.height, decoText.styles);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mNowGettingDecoText != null) {
                            mCakeDevoView.addDecoItem(mNowGettingDecoText);
                            mNowGettingDecoText = null;
                        } else {
                            mCakeDevoView.addDecoItem(decoText);
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 別のところから飛んできた子
     * @param json
     */
    @Override
    public void onCreateItem(String json) {
        createDecoItem(json);
    }

    /**
     * うちの子
     * @param json
     */
    @Override
    public void onCreateSucccess(String json) {
        createDecoItem(json);

    }

    @Override
    public void onUpdateItem(String json) {
        //TODO
    }

    @Override
    public void onDeleteItem(String id) {
        //TODO
    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onConnectionStarted() {

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onConnectionError() {

    }

    @Override
    public void onConnectionClosed() {

    }


}