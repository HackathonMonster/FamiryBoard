package com.hackm.famiryboard.view.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hackm.famiryboard.model.enumerate.DrawerMenu;
import com.hackm.famiryboard.view.adapter.DrawerMenuAdapter;
import com.hackm.famiryboard.R;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_navigation_drawer)
public class NavigationDrawerFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;

    @ViewById(R.id.drawer_layout_head)
    FrameLayout mHeadLayout;
    @ViewById(R.id.drawer_imageview_mood)
    ImageView mMoodImageView;
    @ViewById(R.id.drawer_imageview_icon)
    ImageView mIconImageView;

    @ViewById(R.id.drawer_listview_menu)
    ListView mMenuListView;

    private DrawerMenuAdapter mAdapter;
    private OnDrawerSelectedListener mListener;

    @AfterViews
    void onAfterViews() {
        setMenu();
        ViewGroup.LayoutParams lp;
        lp = mHeadLayout.getLayoutParams();
        lp.height = (int) (getActivity().getResources().getDimensionPixelSize(R.dimen.navigation_drawer_width) / PHOTO_ASPECT_RATIO);
        mHeadLayout.setLayoutParams(lp);

        Picasso.with(getActivity()).load("https://dl.dropboxusercontent.com/u/31455721/mother.jpg").into(mMoodImageView);
    }

    private void setMenu() {
        mAdapter = new DrawerMenuAdapter(getActivity().getApplicationContext());
        mMenuListView.setAdapter(mAdapter);
        mMenuListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == mMenuListView.getId()) {
            if (mListener == null) return;
            if(position == DrawerMenu.Home.id) {
                mListener.onDrawerSelected(DrawerMenu.Home);
            } else if (position == DrawerMenu.Inquiry.id) {
                mListener.onDrawerSelected(DrawerMenu.Inquiry);
            } else if (position == DrawerMenu.Mypage.id) {
                mListener.onDrawerSelected(DrawerMenu.Mypage);
            } else if (position == DrawerMenu.Question.id) {
                mListener.onDrawerSelected(DrawerMenu.Question);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDrawerSelectedListener) activity;
        } catch (ClassCastException e) {
            mListener = null;
        }
    }

    public interface OnDrawerSelectedListener {
        public void onDrawerSelected(DrawerMenu drawerMenu);
    }
}
