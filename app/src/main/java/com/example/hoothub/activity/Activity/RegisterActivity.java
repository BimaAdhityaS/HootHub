package com.example.hoothub.activity.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hoothub.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView btn_login;
    private Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_register = findViewById(R.id.btnSignUp);
        btn_login = findViewById(R.id.txtLogin);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() == R.id.btnSignUp) {
            // Intent to start LoginActivity
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.txtLogin) {
            // Intent to start RegisterActivity
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}