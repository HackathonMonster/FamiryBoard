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

    private Context mContext;

    @AfterViews
    void onAfterViews() {
        mContext = getActivity();

    }


}
