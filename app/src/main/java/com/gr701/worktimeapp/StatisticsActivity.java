package com.gr701.worktimeapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.GridLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;

import android.util.Log;

public class StatisticsActivity extends AppCompatActivity {
	SharedPreferences prefs;
	String [] dates;
	int [] times;
	int middleDateIndex;
	TextView[][] cellTexts;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		Button backButton = findViewById(R.id.back_button2);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		cellTexts = new TextView[3][2];
		Button leftButton = findViewById(R.id.left_button);
		leftButton.setOnClickListener(new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				if (middleDateIndex - 1 >= 0) {
					middleDateIndex -= 1;
					updateGrid();
				}
			}
		});
		Button rightButton = findViewById(R.id.right_button);
		rightButton.setOnClickListener(new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				if (middleDateIndex + 1 < dates.length) {
					middleDateIndex += 1;
					updateGrid();
				}
			}
		});
		GridLayout grid = findViewById(R.id.day_grid);
		for(int row = 0; row < 2; row++) {
			for(int col = 0; col < 3; col++) {
				TextView cellText = new TextView(this);
				cellText.setText("--");
				cellText.setPadding(15, 15, 15, 15);
				if (col == 1) {
					cellText.setTextColor(Color.parseColor("#000000"));
					cellText.setTextSize(60);
				} else {
					cellText.setTextColor(Color.parseColor("#bbbbbb"));
					cellText.setTextSize(50);
				}
				GridLayout.LayoutParams params = new GridLayout.LayoutParams();
				params.rowSpec = GridLayout.spec(row);
				params.columnSpec = GridLayout.spec(col);
				params.width = GridLayout.LayoutParams.WRAP_CONTENT;
				params.height = GridLayout.LayoutParams.WRAP_CONTENT;
				params.setGravity(Gravity.CENTER);
				cellText.setLayoutParams(params);
				grid.addView(cellText);
				cellTexts[col][row] = cellText;
			}
		}
		prefs = getSharedPreferences("WorkTimePrefs", MODE_PRIVATE);
		String [] dateStrings = prefs.getString("statDates", "").split("[;]");
		String [] timeStrings = prefs.getString("statTimes", "").split("[;]");
		if (dateStrings[0].equals("")) {
			dates = new String[0];
			times = new int[0];
		} else {
			dates = new String[dateStrings.length];
			times = new int[timeStrings.length];
		}
		for (int i = 0; i < dates.length; i++) {
			dates[i] = dateStrings[i].substring(8);
			times[i] = Integer.parseInt(timeStrings[i]) / (1000 * 60 * 60);
		}
		middleDateIndex = dates.length - 1;
		updateGrid();
	}
	private void updateGrid() {
		if (dates.length <= 0) {
			return;
		}
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 3; col++) {
				cellTexts[col][row].setText("--");
			}
		}
		cellTexts[1][0].setText(dates[middleDateIndex]);
		cellTexts[1][1].setText(Integer.toString(times[middleDateIndex]));
		Log.e("gr701", "index = " + Integer.toString(middleDateIndex));
		Log.e("gr701", "dates = " + dates[middleDateIndex]);
		if (middleDateIndex - 1 >= 0) {
			cellTexts[0][0].setText(dates[middleDateIndex - 1]);
			cellTexts[0][1].setText(Integer.toString(times[middleDateIndex - 1]));
		}
		if (middleDateIndex + 1 < dates.length) {
			cellTexts[2][0].setText(dates[middleDateIndex + 1]);
			cellTexts[2][1].setText(Integer.toString(times[middleDateIndex + 1]));
		}
	}
}
