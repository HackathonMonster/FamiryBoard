package com.hackm.famiryboard.view.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.hackm.famiryboard.view.activity.WhiteBoardActivity_;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

    private Context mContext;

    private String albumImages[] = {
            "https://dl.dropboxusercontent.com/u/31455721/Family_01.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_02.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_03.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_04.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_05.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_06.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_07.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_08.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_09.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_10.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_11.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_12.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_13.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_14.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_15.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_16.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_17.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_18.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_19.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_20.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_21.jpg",
            "https://dl.dropboxusercontent.com/u/31455721/Family_22.jpg"
    };

    @AfterViews
    void onAfterViews() {
        mContext = getActivity();
    }

    @Click(R.id.top_imageview_board)
    void intentBoard() {
        WhiteBoardActivity_.intent(this).mBoardId(AppConfig.BOARD_ID).start();
    }

    @Click(R.id.top_imageview_mothersday)
    void intentMothersBoard() {
        //母の日用のボードに飛ぶ
        WhiteBoardActivity_.intent(this).mBoardId("0082a888-0f06-47f5-8f0b-d0c03b3015d").start();
    }

    private void imageLoading() {
        for (String image : albumImages) {



        }
    }

    private Target addImage = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };




}
