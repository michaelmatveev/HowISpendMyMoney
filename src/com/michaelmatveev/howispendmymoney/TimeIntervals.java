package com.michaelmatveev.howispendmymoney;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Pair;

public class TimeIntervals {
	
	/*public static String getIntervalAsString(int interval) {
		Date start = getPeriod(interval);
		Date finish = Calendar.getInstance().get
		
		return "Time";
	}*/
	
	public static Pair<Date, Date> getPeriod(int position) {
		Calendar cal = Calendar.getInstance();
		Date finish = cal.getTime();
		//cal.setTime();
		/*
        <item>All periods</item>
        <item>Week</item>
        <item>Two weeks</item>
        <item>Month</item>
        <item>Two months</item>
        <item>Three months</item>
        <item>Six months</item>
        <item>Year</item>
        */
		switch (position) {
		case 1:
			cal.add(Calendar.DATE, -7);
			break;
		case 2:
			cal.add(Calendar.DATE, -14);
			break;
		case 3:
			cal.add(Calendar.MONTH, -1);
			break;
		case 4:
			cal.add(Calendar.MONTH, -2);
			break;
		case 5:
			cal.add(Calendar.MONTH, -3);
			break;
		case 6:
			cal.add(Calendar.MONTH, -6);
			break;
		case 7:
			cal.add(Calendar.YEAR, -1);
			break;

		default:
			return new Pair<Date, Date>(null, null);
		}
		Date start = cal.getTime();
		return new Pair<Date, Date>(start, finish);
	}

	public static final SimpleDateFormat monthDayFormatter = new SimpleDateFormat("MMMMM dd");
}
