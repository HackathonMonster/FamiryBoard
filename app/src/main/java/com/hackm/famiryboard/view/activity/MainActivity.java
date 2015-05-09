package com.hackm.famiryboard.view.activity;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.hackm.famiryboard.model.enumerate.DrawerMenu;
import com.hackm.famiryboard.R;
import com.hackm.famiryboard.view.fragment.NavigationDrawerFragment_;
import com.hackm.famiryboard.view.fragment.TopFragment_;
import com.hackm.famiryboard.view.fragment.WebpageFragment_;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment_.OnDrawerSelectedListener{

    private Calendar mDeliveryDay;

    @ViewById(R.id.toolbar_actionbar)
    Toolbar mActionBarToolbar;
    @ViewById(R.id.main_drawer_layout)
    DrawerLayout mDrawerLayout;
    private NavigationDrawerFragment_  mNavigationDrawerFragment;

    @AfterViews
    void onAfterViews() {
        mNavigationDrawerFragment = (NavigationDrawerFragment_) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        setActionBarToolbar();
        onDrawerSelected(DrawerMenu.Home);
    }

    private void setFragment(DrawerMenu drawerMenu) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (drawerMenu == DrawerMenu.Home) {
            if (false) {
                getSupportFragmentManager().popBackStack(drawerMenu.name(), 0);
            } else {
                Fragment fragment = TopFragment_.builder().build();
                transaction.replace(R.id.main_layout_content, fragment);
                transaction.commit();
            }
        } else {
            Fragment fragment = WebpageFragment_.builder().mPageUrl(drawerMenu.url).build();
            transaction.replace(R.id.main_layout_content, fragment);
            transaction.addToBackStack(drawerMenu.name());
            transaction.commit();
        }
    }


    public void backHome() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final FragmentManager fragmentManager = getSupportFragmentManager();
                while (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStackImmediate();
                }
            }
        });
    }

    private Toolbar setActionBarToolbar() {
        if (mActionBarToolbar != null) {
            mActionBarToolbar.setTitle("");
            setSupportActionBar(mActionBarToolbar);
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            });
        }
        return mActionBarToolbar;
    }

    @Override
    public void onDrawerSelected(DrawerMenu drawerMenu) {
        setFragment(drawerMenu);
        if (drawerMenu == DrawerMenu.Home) {
            //Set ActionBar
            mActionBarToolbar.setBackgroundColor(Color.TRANSPARENT);
            mActionBarToolbar.setNavigationIcon(R.drawable.ic_toolbar_menu);
        } else {
            //Set ActionBar
            mActionBarToolbar.setBackgroundColor(getResources().getColor(R.color.actionbar_background));
            mActionBarToolbar.setNavigationIcon(R.drawable.ic_toolbar_menu_web);
        }
        if (mDrawerLayout.isShown()) {
            mDrawerLayout.closeDrawer(Gravity.START);
        }
    }

    private String getDeliveryDay(boolean isDrawer) {
        StringBuilder builderText = new StringBuilder();
        if (isDrawer) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
            builderText.append(simpleDateFormat.format(mDeliveryDay.getTime()));
            builderText.append("(");
            builderText.append(getDayofWeek(mDeliveryDay.get(Calendar.DAY_OF_WEEK)));
            builderText.append(")");
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy'年'MM'月'dd'日'");
            builderText.append(simpleDateFormat.format(mDeliveryDay.getTime()));
            builderText.append("(");
            builderText.append(getDayofWeek(mDeliveryDay.get(Calendar.DAY_OF_WEEK)));
            builderText.append("曜日");
            builderText.append(")");
        }
        return builderText.toString();
    }

    private String getDayofWeek(int week) {
        switch (week) {
            case Calendar.SUNDAY:
                return "日";
            case Calendar.MONDAY:
                return "月";
            case Calendar.TUESDAY:
                return "火";
            case Calendar.WEDNESDAY:
                return "水";
            case Calendar.THURSDAY:
                return "木";
            case Calendar.FRIDAY:
                return "金";
            case Calendar.SATURDAY:
                return "土";
        }
        return "";
    }

}