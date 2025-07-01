package com.gr701.worktimeapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.util.Log;

public class LoginActivity extends AppCompatActivity {
	private String password = "0000";
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Button loginButton = findViewById(R.id.login_button);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String input = ((EditText)findViewById(R.id.password_field)).getText().toString();
				if (input.equals(password)) {
					SharedPreferences prefs = getSharedPreferences("WorkTimePrefs", MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putBoolean("isLoggedIn", true);
					editor.apply();
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
					return;
				}
			}
		});
	}
}
