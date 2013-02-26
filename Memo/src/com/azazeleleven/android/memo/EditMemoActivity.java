package com.azazeleleven.android.memo;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.EditText;

import com.azazeleleven.android.memo.DaoMaster.DevOpenHelper;

public class EditMemoActivity extends Activity {

	private EditText editText;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private NoteDao memoDao;
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_memo);

		editText = (EditText) findViewById(R.id.edit_text_memo);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String memoText = extras.getString("memoText");
			
			if (memoText != null) {
				editText.setText(memoText);
			}
		}

	}

	public void saveMemo(View view) {
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "memo-db",
				null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		memoDao = daoSession.getNoteDao();

		String memoText = editText.getText().toString();
		editText.setText("");

		Note memo = new Note(null, memoText, null, null);
		memoDao.insert(memo);
		
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_stat_name)
				.setContentTitle("Memo").setContentText(memoText);
		
		builder.setContentIntent(pIntent);
		
		int mNotificationId = 001;
		NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotifyMgr.notify(mNotificationId, builder.build());
		finish();
	}

	public void cancelMemo(View view) {
		finish();

	}
}
