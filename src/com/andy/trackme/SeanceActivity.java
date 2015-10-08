package com.andy.trackme;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.andy.trackme.entities.Seance;
import com.andy.trackme.sqllite.SeanceBDD;
import com.andy.trackme.utils.MyApp;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.octo.android.robodemo.LabeledPoint;
import com.octo.android.robodemo.RoboDemo;

public class SeanceActivity extends Activity implements LocationListener,
		OnClickListener {
	/*******************************************************/
	/**
	 * ATTRIBUTS. /
	 *******************************************************/
	private LocationManager locationManager;
	private GoogleMap gMap;
	private Marker marker;
	private double oldLatitude = 0;
	private double oldLongitude = 0;
	private boolean premiere = true;
	private int distance = 0;
	private TextView textDistance;
	private TextView textVitesse;
	private TextView textCalories;
	private int calories;
    private SharedPreferences prefPoids;    
    private int poids;
	private Chronometer chronometerDuree;
	private ImageButton btnArret;
	private ImageButton btnStart;
	private boolean isStarted = false;
	private double vit;
	private Seance seance;
	private SeanceBDD seanceBDD;
	private MyApp app;
	private Location location;
	//Robo Demo
    /** The id used to identifiy the robodemo "instance" related to this activity. */
    private final static String DEMO_ACTIVITY_ID = "demo-main-activity";
    /** A boolean holding the internal state of the activity under RoboDemo, whether or not to display RoboDemo. */
    private boolean showDemo = true;


	/*******************************************************/
	/**
	 * METHODES / FONCTIONS. /
	 *******************************************************/

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seance);
		
		//Poids Pour calcul calories
		try{
		prefPoids  = getSharedPreferences("prefTaille",0);
		poids  = Integer.parseInt(prefPoids.getString("prefPoids","")); }
		catch(Exception e){
		poids = 0;
		Toast.makeText(this, getResources().getString(R.string.erreurInfoPoidsVide), 2000).show();}
		if(poids == 0){
		Toast.makeText(this, getResources().getString(R.string.erreurInfoPoidsVide), 2000).show();
		}
		//Toast.makeText(this, poids+" kg", Toast.LENGTH_SHORT).show();
		
		//Robo Demo
		refreshList();
	
		//Map
		gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		try{
		marker = gMap.addMarker(new MarkerOptions().title("Vous êtes ici").position(new LatLng(0, 0)));
		gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		gMap.getUiSettings().setCompassEnabled(true);
		gMap.getUiSettings().setMyLocationButtonEnabled(true);
		gMap.getUiSettings().setRotateGesturesEnabled(true);
		}catch(Exception e){
			//finish();
			Toast.makeText(this, "Update Google Map ", 2000).show();
		}

		// gMap.addCircle(new
		// CircleOptions().center(latLng1).fillColor(Color.BLUE).strokeColor(Color.BLUE).radius(2));

		// Affichage
		textDistance = (TextView) findViewById(R.id.textDistance);
		textVitesse = (TextView) findViewById(R.id.textVitesse);
		textCalories = (TextView) findViewById(R.id.valueCalories);

		btnStart = (ImageButton) findViewById(R.id.btnStartSeance);
		btnArret = (ImageButton) findViewById(R.id.btnArretSeance);

		btnStart.setVisibility(View.VISIBLE);
		btnArret.setVisibility(View.INVISIBLE);

		btnStart.setOnClickListener(this);
		btnArret.setOnClickListener(this);

	}
		@Override
		public void onBackPressed() {
		}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onResume() { 
		super.onResume();
		try{
		// Obtention de la référence du service
		locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
	    if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
	        buildAlertMessageNoGps();
	    }
		
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		location = locationManager.getLastKnownLocation(provider);

		if (location != null) {
			LatLng latLng1 = new LatLng(location.getLatitude(),
					location.getLongitude());
			// gMap.addMarker(new
			// MarkerOptions().title("Vous étiez Ici").position(latLng1));
			marker.setPosition(latLng1);
			gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 15));
		} else {
			gMap.addMarker(new MarkerOptions().title("Nouveau Parcours")
					.position(new LatLng(0, 0)));
		}
		// Si le GPS est disponible, on s'y abonne
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			abonnementGPS();
		}
		}catch(Exception e){
			Toast.makeText(this, "Update Google Map ", 2000).show();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPause() {
		super.onPause();

		// On appelle la méthode pour se désabonner
		desabonnementGPS();
	}

	/**
	 * Méthode permettant de s'abonner à la localisation par GPS.
	 */
	public void abonnementGPS() {
		// On s'abonne
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 3, this);

	}

	/**
	 * Méthode permettant de se désabonner de la localisation par GPS.
	 */
	public void desabonnementGPS() {
		// Si le GPS est disponible, on s'y abonne
		locationManager.removeUpdates(this);
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void onLocationChanged(final Location location) {
		// On affiche dans un Toat la nouvelle Localisation
		final StringBuilder msg = new StringBuilder("lat : ");

		msg.append(location.getLatitude());
		msg.append("; lng : ");
		msg.append(location.getLongitude());
		// gMap.addMarker(new
		// MarkerOptions().title("Vous êtes ici").position(new LatLng(0, 0)));

		// Toast.makeText(this, msg.toString()+" "+distance,
		// Toast.LENGTH_SHORT).show();
		if (premiere) {
			oldLatitude = location.getLatitude();
			oldLongitude = location.getLongitude();
		}
		premiere = false;
		// Mise à jour des coordonnées
		final LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		final LatLng latLng2 = new LatLng(oldLatitude, oldLongitude);
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, gMap.getCameraPosition().zoom));
		marker.setPosition(latLng);

		// gMap.addCircle(new
		// CircleOptions().fillColor(Color.BLUE).center(latLng).visible(true).radius(2).strokeColor(Color.BLUE));

		gMap.addPolyline(new PolylineOptions().add(latLng, latLng2).width(5)
				.color(Color.BLUE));      
		oldLatitude = location.getLatitude();
		oldLongitude = location.getLongitude();
		if (isStarted) {
			distance += calculateDistance(latLng2.longitude, latLng2.latitude,
					latLng.longitude, latLng.latitude);
			textDistance.setText(distance + "");
			//
			long timeElapsed = SystemClock.elapsedRealtime()
					- chronometerDuree.getBase();
			int hours = (int) (timeElapsed / 3600000);
			int minutes = (int) (timeElapsed - hours * 3600000) / 60000;
			int seconds = (int) (timeElapsed - hours * 3600000 - minutes * 60000) / 1000;
					
			double temps = seconds;		
			double distaneM = distance;
			
			vit  = (distaneM/temps)*3.6;
			vit = (double)((int)(vit*100))/100;

			textVitesse.setText(vit+"");
			
			
			calories += (poids*distance)/1000;
			textCalories.setText(calories+"");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onProviderDisabled(final String provider) {
		// Si le GPS est désactivé on se désabonne
		if ("gps".equals(provider)) {
			desabonnementGPS();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onProviderEnabled(final String provider) {
		// Si le GPS est activé on s'abonne
		if ("gps".equals(provider)) {
			abonnementGPS();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStatusChanged(final String provider, final int status,
			final Bundle extras) {
	}

	private double calculateDistance(double fromLong, double fromLat,
			double toLong, double toLat) {

		double d2r = Math.PI / 180;
		double dLong = (toLong - fromLong) * d2r;
		double dLat = (toLat - fromLat) * d2r;
		double a = Math.pow(Math.sin(dLat / 2.0), 2) + Math.cos(fromLat * d2r)
				* Math.cos(toLat * d2r) * Math.pow(Math.sin(dLong / 2.0), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = 6367000 * c;
		return Math.round(d);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnStartSeance) {
			isStarted = true;
			// Hide btnStart and Show btnArret
			btnStart.setVisibility(View.INVISIBLE);
			btnArret.setVisibility(View.VISIBLE);
			chronometerDuree = (Chronometer) findViewById(R.id.chronometer1);
			chronometerDuree.setBase(SystemClock.elapsedRealtime());
			chronometerDuree.start();
			
			
			
			
			
			
		} else if (v.getId() == R.id.btnArretSeance) {
			isStarted = false;
			// Hide btnArret and Show btnStart
			btnStart.setVisibility(View.VISIBLE);
			btnArret.setVisibility(View.INVISIBLE);
			chronometerDuree.stop();

			
			long timeElapsed = SystemClock.elapsedRealtime()
					- chronometerDuree.getBase();
			int hours = (int) (timeElapsed / 3600000);
			int minutes = (int) (timeElapsed - hours * 3600000) / 60000;
			int seconds = (int) (timeElapsed - hours * 3600000 - minutes * 60000) / 1000;
			
					
			/*double temps = seconds;		
			double distaneM = distance;
			
			vit  = (distaneM/temps)*3.6;
			vit = (double)((int)(vit*100))/100;
			
			Toast.makeText(this, "vit : "+vit, 2000).show();*/
			
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentDateandTime = sdf.format(new Date());
			seance = new Seance();
			seance.setDuree(minutes);
			seance.setDistance(distance);
			seance.setVitesse(vit+"");
			seance.setCalories(calories);
			seance.setDateSeance(currentDateandTime);
			seanceBDD = new SeanceBDD(getApplicationContext());
			seanceBDD.insertTop(seance);
			seanceBDD.close();
			// Sweet Alert
			
			SweetAlertDialog s =new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
					s.setTitleText(getResources().getString(R.string.bienJoue));
					s.showCancelButton(true);
					s.setContentText(getResources().getString(R.string.alertSeanceFinie));
					s.setCancelClickListener(
							new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sDialog) {
									// Direction Home
									Intent iHome = new Intent(SeanceActivity.this,MainActivity.class);
									startActivity(iHome);
									finish();
								}
							});
					s.setConfirmClickListener(
							new SweetAlertDialog.OnSweetClickListener() {
								@Override
								public void onClick(SweetAlertDialog sDialog) {
									 try {
							                CaptureMapScreen();
							            } catch (Exception e) {
							                e.printStackTrace();
							            }
										Intent shareIntent = new Intent();
										shareIntent.setAction(Intent.ACTION_SEND);
										shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory() + File.separator + "/DCIM/seanceMap.png")));
										shareIntent.setType("image/jpeg");
										startActivity(Intent.createChooser(shareIntent,getResources().getString(R.string.partageParcours)));
								}
							});
					s.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		getActionBar().setHomeButtonEnabled(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			if(isStarted==true){
				
				
				final SweetAlertDialog s =new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
				s.setTitleText(getResources().getString(R.string.quitWithoutSave));
				s.showCancelButton(true);
				s.setContentText(getResources().getString(R.string.alertNonEnregistree));
				s.setCancelClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								// Direction Home
								s.cancel();
							}
						});
				s.setConfirmClickListener(
						new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								 
								 Intent iHome = new Intent(SeanceActivity.this,MainActivity.class);
									startActivity(iHome);
									finish();
							}
						});
				s.show();
				
				
				
				
			}else{
			Intent parentIntent = new Intent(this, MainActivity.class);
			startActivity(parentIntent);
			finish();}
			return true;
		}
		if (id == R.id.menu_music) {
			//this.moveTaskToBack(true);
			//Intent musicIntent = new Intent(this, MusiqueActivity.class);
			//startActivity(musicIntent);
			Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
			startActivity(intent);
			
			return true;
		}
		if (id == R.id.action_share) {
			 try {
	                CaptureMapScreen();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory() + File.separator + "/DCIM/seanceMap.png")));
				shareIntent.setType("image/jpeg");
				startActivity(Intent.createChooser(shareIntent,getResources().getString(R.string.partageParcours)));
		}
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
	        gMap.snapshot(callback);
	}
	
	
	  private void refreshList() {

	        new Handler().postDelayed( new Runnable() {
	            
	            public void run() {
	                displayDemoIfNeeded();
	            }
	        }, 500 );
	    }
    // --------------------------------------------------
    // ----------RoboDemo related methods
    // --------------------------------------------------

    /**
     * Displays demo if never show again has never been checked by the user.
     */
    private void displayDemoIfNeeded() {

        boolean neverShowDemoAgain = RoboDemo.isNeverShowAgain(SeanceActivity.this, DEMO_ACTIVITY_ID);

        if ( !neverShowDemoAgain && showDemo ) {
            showDemo = false;
            ArrayList< LabeledPoint > arrayListPoints = new ArrayList< LabeledPoint >();

            // create a list of LabeledPoints
            LabeledPoint p = new LabeledPoint( btnStart, getResources().getString(R.string.beginSeance) );
            arrayListPoints.add( p );
            
            p = new LabeledPoint( this, 0.95f, 0.05f, getResources().getString(R.string.playingMusic) );
            arrayListPoints.add( p );

            // start DemoActivity.
            Intent intent = new Intent(SeanceActivity.this, MainActivityDemoActivity.class );
            RoboDemo.prepareDemoActivityIntent(intent, DEMO_ACTIVITY_ID, arrayListPoints);
            startActivity(intent);
        }
    }
	
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
               .setCancelable(false)
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                       startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                   }
               });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    
}
