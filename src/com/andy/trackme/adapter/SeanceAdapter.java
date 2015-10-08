package com.andy.trackme.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.andy.trackme.entities.Seance;
import com.andy.trackme.R;

public class SeanceAdapter extends ArrayAdapter<Seance>{
	  public final static String TAG = "Seance";
	  private int resourceId = 0;
	  private LayoutInflater inflater;
	
	  public SeanceAdapter(Context context, int resourceId, List<Seance> seances) {
		    super(context, 0, seances);
		    this.resourceId = resourceId;
		    inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		  }
	 
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {

	    View view;
	
	    TextView textDescription;

	    view = inflater.inflate(resourceId, parent, false);

	    try {
	    	textDescription = (TextView)view.findViewById(R.id.descriptionStat);

	    } catch( ClassCastException e ) {
	      throw e;
	    }
	    
	    Seance item = getItem(position);
	    
	    
	    textDescription.setText("Date: "+item.getDateSeance());


	    return view;
	  }
	  
}
