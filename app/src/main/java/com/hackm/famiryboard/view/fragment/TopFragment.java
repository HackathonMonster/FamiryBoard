package com.hackm.famiryboard.view.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackm.famiryboard.model.system.AppConfig;
import com.hackm.famiryboard.R;
import com.hackm.famiryboard.view.activity.SelectBoardActivity_;
import com.hackm.famiryboard.view.activity.WebpageActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@EFragment(R.layout.fragment_top)
public class TopFragment extends Fragment {

    @FragmentArg("delivery_day")
    String mDeliveryDay;

    @ViewById(R.id.top_imageview_gallery)
    ImageView mGalleryImageView;
    @ViewById(R.id.top_textView_nav)
    TextView mDeliveryNavTextView;

    private List<Long> mGalleryImagesId = new ArrayList<Long>();
    private int mIndex = 0;

    private AlphaAnimation mFadeoutAnimation;

    private Timer mGalleryTimer;
    private Context mContext;
    private final int SLIDE_SPEED = 2300;

    @AfterViews
    void onAfterViews() {
        mContext = getActivity();

        mFadeoutAnimation = new AlphaAnimation(1, 0.8f);
        mFadeoutAnimation.setDuration(175);
        mGalleryImagesId = new ArrayList<Long>();
        getGalleryImageUris(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        getGalleryImageUris(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(mGalleryImagesId != null && mGalleryImagesId.size() > 0) {
            mIndex = 0;
            startGalleryImage();
        }
        mDeliveryNavTextView.setText(getString(R.string.top_text_date, mDeliveryDay));
    }

    @Override
    public void onResume() {
        super.onResume();
        startGalleryImage();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopGalleryImage();
    }

    private void getGalleryImageUris(Uri uriType) {
        //レコードの取得
        Cursor cursor = getActivity().getContentResolver().query(uriType, null, null, null, null);
        Long id;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                mGalleryImagesId.add(id);
            }
        }
        cursor.close();
    }

    private void startGalleryImage() {
        // タイマーのセット
        mGalleryTimer = new Timer(true);
        mGalleryTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                nextImage();
            }
        }, SLIDE_SPEED, SLIDE_SPEED); // 0.3
    }

    private void stopGalleryImage() {
        if(mGalleryTimer != null) {
            mGalleryTimer.cancel();
            mGalleryTimer = null;
        }
    }

    @UiThread
    void nextImage() {
        if (mIndex >= mGalleryImagesId.size()) {
            mIndex = 0;
        }
        if (mGalleryImagesId.size() > 0) {
            if (getActivity() != null && getActivity().getContentResolver() != null) {
                mGalleryImageView.startAnimation(mFadeoutAnimation);
                mGalleryImageView.setImageBitmap(MediaStore.Images.Thumbnails.getThumbnail(
                        getActivity().getContentResolver(),
                        mGalleryImagesId.get(mIndex),
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        null));
                mIndex++;
            }
        }
    }

    @Click(R.id.top_rippleview_make)
    void translateSelect() {
        SelectBoardActivity_.intent(mContext).start();
    }

    @Click(R.id.top_rippleview_collect)
    void translateCollect() {
        WebpageActivity_.intent(this).mPageUrl(AppConfig.PAGE_COLLECTION).start();
    }

}
