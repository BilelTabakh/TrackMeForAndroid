package com.andy.trackme;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.gc.materialdesign.views.Switch;
import com.octo.android.robodemo.LabeledPoint;
import com.octo.android.robodemo.RoboDemo;

public class AlarmDetailsActivity extends Activity {

	// Robo Demo
	/**
	 * The id used to identifiy the robodemo "instance" related to this
	 * activity.
	 */
	private final static String DEMO_ACTIVITY_ID = "demo-main-activity";
	/**
	 * A boolean holding the internal state of the activity under RoboDemo,
	 * whether or not to display RoboDemo.
	 */
	private boolean showDemo = true;

	private AlarmDBHelper dbHelper = new AlarmDBHelper(this);

	private AlarmModel alarmDetails;

	private TimePicker timePicker;
	private EditText edtName;
	private Switch chkWeekly;
	private Switch chkSunday;
	private Switch chkMonday;
	private Switch chkTuesday;
	private Switch chkWednesday;
	private Switch chkThursday;
	private Switch chkFriday;
	private Switch chkSaturday;
	private TextView txtToneSelection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		refreshList();

		requestWindowFeature(Window.FEATURE_ACTION_BAR);

		setContentView(R.layout.activity_details);

		getActionBar().setTitle("Create New Alarm");
		getActionBar().setDisplayHomeAsUpEnabled(true);

		timePicker = (TimePicker) findViewById(R.id.alarm_details_time_picker);
		edtName = (EditText) findViewById(R.id.alarm_details_name);
		chkWeekly = (Switch) findViewById(R.id.alarm_details_repeat_weekly);
		chkSunday = (Switch) findViewById(R.id.alarm_details_repeat_sunday);
		chkMonday = (Switch) findViewById(R.id.alarm_details_repeat_monday);
		chkTuesday = (Switch) findViewById(R.id.alarm_details_repeat_tuesday);
		chkWednesday = (Switch) findViewById(R.id.alarm_details_repeat_wednesday);
		chkThursday = (Switch) findViewById(R.id.alarm_details_repeat_thursday);
		chkFriday = (Switch) findViewById(R.id.alarm_details_repeat_friday);
		chkSaturday = (Switch) findViewById(R.id.alarm_details_repeat_saturday);
		txtToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);

		long id = getIntent().getExtras().getLong("id");

		if (id == -1) {
			alarmDetails = new AlarmModel();
		} else {
			alarmDetails = dbHelper.getAlarm(id);

			timePicker.setCurrentMinute(alarmDetails.timeMinute);
			timePicker.setCurrentHour(alarmDetails.timeHour);

			edtName.setText(alarmDetails.name);

			chkWeekly.setChecked(alarmDetails.repeatWeekly);
			chkSunday.setChecked(alarmDetails
					.getRepeatingDay(AlarmModel.SUNDAY));
			chkMonday.setChecked(alarmDetails
					.getRepeatingDay(AlarmModel.MONDAY));
			chkTuesday.setChecked(alarmDetails
					.getRepeatingDay(AlarmModel.TUESDAY));
			chkWednesday.setChecked(alarmDetails
					.getRepeatingDay(AlarmModel.WEDNESDAY));
			chkThursday.setChecked(alarmDetails
					.getRepeatingDay(AlarmModel.THURSDAY));
			chkFriday.setChecked(alarmDetails
					.getRepeatingDay(AlarmModel.FRDIAY));
			chkSaturday.setChecked(alarmDetails
					.getRepeatingDay(AlarmModel.SATURDAY));

			txtToneSelection.setText(RingtoneManager.getRingtone(this,
					alarmDetails.alarmTone).getTitle(this));
		}

		final LinearLayout ringToneContainer = (LinearLayout) findViewById(R.id.alarm_ringtone_container);
		ringToneContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						RingtoneManager.ACTION_RINGTONE_PICKER);
				startActivityForResult(intent, 1);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1: {
				alarmDetails.alarmTone = data
						.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				txtToneSelection.setText(RingtoneManager.getRingtone(this,
						alarmDetails.alarmTone).getTitle(this));
				break;
			}
			default: {
				break;
			}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.alarm_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home: {
			finish();
			break;
		}
		case R.id.action_save_alarm_details: {
			updateModelFromLayout();

			AlarmManagerHelper.cancelAlarms(this);

			if (alarmDetails.id < 0) {
				dbHelper.createAlarm(alarmDetails);
			} else {
				dbHelper.updateAlarm(alarmDetails);
			}

			AlarmManagerHelper.setAlarms(this);

			setResult(RESULT_OK);
			finish();
		}
		}

		return super.onOptionsItemSelected(item);
	}

	private void updateModelFromLayout() {
		alarmDetails.timeMinute = timePicker.getCurrentMinute().intValue();
		alarmDetails.timeHour = timePicker.getCurrentHour().intValue();
		alarmDetails.name = edtName.getText().toString();
		alarmDetails.repeatWeekly = chkWeekly.isChecked();
		alarmDetails.setRepeatingDay(AlarmModel.SUNDAY, chkSunday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.MONDAY, chkMonday.isChecked());
		alarmDetails
				.setRepeatingDay(AlarmModel.TUESDAY, chkTuesday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.WEDNESDAY,
				chkWednesday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.THURSDAY,
				chkThursday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.FRDIAY, chkFriday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.SATURDAY,
				chkSaturday.isChecked());
		alarmDetails.isEnabled = true;
	}

	// Robo Demo
	private void refreshList() {

		new Handler().postDelayed(new Runnable() {

			public void run() {
				displayDemoIfNeeded();
			}
		}, 500);
	}

	// --------------------------------------------------
	// ----------RoboDemo related methods
	// --------------------------------------------------

	/**
	 * Displays demo if never show again has never been checked by the user.
	 */
	private void displayDemoIfNeeded() {

		boolean neverShowDemoAgain = RoboDemo.isNeverShowAgain(
				AlarmDetailsActivity.this, DEMO_ACTIVITY_ID);

		if (!neverShowDemoAgain && showDemo) {
			showDemo = false;
			ArrayList<LabeledPoint> arrayListPoints = new ArrayList<LabeledPoint>();

			// create a list of LabeledPoints
			LabeledPoint p = new LabeledPoint(
					findViewById(R.id.action_save_alarm_details),getResources().getString(R.string.savePlanning));
			arrayListPoints.add(p);

			p = new LabeledPoint(findViewById(R.id.action_save_alarm_details), "");
			arrayListPoints.add(p);

			// start DemoActivity.
			Intent intent = new Intent(AlarmDetailsActivity.this,
					MainActivityDemoActivity.class);
			RoboDemo.prepareDemoActivityIntent(intent, DEMO_ACTIVITY_ID,
					arrayListPoints);
			startActivity(intent);
		}
	}

}
