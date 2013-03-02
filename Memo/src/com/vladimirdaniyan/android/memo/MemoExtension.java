package com.vladimirdaniyan.android.memo;

import android.content.Intent;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.vladimirdaniyan.android.memo.R;

public class MemoExtension extends DashClockExtension {

	@Override
	protected void onUpdateData(int reason) {
		final Intent addMemoIntent = getAddMemoIntent();

		publishUpdate(new ExtensionData().visible(true)
				.icon(R.drawable.ic_stat_memo).status("Add Memo")
				.clickIntent(addMemoIntent));

	}

	private Intent getAddMemoIntent() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setClassName("com.vladimirdaniyan.android.memo",
				"com.vladimirdaniyan.android.memo.EditMemoActivity");
		return intent;

	}

}
