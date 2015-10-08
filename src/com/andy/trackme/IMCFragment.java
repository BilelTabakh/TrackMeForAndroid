package com.andy.trackme;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.gc.materialdesign.views.ButtonRectangle;


public class IMCFragment extends Fragment implements OnClickListener{
    private SharedPreferences prefPoids;   
    private SharedPreferences prefTaille; 
    private String poids;
    private String taille;
    EditText editTaille;
    EditText editPoids;
    TextView textIMC;
    ButtonRectangle btnModifyInfo;
    
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
		    View rootView = inflater.inflate(R.layout.imc_fragment, container, false);
		   try{
		    editPoids = (EditText) rootView.findViewById(R.id.editPoids);
		    editTaille = (EditText) rootView.findViewById(R.id.editTaille);
		    
		    textIMC = (TextView) rootView.findViewById(R.id.textIMC);
		    
		    btnModifyInfo = (ButtonRectangle) rootView.findViewById(R.id.btnModifyInfo); 
		    btnModifyInfo.setOnClickListener(this);
		    
			prefPoids  = getActivity().getSharedPreferences("prefTaille",0);
			poids  = prefPoids.getString("prefPoids","")+""; 
			editPoids.setText(poids+"");
			
			prefTaille  = getActivity().getSharedPreferences("prefTaille",0);
			taille  = prefTaille.getString("prefTaille","")+"";
			editTaille.setText(taille+"");
			
			int taille1 = Integer.parseInt(taille);
			int poids1 = Integer.parseInt(poids);
			
			double taille2 = (double)taille1/100;
			double tailleCarre = taille2*taille2;
			double imc = poids1/tailleCarre;
			imc = (double)((int)(imc*100))/100;
			textIMC.setText("IMC "+imc);
		   }catch(Exception e){
			   Editor editor = prefPoids.edit();
				editor.putString("prefPoids","0");
				editor.commit();
				
				Editor editor1 = prefTaille.edit();
				editor1.putString("prefTaille","0");
				editor1.commit();
		   }
	        return rootView;
}
	 
	 @Override
		public void onAttach(Activity activity) {
			// TODO Auto-generated method stub
			super.onAttach(activity);
			ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.actionBarInfos));
		    activity.getActionBar().setBackgroundDrawable(colorDrawable);
		}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnModifyInfo){
			try{
			Editor editor = prefPoids.edit();
			editor.putString("prefPoids",editPoids.getText().toString());
			editor.commit();
			
			Editor editor1 = prefTaille.edit();
			editor1.putString("prefTaille",editTaille.getText().toString());
			editor1.commit();
			final SweetAlertDialog s =new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
			s.setTitleText("Modifié!");
			s.setContentText(
					"Vos nouvelles données seront prises en compte lors de votre prochaine séance");
			s.setConfirmClickListener(
					new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sDialog) {
							s.dismiss();
						}
					});
			s.show();
			int taille1 = Integer.parseInt(editTaille.getText().toString());
			int poids1 = Integer.parseInt(editPoids.getText().toString());
			
			double taille2 = (double)taille1/100;
			double tailleCarre = taille2*taille2;
			double imc = poids1/tailleCarre;
			imc = (double)((int)(imc*100))/100;
			textIMC.setText("IMC "+imc);
			}catch(Exception e){
				
			}
		}
		
	}
}
