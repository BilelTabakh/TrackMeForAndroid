package com.andy.trackme;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.octo.android.robodemo.LabeledPoint;
import com.octo.android.robodemo.RoboDemo;

public class AlarmListActivity extends ListActivity {
	
	//Robo Demo
    /** The id used to identifiy the robodemo "instance" related to this activity. */
    private final static String DEMO_ACTIVITY_ID = "demo-main-activity";
    /** A boolean holding the internal state of the activity under RoboDemo, whether or not to display RoboDemo. */
    private boolean showDemo = true;

	private AlarmListAdapter mAdapter;
	private AlarmDBHelper dbHelper = new AlarmDBHelper(this);
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		mContext = this;
		
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		
		refreshList();
		setContentView(R.layout.activity_alarm_list);
		
		mAdapter = new AlarmListAdapter(this, dbHelper.getAlarms());
		
		setListAdapter(mAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.alarm_list, menu);
		getActionBar().setHomeButtonEnabled(true);
		return true;
	}
	@Override
	public void onBackPressed() {
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.action_add_new_alarm: {
				startAlarmDetailsActivity(-1);
				break;
			}
			case android.R.id.home: {
				Intent mainIntent = new Intent(this, MainActivity.class);
				startActivity(mainIntent);
				finish();
				break;
			}
		}

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
	        mAdapter.setAlarms(dbHelper.getAlarms());
	        mAdapter.notifyDataSetChanged();
	    }
	}
	
	public void setAlarmEnabled(long id, boolean isEnabled) {
		AlarmManagerHelper.cancelAlarms(this);
		
		AlarmModel model = dbHelper.getAlarm(id);
		model.isEnabled = isEnabled;
		dbHelper.updateAlarm(model);
		
		AlarmManagerHelper.setAlarms(this);
	}

	public void startAlarmDetailsActivity(long id) {
		Intent intent = new Intent(this, AlarmDetailsActivity.class);
		intent.putExtra("id", id);
		startActivityForResult(intent, 0);
	}
	
	public void deleteAlarm(long id) {
		final long alarmId = id;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.alertAlarmConfirmation))
		.setTitle(getResources().getString(R.string.DeletePlanning))
		.setCancelable(true)
		.setNegativeButton(getResources().getString(R.string.cancelAlarm), null)
		.setPositiveButton("Ok", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Cancel Alarms
				AlarmManagerHelper.cancelAlarms(mContext);
				//Delete alarm from DB by id
				dbHelper.deleteAlarm(alarmId);
				//Refresh the list of the alarms in the adaptor
				mAdapter.setAlarms(dbHelper.getAlarms());
				//Notify the adapter the data has changed
				mAdapter.notifyDataSetChanged();
				//Set the alarms
				AlarmManagerHelper.setAlarms(mContext);
			}
		}).show();
	}
	
	//Robo Demo
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

    boolean neverShowDemoAgain = RoboDemo.isNeverShowAgain(AlarmListActivity.this, DEMO_ACTIVITY_ID);

    if ( !neverShowDemoAgain && showDemo ) {
        showDemo = false;
        ArrayList< LabeledPoint > arrayListPoints = new ArrayList< LabeledPoint >();

        // create a list of LabeledPoints
        LabeledPoint p = new LabeledPoint( findViewById(R.id.action_add_new_alarm), getResources().getString(R.string.toAddPlanning) );
        arrayListPoints.add( p );
        
        p = new LabeledPoint( findViewById(R.id.action_add_new_alarm), "" );
        arrayListPoints.add( p );
        
        // start DemoActivity.
        Intent intent = new Intent(AlarmListActivity.this, MainActivityDemoActivity.class );
        RoboDemo.prepareDemoActivityIntent(intent, DEMO_ACTIVITY_ID, arrayListPoints);
        startActivity(intent);
    }
}
}
