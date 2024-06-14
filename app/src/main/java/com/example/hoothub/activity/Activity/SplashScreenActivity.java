package com.example.hoothub.activity.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.hoothub.R;

public class SplashScreenActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sp = getApplicationContext().getSharedPreferences("userCred", Context.MODE_PRIVATE);
        String user_id = sp.getString("user_id", "");

        new Handler().postDelayed(() -> {
            Intent intent;
            if (user_id.isEmpty()) {
                // If user_id is empty, go to LoginActivity
                intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            } else {
                // If user_id is not empty, go to MainActivity
                intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }, 3000);
    }
}