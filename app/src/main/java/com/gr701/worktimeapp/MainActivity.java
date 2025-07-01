package com.gr701.worktimeapp;

import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.os.Handler;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.Date;
import android.text.format.DateFormat;

import android.util.Log;

public class MainActivity extends AppCompatActivity {
	private String state;
	private TextView timer;
	private Button myButton;
	private long startTime = 0;
	private int currentShiftTime;
	private SharedPreferences prefs;
	private Handler timerHandler = new Handler();
	private Runnable timerRunnable = new Runnable() {
		private boolean isColonShown = true;
		@Override 
		public void run() {
			int workTime = currentShiftTime + (int)(System.currentTimeMillis() - startTime);
			//int seconds = (workTime / 1000) % 60;
			int minutes = (workTime / (1000 * 60)) % 60;
			int hours = workTime / (1000 * 60 * 60);
			String colon = isColonShown ? ":" : " ";
			isColonShown = !isColonShown;
			timer.setText(String.format("%02d%s%02d", hours, colon, minutes));
			timerHandler.postDelayed(this, 1000);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = getSharedPreferences("WorkTimePrefs", MODE_PRIVATE);
		boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
		if (!isLoggedIn) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		setContentView(R.layout.activity_main);
		timer = findViewById(R.id.time_text);
		startTime = prefs.getLong("startTime", 0);
		myButton = findViewById(R.id.work_button);
		RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)myButton.getLayoutParams();
		myButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (state.equals("START") || state.equals("BRAKE") || state.equals("SHIFT_BRAKE")) {
					if (state.equals("START")) {
						String startDate = DateFormat.format("yyyy-MM-dd", new Date()).toString();
						Log.d(" gr701 ", "startDate = " + startDate);
						prefs.edit().putString("startDate", startDate).apply();
					}
					state = "CONFIRM_WORK";
					myButton.setText("Vahvista");
					buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
				} else if (state.equals("CONFIRM_WORK")) {
					state = "WORK";
					startTime = System.currentTimeMillis();
					prefs.edit().putString("state", state).apply();
					prefs.edit().putLong("startTime", startTime).apply();
					myButton.setText("Aloita tauko");
					buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
					timerHandler.post(timerRunnable);
				} else if (state.equals("WORK")) {
					state = "CONFIRM_BRAKE";
					myButton.setText("Vahvista");
					buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
				} else if (state.equals("CONFIRM_BRAKE")) {
					state = "BRAKE";
					prefs.edit().putString("state", state).apply();
					myButton.setText("Lopeta tauko");
					buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
					stopTimer();
					setStaticTimerText();
				}
				myButton.setLayoutParams(buttonParams);
			}
		});
		Button menuButton = findViewById(R.id.menu_button);
		menuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, MenuActivity.class);
				startActivity(intent);
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		state = prefs.getString("state", "START");
		currentShiftTime = prefs.getInt("currentShiftTime", 0);
		if (state.equals("START")) {
			timerHandler.removeCallbacks(timerRunnable);
			//timer.setText("00:00");
			myButton.setText("Aloita työ");
			setStaticTimerText();
		} else if (state.equals("WORK")) {
			startTime = prefs.getLong("startTime", 0);
			timerHandler.removeCallbacks(timerRunnable);
			timerHandler.post(timerRunnable);
			myButton.setText("Aloita tauko");
		} else if (state.equals("BRAKE")) {
			myButton.setText("Lopeta tauko");
			setStaticTimerText();
		} else if (state.equals("START_SHIFT_BRAKE")) {
			state = "SHIFT_BRAKE";
			prefs.edit().putString("state", state).apply();
			myButton.setText("Aloita työ");
			stopTimer();
			setStaticTimerText();
		} else if (state.equals("SHIFT_BRAKE")) {
			myButton.setText("Aloita työ");
			String currentDate = DateFormat.format("yyyy-MM-dd", new Date()).toString();
			String startDate = prefs.getString("startDate", "");
			if (!startDate.equals(currentDate)) {
				saveStatistics();
				state = "START";
				prefs.edit().putString("state", state);
			}
			setStaticTimerText();
		} else if (state.equals("START_SAVE_STAT")) {
			state = "START";
			prefs.edit().putString("state", state).apply();
			stopTimer();
			myButton.setText("Aloita työ");
			saveStatistics();
			setStaticTimerText();
		} else if (state.equals("SAVE_STAT")) {
			state = "START";
			prefs.edit().putString("state", state).apply();
			myButton.setText("Aloita työ");
			saveStatistics();
			setStaticTimerText();
		}
	}
	private void setStaticTimerText() {
		int minutes = (currentShiftTime / (1000 * 60)) % 60;
		int hours = currentShiftTime / (1000 * 60 * 60);
		timer.setText(String.format("%02d:%02d", hours, minutes));
	}
	private void stopTimer() {
		timerHandler.removeCallbacks(timerRunnable);
		currentShiftTime += (int)(System.currentTimeMillis() - startTime);
		prefs.edit().putInt("currentShiftTime", currentShiftTime).apply();
	}
	private void saveStatistics() {
		String statDates = prefs.getString("statDates", "");
		String statTimes = prefs.getString("statTimes", "");
		String startDate = prefs.getString("startDate", "");
		statDates += startDate + ";";
		statTimes += currentShiftTime + ";"; 
		prefs.edit().putString("statDates", statDates).apply();
		prefs.edit().putString("statTimes", statTimes).apply();
		currentShiftTime = 0;
		prefs.edit().putInt("currentShiftTime", currentShiftTime).apply();
	}
}
