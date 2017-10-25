package com.wanhuiyuan.szoa.adapter;

import android.support.v4.app.*;
import android.view.ViewGroup;

import java.util.ArrayList;

public class TabFragmentPagerAdapter extends FragmentStatePagerAdapter {

	private ArrayList<Fragment> fragments;
	private FragmentManager fm;
	
	public TabFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		this.fm = fm;
	}
	
	public TabFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
		super(fm);
		this.fm = fm;
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}
	
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
	
	public void setFragments(ArrayList<Fragment> fragments) {
		if (this.fragments != null) {
			FragmentTransaction ft = fm.beginTransaction();
			for (Fragment f : this.fragments) {
				ft.remove(f);
			}
			ft.commit();
			ft = null;
			fm.executePendingTransactions();
		}
		this.fragments = fragments;
		notifyDataSetChanged();
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		Object obj = super.instantiateItem(container, position);
		return obj;
	}
}
