package com.vladimirdaniyan.android.memo;

import android.content.Context;


public class FormatAlarmText  {
	
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
