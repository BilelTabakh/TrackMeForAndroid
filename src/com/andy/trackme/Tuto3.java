package com.andy.trackme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;

public class Tuto3 extends Fragment implements OnClickListener{
	ButtonRectangle btnNext;
	RadioGroup radioSexGroup;
	RadioButton radioHommeButton;
	RadioButton radioFemmeButton;
	EditText poids;
	EditText taille;
	SharedPreferences prefTaille;
	SharedPreferences prefPoids;
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
		    View windows = inflater.inflate(R.layout.tuto3, container, false);
	        btnNext = (ButtonRectangle) windows.findViewById(R.id.btnNext);
	        btnNext.setOnClickListener(this);
	        radioSexGroup = (RadioGroup) windows.findViewById(R.id.sexe);
	        radioHommeButton = (RadioButton) windows.findViewById(R.id.homme);
	        radioFemmeButton = (RadioButton) windows.findViewById(R.id.femme);
	        poids = (EditText) windows.findViewById(R.id.poids);
	        taille = (EditText) windows.findViewById(R.id.taille);
	        return windows;
}

	@Override
	public void onClick(View v) {
		if (v.getId() == btnNext.getId()) {
			if(!poids.getText().toString().equals("")&&!taille.getText().toString().equals("")){
			Intent i = new Intent(getActivity(),MainActivity.class);
			prefTaille = getActivity().getApplicationContext().getSharedPreferences("prefTaille", 0);
			prefPoids = getActivity().getApplicationContext().getSharedPreferences("prefPoids", 0);
			Editor editorTaille = prefTaille.edit();
			Editor editorPoids = prefPoids.edit();
			editorTaille.putString("prefTaille",taille.getText().toString());
			editorTaille.putString("prefPoids",poids.getText().toString());
			editorTaille.commit();
			editorPoids.commit();
			startActivity(i);
			getActivity().finish();
			//
		}else{
			Toast.makeText(getActivity(), "Champs Vides", Toast.LENGTH_SHORT).show();
		}
		}
	}

		
	}

