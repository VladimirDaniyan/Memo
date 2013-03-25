package com.vladimirdaniyan.android.memo;

import java.util.Calendar;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.google.common.primitives.Ints;

public class TimerReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent alarmIntent) {

		String memoText = alarmIntent.getStringExtra("memoText");
		Long mRowId = alarmIntent.getExtras().getLong("mRowId");
		
		Intent resultIntent = new Intent(context, ListMemoActivity.class);

		PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
				0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context)
				.setSmallIcon(R.drawable.ic_stat_memo)
				.setTicker(memoText)
				.setWhen(Calendar.getInstance().getTimeInMillis())
				.setContentTitle("Timed Memo")
				.setContentText(memoText)
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_ALARM));

		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.setBigContentTitle("Timed Memo").bigText(memoText);
		mBuilder.setStyle(bigTextStyle);
		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(Ints.checkedCast(mRowId), mBuilder.build());

	}

}