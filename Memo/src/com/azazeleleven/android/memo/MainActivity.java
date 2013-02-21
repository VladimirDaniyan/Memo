package com.azazeleleven.android.memo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.azazeleleven.android.memo.DaoMaster.DevOpenHelper;

public class MainActivity extends FragmentActivity {

	private SQLiteDatabase db;

	private EditText editText;

	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private NoteDao memoDao;

	private Cursor cursor;

	public static final CharSequence PREF_NAME = "pref_name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		
		editText = (EditText) findViewById(R.id.editText1);
		ListView lv = (ListView) findViewById(R.id.listView1);

		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "memo-db",
				null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		memoDao = daoSession.getNoteDao();

		String textColumn = NoteDao.Properties.Text.columnName;
		cursor = db.query(memoDao.getTablename(), memoDao.getAllColumns(),
				null, null, null, null, null, null);
		String[] from = { textColumn };
		int[] to = { android.R.id.text1, };

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_expandable_list_item_1, cursor, from,
				to);
		lv.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	

}
