package com.azazeleleven.android.memo;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.azazeleleven.android.memo.DaoMaster.DevOpenHelper;

public class MainActivity extends ListActivity {

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
		setListAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void addNote(View view) {
		String memoText = editText.getText().toString();
		editText.setText("");

		Note memo = new Note(null, memoText, null, null);
		memoDao.insert(memo);
		Log.d("Memo", "Inserted new memo, ID: " + memo.getId());

		cursor.requery();
	}

	@Override
	public void onListItemClick(ListView list, View view, int position, long id) {
		memoDao.deleteByKey(id);
		cursor.requery();
	}


}
