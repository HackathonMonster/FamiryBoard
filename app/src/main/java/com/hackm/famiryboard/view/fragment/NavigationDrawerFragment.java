package com.hackm.famiryboard.view.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hackm.famiryboard.model.enumerate.DrawerMenu;
import com.hackm.famiryboard.view.adapter.DrawerMenuAdapter;
import com.hackm.famiryboard.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_navigation_drawer)
public class NavigationDrawerFragment extends Fragment implements AdapterView.OnItemClickListener {

    @ViewById(R.id.drawer_listview_menu)
    ListView mMenuListView;
    @ViewById(R.id.drawer_textview_delivery_year)
    TextView mDeliveryYearTextView;
    @ViewById(R.id.drawer_textview_delivery_day)
    TextView mDeliveryDayTextView;

    private DrawerMenuAdapter mAdapter;
    private OnDrawerSelectedListener mListener;

    @AfterViews
    void onAfterViews() {
        setMenu();
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
            } else if (position == DrawerMenu.Cart.id) {
                mListener.onDrawerSelected(DrawerMenu.Cart);
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

    public void setDeliveryDate(String year, String date) {
        mDeliveryYearTextView.setText(year);
        mDeliveryDayTextView.setText(date);
    }

    public interface OnDrawerSelectedListener {
        public void onDrawerSelected(DrawerMenu drawerMenu);
    }
}
