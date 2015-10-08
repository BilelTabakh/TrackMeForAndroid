package com.andy.trackme;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlanningFragment extends Fragment {
	
	public PlanningFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_planning, container, false);
         
        return rootView;
    }
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.actionBarPlanning));
	    activity.getActionBar().setBackgroundDrawable(colorDrawable);
	}
	
	
}
