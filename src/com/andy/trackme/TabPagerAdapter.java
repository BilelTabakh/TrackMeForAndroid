package com.andy.trackme;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    public TabPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int i) {
		switch (i) {
        case 0:
            //Fragement for Android Tab
            return new Tuto3();
       /* case 1:
           //Fragment for Ios Tab
            return new Tuto2();
        case 2:
            //Fragment for Windows Tab
            return new Tuto3();*/
        }
		return null;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 1; //No of Tabs
	}



}
