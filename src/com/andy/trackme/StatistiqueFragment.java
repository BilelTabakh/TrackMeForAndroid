package com.andy.trackme;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.andy.trackme.entities.Seance;
import com.andy.trackme.sqllite.SeanceBDD;
import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.BarChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;

public class StatistiqueFragment extends Fragment {
	private LinearLayout relativeLayout;
	private Bitmap myBitmap;
	
	SeanceBDD seanceBDD;
	List<Seance> seanceList;

	private String[] mLabelsLineChart ;
	private String[] mLabelsBarChart ;
	private final TimeInterpolator enterInterpolator = new DecelerateInterpolator(
			1.5f);
	private final TimeInterpolator exitInterpolator = new AccelerateInterpolator();
	//LineChart Statistique
	private static LineChartView mLineChart;
	private TextView mLineTooltip;
	private static int mCurrLineEntriesSize;
	private static int mCurrLineSetSize;
	
	//BarChart Statistique
	private static BarChartView mBarChart;
	private TextView mBarTooltip;
	private static int mCurrBarEntriesSize;
	private static int mCurrBarSetSize;

	private TextView txtDurationStatRecieve;
	private TextView txtDistanceStatRecieve;
	private TextView txtSpeedStatRecieve;
	private TextView txtCaloriesStatRecieve;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);

		View rootView = inflater.inflate(R.layout.fragment_statistique,
				container, false);

		
		
		mLineChart = (LineChartView) rootView.findViewById(R.id.linechart);
		mBarChart = (BarChartView)rootView.findViewById(R.id.barchart);
		
		initLineChart();
		initBarChart();
		
		
		relativeLayout = (LinearLayout) rootView.findViewById(R.id.relative1);
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
		setHasOptionsMenu(true);
		
		super.onActivityCreated(savedInstanceState);
		

		// Statistique Graph
		try{
		seanceList = new ArrayList<Seance>();
		seanceBDD = new SeanceBDD(getActivity().getApplicationContext());
		seanceList = seanceBDD.selectAll();
		
		mLabelsLineChart = new String[seanceList.size()];
		for(int i=0;i<seanceList.size();i++){
			mLabelsLineChart[i]=seanceList.get(i).getDistance()*0.001+"\nKm";
		}
		
		mLabelsBarChart = new String[seanceList.size()];
		for(int i=0;i<seanceList.size();i++){
			mLabelsBarChart[i]=seanceList.get(i).getCalories()+"";
		}

		updateLineChart(1, mCurrLineEntriesSize = seanceList.size());
		updateBarChart(1, mCurrLineEntriesSize = seanceList.size());
		
		// Statistique par Date
		txtDurationStatRecieve = (TextView) getActivity().findViewById(
				R.id.txtDurationStatistique);
		txtDistanceStatRecieve = (TextView) getActivity().findViewById(
				R.id.txtDistanceStatistique);
		txtSpeedStatRecieve = (TextView) getActivity().findViewById(
				R.id.txtSpeedStatistique);
		txtCaloriesStatRecieve = (TextView) getActivity().findViewById(
				R.id.txtCaloriesStatistique);

		// Récupération des données envoyées depuis le fragment
		// ListStatistiqueFragment
		Bundle bundle = this.getArguments();
		int duree = bundle.getInt("DUREE");
		int distance = bundle.getInt("DISTANCE");
		String vitesse = bundle.getString("VITESSE");
		int calories = bundle.getInt("CALORIES");

		// Affectation des données aux TextView(s)
		txtDurationStatRecieve.setText(duree + "");
		txtDistanceStatRecieve.setText(distance*0.001 + " km");
		txtSpeedStatRecieve.setText(vitesse + "");
		txtCaloriesStatRecieve.setText(calories + "");
		}catch(Exception e){
			Toast.makeText(getActivity(), "empty !!!", Toast.LENGTH_LONG).show();
		}

	}

	/*------------------------------------*
	 *              LINECHART             *
	 *------------------------------------*/

	private void initLineChart() {

		final OnEntryClickListener lineEntryListener = new OnEntryClickListener() {
			@Override
			public void onClick(int setIndex, int entryIndex, Rect rect) {
				if (mLineTooltip == null)
					showLineTooltip(entryIndex, rect);
				else
					dismissLineTooltip(entryIndex, rect);
			}
		};

		
		  final OnClickListener lineClickListener = new OnClickListener(){
		  
		  @Override public void onClick(View v) { if(mLineTooltip != null)
		  dismissLineTooltip(-1, null); } };
		 

		// mLineChart.setStep(1);
		mLineChart.setOnEntryClickListener(lineEntryListener);
		mLineChart.setOnClickListener(lineClickListener);

	}

	public void updateLineChart(int nSets, int nPoints) {

		LineSet data;
		mLineChart.reset();
		try{
		for (int i = 0; i < nSets; i++) {
			data = new LineSet();
			// insertion des points dans le diagramme
			for (int j = 0; j < seanceList.size(); j++){
				
				if(seanceList.get(j).getDistance()>20000)
					{seanceList.get(j).setDistance(20000);}

				data.addPoint(new Point("",(int)(seanceList.get(j).getDistance()*0.001)));}
			
			// paramètres des points et des lignes
			data.setDots(true)
					.setDotsColor(getResources().getColor(R.color.dotsLineChart))
					.setDotsRadius(10)// taille du rayon
					.setLineThickness(8)// taille de la ligne
					.setLineColor(getResources().getColor(R.color.traitLineChart)) //couleur de la ligne
					.setDashed(false)// pointillée ou non
					.setSmooth(false)// lisse ou non
					.setFill(Color.parseColor("#3388c6c3"))
					.setDotsStrokeThickness(6)// epaisseur contour des points
					.setDotsStrokeColor(getResources().getColor(R.color.dotsStrokeLineChart));// couleur contour des points

			mLineChart.addData(data);
		}

		mLineChart.setGrid(DataRetriever.randPaint())
				.setVerticalGrid(DataRetriever.randPaint())
				.setHorizontalGrid(DataRetriever.randPaint())
				// .setThresholdLine(2, randPaint())
				.setYLabels(YController.LabelPosition.NONE)//labels des coordonnées de y
				.setYAxis(true)// ajouter l'axe des ordonnées
				.setXLabels(XController.LabelPosition.OUTSIDE)//labels des coordonnées de x
				.setXAxis(true)// ajouter l'axe des abcisses
				.setMaxAxisValue(20000, 5)
				.animate(DataRetriever.randAnimation(nPoints));
		}catch(Exception e){Toast.makeText(getActivity(), "vide 2 !!!", Toast.LENGTH_LONG).show();}
				//.show();
	}

	@SuppressLint("NewApi")
	private void showLineTooltip(int index, Rect rect) {

		mLineTooltip = (TextView) getActivity().getLayoutInflater().inflate(
				R.layout.circular_tooltip, null);
		mLineTooltip.setText(mLabelsLineChart[index]);

		LayoutParams layoutParams = new LayoutParams(
				(int) Tools.fromDpToPx(40), (int) Tools.fromDpToPx(40));
		layoutParams.leftMargin = rect.centerX() - layoutParams.width / 2;
		layoutParams.topMargin = rect.centerY() - layoutParams.height / 2;
		mLineTooltip.setLayoutParams(layoutParams);

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			mLineTooltip.setPivotX(layoutParams.width / 2);
			mLineTooltip.setPivotY(layoutParams.height / 2);
			mLineTooltip.setAlpha(0);
			mLineTooltip.setScaleX(0);
			mLineTooltip.setScaleY(0);
			mLineTooltip.animate().setDuration(150).alpha(1).scaleX(1)
					.scaleY(1).rotation(360).setInterpolator(enterInterpolator);
		}

		mLineChart.showTooltip(mLineTooltip);
	}

	@SuppressLint("NewApi")
	private void dismissLineTooltip(final int index, final Rect rect) {
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mLineTooltip.animate().setDuration(100).scaleX(0).scaleY(0)
					.alpha(0).rotation(-360)
			
			
			  .setInterpolator(exitInterpolator).withEndAction(new Runnable(){
			  
			  @Override public void run() {
			  mLineChart.removeView(mLineTooltip); mLineTooltip = null;
			  if(index != -1) showLineTooltip(index, rect); } });
			 
		} else {
			mLineChart.dismissTooltip(mLineTooltip);
			mLineTooltip = null;
			if (index != -1)
				showLineTooltip(index, rect);
		}
	}
	
	/*------------------------------------*
	 *              BARCHART              *
	 *------------------------------------*/
	
	private void initBarChart(){
		
		final OnEntryClickListener barEntryListener = new OnEntryClickListener(){
			
			@Override
			public void onClick(int setIndex, int entryIndex, Rect rect) {
				
				if(mBarTooltip == null)
					showBarTooltip(entryIndex, rect);
				else
					dismissBarTooltip(entryIndex, rect);
			}
		};
		
		final OnClickListener barClickListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(mBarTooltip != null)
					dismissBarTooltip(-1, null);
			}
		};
		
		
		mBarChart.setOnEntryClickListener(barEntryListener);
		mBarChart.setOnClickListener(barClickListener);
	}
	
	
	
	public void updateBarChart(int nSets, int nPoints){
		
		BarSet data;
		Bar bar;
		mBarChart.reset();
		try{
		for(int i = 0; i < nSets; i++){
			
			data = new BarSet();
			for(int j = 0; j < nPoints; j++){
				
				if(seanceList.get(j).getCalories()>3000)
				{seanceList.get(j).setCalories(3000);}
				
				bar = new Bar(mLabelsBarChart[j], seanceList.get(j).getCalories());
				//bar.setColor(Color.parseColor(getColor(j)));
				data.addBar(bar);
			}
			
			data.setColor(getResources().getColor(R.color.traitBarChart));
			mBarChart.addData(data);
		}
		
		mBarChart.setBarSpacing(10);
		//mBarChart.setSetSpacing(DataRetriever.randDimen(2, 7));
		//mBarChart.setBarBackground(true);
		//mBarChart.setBarBackgroundColor(getResources().getColor(R.color.dotsLineChart));
		//mBarChart.setRoundCorners(50);

		mBarChart.setBorderSpacing(15)
			.setGrid(DataRetriever.randPaint())
			.setHorizontalGrid(DataRetriever.randPaint())
			.setVerticalGrid(DataRetriever.randPaint())
			.setYLabels(YController.LabelPosition.NONE)
			.setYAxis(true)
			.setXLabels(XController.LabelPosition.OUTSIDE)
			.setXAxis(true)
			//.setThresholdLine(2, randPaint())
			.setMaxAxisValue(3000, 600)
			.animate(DataRetriever.randAnimation(nPoints))
			//.show()
			;	
		}catch(Exception e){
			Toast.makeText(getActivity(), "vide 3 !!!", Toast.LENGTH_LONG).show();
		}
	}
	
	
	@SuppressLint("NewApi")
	private void showBarTooltip(int index, Rect rect){

		mBarTooltip = (TextView)getActivity().getLayoutInflater().inflate(R.layout.tooltip, null);
		try{
		mBarTooltip.setText(""+mLabelsBarChart[index]);
		
		LayoutParams layoutParams = new LayoutParams(rect.width(), rect.height());	
		layoutParams.leftMargin = rect.left;
		layoutParams.topMargin = rect.top;
		mBarTooltip.setLayoutParams(layoutParams);
        
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){
        	mBarTooltip.setAlpha(0);
        	mBarTooltip.setScaleY(0);
        	mBarTooltip.animate()
	        	.setDuration(200)
	        	.alpha(1)
	        	.scaleY(1)
	        	.setInterpolator(enterInterpolator);
        }
        
        mBarChart.showTooltip(mBarTooltip);
	
		}catch(Exception e){Toast.makeText(getActivity(), "vide vide !!!", Toast.LENGTH_LONG).show();}
		}
	

	@SuppressLint("NewApi")
	private void dismissBarTooltip(final int index, final Rect rect){
		
		if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			mBarTooltip.animate()
			.setDuration(100)
	    	.scaleY(0)
	    	.alpha(0)
			
	    	.setInterpolator(exitInterpolator).withEndAction(new Runnable(){
				@Override
				public void run() {
					mBarChart.removeView(mBarTooltip);
					mBarTooltip = null;
					if(index != -1)
						showBarTooltip(index, rect);
				}
	    	});
		}else{
			mBarChart.dismissTooltip(mBarTooltip);
			mBarTooltip = null;
			if(index != -1)
				showBarTooltip(index, rect);
		}
	}

	public static Bitmap captureScreen(View v) {

		Bitmap screenshot = null;
		try {

			if (v != null) {

				screenshot = Bitmap.createBitmap(v.getMeasuredWidth(),
						v.getMeasuredHeight(), Config.ARGB_8888);
				Canvas canvas = new Canvas(screenshot);
				v.draw(canvas);
			}

		} catch (Exception e) {
			Log.d("ScreenShotActivity", "Failed to capture screenshot because:"
					+ e.getMessage());
		}

		return screenshot;
	}

	public static void saveImage(Bitmap bitmap) throws IOException {

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 40, bytes);
		File f = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "/DCIM/test.png");
		f.createNewFile();
		FileOutputStream fo = new FileOutputStream(f);
		fo.write(bytes.toByteArray());
		fo.close();
	}
	
	//Action Bar Share
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.main_share, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==R.id.action_share){
			
			relativeLayout.post(new Runnable() {
				public void run() {
					// take screenshot
					myBitmap = captureScreen(relativeLayout);
					try {
						if (myBitmap != null) {
							// save image to SD card
							saveImage(myBitmap);
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(
					Environment.getExternalStorageDirectory() + File.separator
							+ "/DCIM/test.png")));
			shareIntent.setType("image/jpeg");
			startActivity(Intent.createChooser(shareIntent,
					"Share your performance"));
		
		}
			
			
			/*try {
                CaptureMapScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory() + File.separator + "/DCIM/seanceMap.png")));
			shareIntent.setType("image/jpeg");
			startActivity(Intent.createChooser(shareIntent,"Partage ton parcours"));*/
		
		
		return super.onOptionsItemSelected(item);
	}
	
	//ScreenShot GoogleMap
		public void CaptureMapScreen() 
		{
		SnapshotReadyCallback callback = new SnapshotReadyCallback() {
		            Bitmap bitmap;

		            @Override
		            public void onSnapshotReady(Bitmap snapshot) {
		                // TODO Auto-generated method stub
		                bitmap = snapshot;
		                try {
		                    FileOutputStream out = new FileOutputStream(
		        					Environment.getExternalStorageDirectory() + File.separator
									+ "/DCIM/seanceMap.png");
		                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }
		            }
		        };
		        

		}

}
