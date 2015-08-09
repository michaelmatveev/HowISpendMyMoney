package com.michaelmatveev.howispendmymoney;

import java.util.Date;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.test.AndroidTestRunner;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.TextView;

public class TotalFragment extends Fragment
implements PeriodFilter {
	private SQLiteHelper dbHelper;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		dbHelper = new SQLiteHelper(getActivity());
		
		View view = inflater.inflate(android.R.layout.simple_list_item_2, container, false);	
		view.setBackgroundColor(Color.DKGRAY);
		return view;
		//return inflater.inflate(R.layout.total_layout, container, false);
	}

	public void setPeriod(int periodId) {
		//TextView tv = (TextView)this.getView().findViewById(R.id.total_text);
		TextView tv = (TextView)this.getView().findViewById(android.R.id.text1);
		Pair<Date, Date> interval = TimeIntervals.getPeriod(periodId);
		float total = dbHelper.getTotal(interval.first);
		tv.setText(String.format("%s", total));
		tv = (TextView)this.getView().findViewById(android.R.id.text2);
		if(interval.first != null) {
		tv.setText(String.format("%s - %s", 
				TimeIntervals.monthDayFormatter.format(interval.first),
				TimeIntervals.monthDayFormatter.format(interval.second)));
		}
		else {
			tv.setText(getResources().getString(R.string.from_the_beginning));
		}
	}
}
