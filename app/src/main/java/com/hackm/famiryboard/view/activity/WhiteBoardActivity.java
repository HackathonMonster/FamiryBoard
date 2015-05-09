package com.hackm.famiryboard.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.hackm.famiryboard.controller.util.ImageUtil;
import com.hackm.famiryboard.controller.util.PicassoHelper;
import com.hackm.famiryboard.model.pojo.FontStyles;
import com.hackm.famiryboard.model.system.AppConfig;
import com.hackm.famiryboard.view.fragment.MessageDialogFragment;
import com.hackm.famiryboard.view.fragment.SignalRFragment;
import com.hackm.famiryboard.view.fragment.TextDecoDialogFragment;
import com.hackm.famiryboard.view.widget.WhiteBoardView;
import com.hackm.famiryboard.R;
import com.hackm.famiryboard.model.pojo.Cake;
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

import java.io.IOException;

@EActivity(R.layout.activity_whiteboard)
public class WhiteBoardActivity extends Activity implements TextDecoDialogFragment.OnTextDecoratedListener, WhiteBoardView.OnTextTapListener, SignalRFragment.OnUpdateConnectionListener {

    @Extra("cake_json")
    String mCakeJson;
    //Get by select cake activity
    private Cake mCake;

    //Main Content
    @ViewById(R.id.make_cakedecoview)
    WhiteBoardView mCakeDevoView;
    @ViewById(R.id.make_imageview_frame)
    ImageView mFrameImageView;

    //Footer
    @ViewById(R.id.make_imagebutton_camera)
    ImageButton mCameraButton;
    @ViewById(R.id.make_imagebutton_stamp)
    ImageButton mStampButton;
    @ViewById(R.id.make_imagebutton_preview)
    ImageButton mPreviewButton;
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

    private String mNowGettingImageUrl;

    @AfterInject
    void onAfterInject() {
        if (mCakeJson != null) {
            mCake = new Gson().fromJson(mCakeJson, Cake.class);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSignalRFragment = SignalRFragment.newInstance("ApplicationHub", "http://carnation.azurewebsites.net/");
        getFragmentManager().beginTransaction().add(mSignalRFragment, SignalRFragment.class.getSimpleName()).commit();
    }

    @AfterViews
    void onAfterViews() {
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mCake != null) {
            //Set image frame cake type. 0->plain 1->choco
            mFrameImageView.setImageResource(mCake.type%2 == 1 ? R.drawable.img_make_frame_plain : R.drawable.img_make_frame_choco);
        }
        mCakeDevoView.setOnTextTapListener(this);

        //プログレスレイアウトの設定
        mProgressLayout.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2, R.color.refresh_progress_3);
        //ジェスチャを無効にする
        mProgressLayout.setEnabled(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //プログレスが動いてたら停止する。
        if (mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(false);
    }

    @Click(R.id.make_imagebutton_camera)
    void clickCamera() {
        if(mFooterPopupMenu == null || !mFooterPopupMenu.isShowing()) {
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
            mFooterPopupMenu = new PopupWindow(getpicMenuView, WindowManager.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.make_cake_getpic_nav_height));
            if (AppConfig.DEBUG) {
                Log.d(getClass().getSimpleName(),"FooterLayout Height:"+Integer.toString(mFooterLayout.getHeight()));
            }
            mFooterPopupMenu.showAsDropDown(mFooterLayout, 0, (mFooterLayout.getHeight() + getResources().getDimensionPixelSize(R.dimen.make_cake_getpic_nav_height)) * -1);
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

    @Click(R.id.make_imagebutton_preview)
    void clickPreview() {
        if (mCakeDevoView.getDecosCount() < 1) {
            if (getFragmentManager().findFragmentByTag(AppConfig.TAG_MESSAGE_DIALOG) == null) {
                MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance(getString(R.string.make_attention_title), getString(R.string.make_attention_message));
                messageDialogFragment.show(getFragmentManager(), AppConfig.TAG_MESSAGE_DIALOG);
            }
            //キャンセル
            return;
        }
        if (!mProgressLayout.isRefreshing()) mProgressLayout.setRefreshing(true);

        mPreviewButton.setImageResource(R.drawable.ic_make_preview_true);
        mPreviewButton.setEnabled(false);

        mCakeDevoView.removeFrame();
        //背景を白く設定
        mCakeDevoView.setBackgroundColor(Color.WHITE);
        mCakeDevoView.setDrawingCacheEnabled(false);
        mCakeDevoView.setDrawingCacheEnabled(true);
        Bitmap bitmap = transformImage(Bitmap.createBitmap(mCakeDevoView.getDrawingCache()));
        mCakeDevoView.setBackgroundColor(getResources().getColor(R.color.top_cake_background));
        SharedPreferences.Editor editor = getSharedPreferences(AppConfig.PREF_NAME, MODE_PRIVATE).edit();
        editor.putString(AppConfig.PREF_SAVED_IMAGE, ImageUtil.encodeImageBase64(bitmap));
        editor.commit();

        ConfilmActivity_.intent(this).mImageString(null).mCakeJson(mCakeJson).startForResult(AppConfig.ID_ACTIVITY_CONFILM);
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
                if(resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = ImageUtil.loadImage(getApplicationContext(), mImageUri, true);
                        mCakeDevoView.addDecoImageItem(bitmap, "",Deco.TYPE_CAMERA);
                        //TODO
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    closeFooterMenu();
                }
                break;
            case AppConfig.ID_INTENT_GALLERY:
                if(resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = ImageUtil.loadImage(getApplicationContext(), data.getData(), false);
                        mCakeDevoView.addDecoImageItem(bitmap, "",Deco.TYPE_GALLERY);
                        //TODO
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    closeFooterMenu();
                }
                break;
            case AppConfig.ID_ACTIVITY_SELECT_STAMP_CATEGORY:
                mStampButton.setImageResource(R.drawable.ic_make_stamp_false);
                if(resultCode == RESULT_OK) {
                    Stamp stamp = new Gson().fromJson(data.getStringExtra("stamp"), Stamp.class);
                    int imageSize = (int) (mCakeDevoView.getWidth() * 0.8f);
                    mNowGettingImageUrl = stamp.stamp_url;
                    PicassoHelper.with(getApplicationContext()).load(stamp.stamp_url).priority(Picasso.Priority.HIGH).skipMemoryCache().resize(imageSize, imageSize).into(addStampTarget);
                }
                break;
            case AppConfig.ID_ACTIVITY_CONFILM:
                mPreviewButton.setImageResource(R.drawable.ic_make_preview_false);
                mPreviewButton.setEnabled(true);
                break;
        }
    }

    private void closeFooterMenu() {
        if(mFooterPopupMenu!=null && mFooterPopupMenu.isShowing()) {
            mCameraButton.setImageResource(R.drawable.ic_make_camera_false);
            mFooterPopupMenu.dismiss();
        }
    }

    //TextDeco Dialog Callbacks
    @Override
    public void onTextDecorated(Bitmap bitmap, FontStyles fontStyles, int index) {
        if (index < 0) {
            //マイナスの値なら新しく追加をする
            mCakeDevoView.addDecoTextItem(bitmap, Deco.TYPE_TEXT, fontStyles);
            //TODO
            mSignalRFragment.sendMessage("Add Text" + fontStyles.text);
        } else {
            mCakeDevoView.replaceDecoItem(bitmap, Deco.TYPE_TEXT, fontStyles, index);
            //TODO
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

    private Target addStampTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //画像を読み込み完了したので追加
            if (mNowGettingImageUrl != null) {
                mCakeDevoView.addDecoImageItem(bitmap, mNowGettingImageUrl, Deco.TYPE_STAMP);
                mNowGettingImageUrl = null;
            }
            // TODO Update imageUrl
            mSignalRFragment.sendMessage("Add Stamp");
        }
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Toast.makeText(getApplicationContext(), getString(R.string.faild_stamp_get), Toast.LENGTH_LONG).show();
        }
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    private void uploadImage() {

    }


    @Override
    public void onMessageReceived(String message) {
        Log.d(WhiteBoardActivity.class.getSimpleName(), message);
    }

    @Override
    public void onMessageSented() {

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