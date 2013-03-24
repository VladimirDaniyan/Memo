package com.vladimirdaniyan.android.memo;

import com.google.common.primitives.Ints;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class TimerReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent alarmIntent) {

		String memoText = alarmIntent.getStringExtra("memoText");
		String reminderTime = alarmIntent.getStringExtra("reminder_time");
		Long mRowId = alarmIntent.getExtras().getLong("mRowId");
		
		Log.d("POST ALARM TEXT", memoText);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context)
				.setSmallIcon(R.drawable.ic_stat_memo)
				.setContentTitle("Reminder set for " + reminderTime)
				.setContentText(memoText)
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_ALARM));
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.setBigContentTitle("Reminder set for " + reminderTime)
				.bigText(memoText);
		mBuilder.setStyle(bigTextStyle);

		Intent resultIntent = new Intent(context, EditMemoActivity.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
				0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(Ints.checkedCast(mRowId), mBuilder.build());

	}

}