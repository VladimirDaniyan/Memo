package com.azazeleleven.android.memo;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.azazeleleven.android.memo.DaoMaster.DevOpenHelper;

public class EditMemoActivity extends Activity {

	private EditText editText;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private NoteDao memoDao;
	private SQLiteDatabase db;
	private Long mRowId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_memo);

		editText = (EditText) findViewById(R.id.edit_text_memo);
		Button ok = (Button) findViewById(R.id.button_ok);
		ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				saveMemo();

			}
		});

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String memoText = extras.getString("memoText");
			mRowId = extras.getLong("mRowId");
			Log.d("EditMemoActivity", "Get Note, ID: " + mRowId);
			Log.d("EditMemoActivity", "Get Note, TEXT: " + memoText);
			if (memoText != null) {
				editText.setText(memoText);
			}
		}

	}

	public void saveMemo() {
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
		Log.d("MainActiity", "Inserted new note, ID: " + memo.getId());

		showMemoNotification();

		finish();
	}

	private void showMemoNotification() {
		final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_add_to_notification);
		if (checkBox.isChecked()) {
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					this).setSmallIcon(R.drawable.ic_stat_name)
					.setContentTitle("Quick Memo")
					.setContentText(editText.getText().toString());
			NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			int mNotificationId = 001;
			nm.notify(mNotificationId, builder.build());
			Log.d("EditMemoActivity", "Recieved text input: "
					+ editText.getText().toString());
			finish();
		} else {
			finish();
		}

	}

	public void cancelMemo(View view) {
		finish();

	}

}
