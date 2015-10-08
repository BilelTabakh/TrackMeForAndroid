package com.andy.trackme;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.andy.trackme.adapter.SeanceAdapter;
import com.andy.trackme.entities.Seance;
import com.andy.trackme.sqllite.SeanceBDD;


public class ListStatistiqueFragment extends Fragment implements
		OnItemClickListener {
	SeanceBDD seanceBDD;
	List<Seance> seanceList;
	ListView listViewSeance;
	final static SeanceAdapter adapter=null;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.list_statistique, container,
				false);

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		ColorDrawable colorDrawable = new ColorDrawable(getResources()
				.getColor(R.color.actionBarStatistique));
		activity.getActionBar().setBackgroundDrawable(colorDrawable);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		try{
		seanceList = new ArrayList<Seance>();
		seanceBDD = new SeanceBDD(getActivity().getApplicationContext());
		
		seanceList = seanceBDD.selectAll();
		listViewSeance = (ListView) getActivity().findViewById(
				R.id.listviewStatistique);
		
		
		//Delete from listView
		 ListView listView = listViewSeance;
		 SwipeDismissListViewTouchListener touchListener =
	                new SwipeDismissListViewTouchListener(
	                		listView,
	                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
	                            public boolean canDismiss(int position) {
	                                return true;
	                            }
  
	                            public void onDismiss(ListView listView, final int[] reverseSortedPositions) {
	                               

	                    			final SweetAlertDialog s =new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
	            					s.setTitleText("Suppression");
	            					s.showCancelButton(true);
	            					s.setContentText(
	            							"Supprimer????");
	            					s.setCancelClickListener(
	            							new SweetAlertDialog.OnSweetClickListener() {
	            								@Override
	            								public void onClick(SweetAlertDialog sDialog) {
	            									s.dismiss();
	            								}
	            							});
	            					s.setConfirmClickListener(
	            							new SweetAlertDialog.OnSweetClickListener() {
	            								@Override
	            								public void onClick(SweetAlertDialog sDialog) {
	            	                            	for (int position : reverseSortedPositions) {
	            	                                	Seance seanceToDelete = (Seance) listViewSeance.getItemAtPosition(position);
	            	                                	seanceBDD.removeSeance(seanceToDelete.getId());	
	            	                                }
	            	                            	Fragment lisFragment = new ListStatistiqueFragment();
	            	                            	FragmentTransaction ft  = getFragmentManager().beginTransaction();
	            	                            	ft.replace(R.id.frame_container, lisFragment);
	            	                            	ft.addToBackStack(null);
	            	                            	ft.commit();
	            	                            	s.dismiss();
	            								}
	            							});
	            					s.show();
	            					

	                            }
	                            
	                        }); 
		 
		 listViewSeance.setOnTouchListener(touchListener);
		 listViewSeance.setOnScrollListener(touchListener.makeScrollListener());
		 //End Delete from listView
		
		
		listViewSeance.setAdapter(new SeanceAdapter(getActivity()
				.getBaseContext(), R.layout.list_item_statistique, seanceList));

		listViewSeance.setOnItemClickListener(this);
		
		}catch(Exception e){
			e.getMessage();
		} 
		 
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		@SuppressWarnings("unchecked")
		Seance seance = (Seance) listViewSeance.getItemAtPosition(position);
		
		StatistiqueFragment fragmentStatistique = new StatistiqueFragment();
		//Ajout de données
		Bundle bundle = new Bundle();
		bundle.putInt("DUREE", seance.getDuree());
		bundle.putInt("DISTANCE", seance.getDistance());
		bundle.putString("VITESSE", seance.getVitesse());
		bundle.putInt("CALORIES", seance.getCalories());
		fragmentStatistique.setArguments(bundle);
		//Transaction vers StatistiqueFragment
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragmentStatistique).commit();
		
	}
}
