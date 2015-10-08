package com.andy.trackme;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.gc.materialdesign.views.ButtonRectangle;

public class PompesFragment extends Fragment implements SensorEventListener,
		OnClickListener {
	TextView proxText;
	//TextView textBestScore;
	SensorManager sm;
	Sensor proxSensor;
	int nbrPompes = -1;
	ButtonRectangle btnReset;
	//SharedPreferences prefBest;
	int best;
	//public MediaPlayer mediaPlayer;

	// TextToSpeech tts;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.activity_pompes, container,
				false);

		btnReset = (ButtonRectangle) rootView.findViewById(R.id.btnReset);
		//mediaPlayer.setVolume(1.0f, 1.0f);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		btnReset.setOnClickListener(this);
		//prefBest = getActivity().getApplicationContext().getSharedPreferences("prefBest", 0);
		sm = (SensorManager) getActivity().getSystemService(
				getActivity().SENSOR_SERVICE);
		proxSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		proxText = (TextView) getActivity().findViewById(R.id.proximityTextView);
		//textBestScore = (TextView) getActivity().findViewById(R.id.textBestScore);

		sm.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
		//prefBest = getActivity().getSharedPreferences("prefBest", 0);
		//best = prefBest.getInt("prefBest", 0);
		//textBestScore.setText("Best Score : " + best);
		//mediaPlayer = MediaPlayer.create(getActivity(), R.raw.beep);
		final SweetAlertDialog s = new SweetAlertDialog(getActivity(),
				SweetAlertDialog.SUCCESS_TYPE);
		s.setTitleText(getResources().getString(R.string.TitrePompes));
		s.setContentText(getResources().getString(R.string.InfoPompes));

		s.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
			@Override
			public void onClick(SweetAlertDialog sDialog) {
				s.cancel();
			}
		});
		s.show();
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		ColorDrawable colorDrawable = new ColorDrawable(getResources()
				.getColor(R.color.pompes));
		activity.getActionBar().setBackgroundDrawable(colorDrawable);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// nbrPompes ++;
		if (event.values[0] != 0) {
			nbrPompes++;
			//mediaPlayer.start();
			/*if (nbrPompes > best) {
				Editor editor = prefBest.edit();
				editor.putInt("prefBest", nbrPompes);
				editor.commit();
				best = prefBest.getInt("prefBest", 0);
				textBestScore.setText("Best Score : " + best);
			}*/
		}
		if (nbrPompes > 0) {

			proxText.setText("" + nbrPompes);
			
		}
		// tts.speak(nbrPompes + "pompes", TextToSpeech.QUEUE_FLUSH, null);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	/*
	 * @Override protected void onPause() { // TODO Auto-generated method stub
	 * super.onPause(); if (tts != null) { nbrPompes = -1; tts.stop();
	 * tts.shutdown(); }
	 */

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnReset) {
			final SweetAlertDialog s = new SweetAlertDialog(getActivity(),
					SweetAlertDialog.ERROR_TYPE);
			s.setTitleText(getResources().getString(R.string.TitredeletePompes));
			s.showCancelButton(true);
			s.setContentText(getResources().getString(R.string.deletePompes));

			s.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
				@Override
				public void onClick(SweetAlertDialog sDialog) {
					//Editor editor = prefBest.edit();
					//editor.clear();
					//editor.commit();
				/*	Fragment home = new HomeFragment();
					FragmentTransaction ft = getFragmentManager()
							.beginTransaction();
					ft.replace(R.id.frame_container, home);
					ft.addToBackStack(null);
					ft.commit();*/
					nbrPompes = 0;
					proxText.setText(nbrPompes+"");
					s.cancel();
				}
			});
			s.show();

		}


	}
	
}
