package com.andy.trackme;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andy.trackme.entities.Seance;
import com.andy.trackme.sqllite.SeanceBDD;


public class HomeFragment extends Fragment {
	
	SeanceBDD seanceBDD;
	List<Seance> seanceList;
	Seance lastSeanceStat;
	
	TextView txtDistanceRec;
	TextView txtDurationRec;
	TextView txtSpeedRec;
	TextView txtCaloriesRec;
	
	public HomeFragment(){}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onActivityCreated(savedInstanceState);	
		
		txtDurationRec = (TextView)getActivity().findViewById(R.id.textViewDurationHome);
		txtDistanceRec = (TextView)getActivity().findViewById(R.id.textViewDistanceHome);
		txtSpeedRec = (TextView)getActivity().findViewById(R.id.textViewSpeedHome);
		txtCaloriesRec = (TextView)getActivity().findViewById(R.id.textViewCaloriesHome);
		
        //Last Stat 
		try{
		seanceList = new ArrayList<Seance>();
		seanceBDD = new SeanceBDD(getActivity().getApplicationContext());
		seanceList = seanceBDD.selectAll();
		lastSeanceStat = seanceList.get((seanceList.size())-1);
		
		
		
			if(!seanceList.equals(null)){
				txtDistanceRec.setText(lastSeanceStat.getDistance()*0.001+" km");
				txtDurationRec.setText(lastSeanceStat.getDuree()+" min");
				txtSpeedRec.setText(lastSeanceStat.getVitesse());
				txtCaloriesRec.setText(lastSeanceStat.getCalories()+" kcal");
			}
			else{
				txtDistanceRec.setText("0 km");
				txtDurationRec.setText("0 min");
				txtSpeedRec.setText("0 min/km");
				txtCaloriesRec.setText("0 kcal");
			}
		}catch(Exception e){
		}
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
      
     
        
        return rootView;
    }
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.actionBarHome));
	    activity.getActionBar().setBackgroundDrawable(colorDrawable);
	}



}
