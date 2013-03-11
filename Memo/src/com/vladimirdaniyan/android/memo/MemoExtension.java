package com.vladimirdaniyan.android.memo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.azazeleleven.android.memo.DaoMaster;
import com.azazeleleven.android.memo.DaoMaster.DevOpenHelper;
import com.azazeleleven.android.memo.DaoSession;
import com.azazeleleven.android.memo.Note;
import com.azazeleleven.android.memo.NoteDao;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class MemoExtension extends DashClockExtension {

	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private NoteDao memoDao;
	private Note memo;

	private SQLiteDatabase db;
	private Cursor cursor;

	@Override
	protected void onInitialize(boolean isReconnect) {
		super.onInitialize(isReconnect);
		setUpdateWhenScreenOn(true);
	}

	@Override
	protected void onUpdateData(int reason) {
		final Intent addMemoIntent = getAddMemoIntent();

		publishUpdate(new ExtensionData().visible(true)
				.icon(R.drawable.ic_stat_memo).status("Add Memo")
				.expandedTitle("Most Recent Memo").expandedBody(getMemoText())
				.clickIntent(addMemoIntent));

	}

	private String getMemoText() {

		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "memo-db",
				null);
		db = helper.getReadableDatabase();

		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		memoDao = daoSession.getNoteDao();

		cursor = db.query(memoDao.getTablename(), memoDao.getAllColumns(),
				null, null, null, null, null);

		cursor.moveToLast();

		String memoText = "";

		if (cursor.getCount() > 0) {
			memo = memoDao.readEntity(cursor, 0);
			memoText = memo.getText();
		} else {
			memoText = (String) getText(R.string.no_memos_yet);
		}

		return memoText;

	}

	private Intent getAddMemoIntent() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setClassName("com.vladimirdaniyan.android.memo",
				"com.vladimirdaniyan.android.memo.EditMemoActivity");
		return intent;

	}

}
