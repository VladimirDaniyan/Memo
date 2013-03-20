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

public class EditMemoActivity extends Activity implements OnClickListener {

	protected static final int REQ_1 = 1;
	private static int MEMO_ID = 0;

	private EditText editText;

	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private NoteDao memoDao;
	private SQLiteDatabase db;
	private Long mRowId;
	protected boolean boolBasicNotif = false;
	protected boolean boolTimedNotif = false;

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
			if (memoText != null) {
				editText.setText(memoText);
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
					saveMemoText();
					openTimePickerDialog(false);
					break;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_ok:
			if (boolBasicNotif == true) {
				saveMemoText();
				showNotification();
			} else if (boolTimedNotif == true) {
				showNotification();
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

		String memoText = editText.getText().toString();
		if (memoText.matches("")) {
			Toast.makeText(this, R.string.toast_input_required,
					Toast.LENGTH_SHORT).show();
			return;
		}

		Note memo = new Note(mRowId, memoText, null, null);
		memo.setId(mRowId);
		memoDao.insertOrReplace(memo);
	}

	OnTimeSetListener OnTimeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

			// Get time right now
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());

			int hourNow = cal.get(Calendar.HOUR_OF_DAY);
			int minuteNow = cal.get(Calendar.MINUTE);

			// advance alarm by one day if behind current time
			if (hourOfDay < hourNow || hourOfDay == hourNow
					&& minute == minuteNow) {
				cal.add(Calendar.DAY_OF_YEAR, 1);
			}
			cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			setAlarm(cal);

		}

		// set timer and send information to BroadcastReciever
		private void setAlarm(Calendar cal) {
			Intent intent = new Intent(getBaseContext(), TimerReceiver.class);
			intent.putExtra("alarm_time",
					formatText(getBaseContext(), cal.getTimeInMillis()));
			intent.putExtra("memoText", editText.getText().toString());
			intent.putExtra("mRowId", mRowId);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					getBaseContext(), REQ_1, intent, 0);
			AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
					pendingIntent);

		}
	};

	// show the notification if applicable
	private void showNotification() {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_stat_memo)
				.setContentTitle(editText.getText().toString())
				.setContentText("memo");

		// create intent for edit action when notification is clicked
		Intent resultIntent = new Intent(this, ListMemoActivity.class);

		// artificial back stack for started activity
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Intent to start Activity at the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
				MEMO_ID, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify((int) (MEMO_ID + mRowId), builder.build());

		finish();

	}

	private void openTimePickerDialog(boolean is24r) {
		Calendar calendar = Calendar.getInstance();

		TimePickerDialog timePickerDialog = new TimePickerDialog(this,
				OnTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), is24r);
		timePickerDialog.setTitle("When do you want to be reminded?");
		timePickerDialog.show();
	}

	// show alarm as "alarm set for x days x hours and x minutes"
	static String formatText(Context context, long timeInMillis) {
		long delta = timeInMillis - System.currentTimeMillis();
		long hours = delta / (1000 * 60 * 60);
		long minutes = delta / (1000 * 60) % 60;
		long days = hours / 24;
		hours = hours % 24;

		String daySeq = (days == 0) ? "" : (days == 1) ? context
				.getString(R.string.day) : context.getString(R.string.days,
				Long.toString(days));

		String minSeq = (minutes == 0) ? "" : (minutes == 1) ? context
				.getString(R.string.minute) : context.getString(
				R.string.minutes, Long.toString(minutes));

		String hourSeq = (hours == 0) ? "" : (hours == 1) ? context
				.getString(R.string.hour) : context.getString(R.string.hours,
				Long.toString(hours));

		boolean dispDays = days > 0;
		boolean dispHour = hours > 0;
		boolean dispMinute = minutes > 0;

		int index = (dispDays ? 1 : 0) | (dispHour ? 2 : 0)
				| (dispMinute ? 4 : 0);

		String[] formats = context.getResources().getStringArray(
				R.array.alarm_set);
		return String.format(formats[index], daySeq, hourSeq, minSeq);
	}

}
