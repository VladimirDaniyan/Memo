package com.vladimirdaniyan.android.memo;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.azazeleleven.android.memo.DaoMaster;
import com.azazeleleven.android.memo.DaoMaster.DevOpenHelper;
import com.azazeleleven.android.memo.DaoSession;
import com.azazeleleven.android.memo.Note;
import com.azazeleleven.android.memo.NoteDao;
import com.google.common.primitives.Ints;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class EditMemoActivity extends Activity implements OnClickListener,
		OnTimeSetListener {

	TimePicker myTimePicker;
	TimePickerDialog timePickerDialog;

	private EditText editText;
	private String memoText;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private NoteDao memoDao;
	private SQLiteDatabase db;
	private Long mRowId;
	private int notifId;

	protected boolean boolBasicNotif = false;
	protected boolean boolTimedNotif = false;

	private Calendar newCal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_memo);
		editText = (EditText) findViewById(R.id.edit_text_memo);
		Button ok = (Button) findViewById(R.id.button_ok);
		Button cancel = (Button) findViewById(R.id.button_cancel);
		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String memoText = extras.getString("memoText");
			mRowId = extras.getLong("mRowId");
			if (memoText != null && mRowId != null) {
				editText.setText(memoText);
				notifId = Ints.checkedCast(mRowId);
			}
		}

		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.notification_types,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 1:
					boolBasicNotif = true;
					break;
				case 2:
					boolTimedNotif = true;
					openTimePickerDialog(false);
					break;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

	}

	private void openTimePickerDialog(boolean is24HourView) {
		Calendar calendar = Calendar.getInstance();

		timePickerDialog = new TimePickerDialog(this, (OnTimeSetListener) this,
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), is24HourView);
		timePickerDialog.show();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_ok:
			if (boolBasicNotif == true) {
				saveMemoText();
				showNotification();
			} else if (boolTimedNotif == true) {
				saveMemoText();
				setAlarm(newCal);
				finish();
			} else {
				saveMemoText();
				finish();
			}
			break;
		case R.id.button_cancel:
			finish();
			break;
		}

	}

	// save the text that was entered
	public void saveMemoText() {
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "memo-db",
				null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		memoDao = daoSession.getNoteDao();

		memoText = editText.getText().toString();
		if (memoText.matches("")) {
			Toast.makeText(this, R.string.toast_input_required,
					Toast.LENGTH_SHORT).show();
			return;
		}

		Note memo = new Note(mRowId, memoText, null, null);
		memo.setId(mRowId);
		memoDao.insertOrReplace(memo);

	}

	// show the notification if applicable
	private void showNotification() {

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_stat_memo)
				.setContentTitle("Memo").setContentText(memoText);

		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.setBigContentTitle("Memo").bigText(memoText);
		builder.setStyle(bigTextStyle);

		// create intent for edit action when notification is clicked
		Intent resultIntent = new Intent(this, ListMemoActivity.class);

		// artificial back stack for started activity
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

		// Intent to start Activity at the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(notifId, builder.build());

		finish();

	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// Get time right now
		newCal = Calendar.getInstance();
		newCal.setTimeInMillis(System.currentTimeMillis());

		int hourNow = newCal.get(Calendar.HOUR_OF_DAY);
		int minuteNow = newCal.get(Calendar.MINUTE);

		// advance alarm by one day if behind current time
		if (hourOfDay < hourNow || hourOfDay == hourNow && minute == minuteNow) {
			newCal.add(Calendar.DAY_OF_YEAR, 1);
		}
		newCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		newCal.set(Calendar.MINUTE, minute);
		newCal.set(Calendar.SECOND, 0);
		newCal.set(Calendar.MILLISECOND, 0);

		Crouton.makeText(this,
				FormatAlarmText.formatText(this, newCal.getTimeInMillis()),
				Style.INFO, R.id.alternate_view_group).show();

	}

	private void setAlarm(Calendar targetCal) {

		Intent alarmIntent = new Intent(getBaseContext(), TimerReceiver.class);
		alarmIntent.putExtra("memoText", memoText);
		alarmIntent.putExtra("mRowId", mRowId);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getBaseContext(), notifId, alarmIntent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
				pendingIntent);

	}

	@Override
	protected void onDestroy() {
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}

}
