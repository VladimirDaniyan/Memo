package com.azazeleleven.android.memo;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity {

	private SQLiteDatabase db;

	private NoteDao memoDao;

	private Cursor cursor;

	public static final CharSequence PREF_NAME = "pref_name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	public void addNote(MenuItem item) {
		Intent intent = new Intent(this, EditMemoActivity.class);
		startActivity(intent);
	}

	@Override
	public void onListItemClick(ListView list, View view, int position, long id) {
		memoDao.deleteByKey(id);
		cursor.requery();
	}

}
