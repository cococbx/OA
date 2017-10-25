package com.wanhuiyuan.szoa.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by dada on 2016/4/17.
 */
public class MainPageAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener, View.OnClickListener {
    public MainPageAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	private Context context;
    private ViewPager viewPager;
    private LinearLayout tabGroup;
    private Map<String, Fragment> fragmentMap = new HashMap<String, Fragment>();
    private ArrayList<MainPageAdapter.TabObject> tabObjects;
    private ArrayList<MainPageAdapter.TabInfo> tabs;


    private void init() {
        tabs = new ArrayList<MainPageAdapter.TabInfo>();
        fragmentMap = new HashMap();
        tabObjects = new ArrayList();
        for (int i = 0; i < tabGroup.getChildCount(); i++) {
            View view = tabGroup.getChildAt(i);
            MainPageAdapter.TabObject tabObject = new MainPageAdapter.TabObject();
            if (view instanceof RelativeLayout) {
                CheckBox checkBox = (CheckBox) ((RelativeLayout) view).getChildAt(0);
                checkBox.setTag(i);
                checkBox.setOnClickListener(this);
                ImageView redPoint = (ImageView) ((RelativeLayout) view).getChildAt(1);
                tabObject.setCheckBox(checkBox);
                tabObject.setRedPoint(redPoint);
                tabObjects.add(tabObject);
            } else if (view instanceof CheckBox) {
                tabObject.setCheckBox((CheckBox) view);
                view.setTag(i);
                view.setOnClickListener(this);
                tabObjects.add(tabObject);
            }
        }
    }
    public void addTab(Class<?> clazz, Bundle args) {
        TabInfo tabInfo = new TabInfo(clazz, args);
        tabs.add(tabInfo);
        notifyDataSetChanged();
    }

    public Fragment getItem(int position) {
        MainPageAdapter.TabInfo info = tabs.get(position);
        Fragment fragment = Fragment.instantiate(context, info.getClazz().getName(), info.getArgs());
        fragmentMap.put(info.getClazz().getName(), fragment);
        return fragment;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    public void onPageSelected(int position) {
        clearCheckedState(tabObjects.get(position).getCheckBox());
    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public int getCount() {
        return tabs == null ? 0 : tabs.size();
    }

    @Override
    public void onClick(View v) {
        viewPager.setCurrentItem(Integer.parseInt(v.getTag().toString()));
        clearCheckedState((CheckBox) v);
    }

    //设置红点状态
    public void showRedPoint(int position, boolean isVisible) {
        tabObjects.get(position).getRedPoint().setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    //更具类名获得Frament
    public Fragment getFragment(String className) {
        return fragmentMap != null ? fragmentMap.get(className) : null;
    }


    private void clearCheckedState(CheckBox checkBox) {
        for (int i = 0; i < tabObjects.size(); i++) {
            tabObjects.get(i).getCheckBox().setChecked(false);
        }
        checkBox.setChecked(true);
    }

    class TabObject {
        private CheckBox checkBox;
        private ImageView redPoint;

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        public ImageView getRedPoint() {
            return redPoint;
        }

        public void setRedPoint(ImageView redPoint) {
            this.redPoint = redPoint;
        }
    }

    class TabInfo {
        private Class<?> clazz;
        private Bundle args;

        public TabInfo(Class<?> clazz, Bundle args) {
            this.clazz = clazz;
            this.args = args;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Bundle getArgs() {
            return args;
        }

        public void setArgs(Bundle args) {
            this.args = args;
        }
    }

}
