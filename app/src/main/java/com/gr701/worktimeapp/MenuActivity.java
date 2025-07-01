package com.gr701.worktimeapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.Date;
import android.text.format.DateFormat;

import android.util.Log;

public class MenuActivity extends AppCompatActivity {
	private SharedPreferences prefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		prefs = getSharedPreferences("WorkTimePrefs", MODE_PRIVATE);
		Button backButton = findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		Button statisticsButton = findViewById(R.id.statistics_button);
		statisticsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this, StatisticsActivity.class);
				startActivity(intent);
			}
		});
		Button endButton = findViewById(R.id.end_button);
		String state = prefs.getString("state", "START");
		if (state.equals("START") || state.equals("SHIFT_BRAKE")) {
			endButton.setEnabled(false);
		} 
		endButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				endWork();
				finish();
			}
		});
		Button logoutButton = findViewById(R.id.logout_button);
		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!state.equals("START")) {
					endWork();
				}
				prefs.edit().putBoolean("isLoggedIn", false).apply();
				Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}
	private void endWork() {
		String state = prefs.getString("state", "START");
		String startDate = prefs.getString("startDate", "0");
		String currentDate = DateFormat.format("yyyy-MM-dd", new Date()).toString();
		Log.d(" gr701 ", "startDate = " + startDate);
		Log.d(" gr701 ", "currentDate = " + currentDate);
		if (startDate.equals(currentDate)) {
			Log.d(" gr701 ", "checked passed");
			if (state.equals("WORK")) {
				prefs.edit().putString("state", "START_SHIFT_BRAKE").apply();
			} else if (state.equals("BRAKE")) {
				prefs.edit().putString("state", "SHIFT_BRAKE").apply();
			}
		} else {
			if (state.equals("WORK")) {
				prefs.edit().putString("state", "START_SAVE_STAT").apply();
			} else if (state.equals("BRAKE")) {
				prefs.edit().putString("state", "SAVE_STAT").apply();
			}
		}
	}
}
