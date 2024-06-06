package com.example.hoothub.activity.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hoothub.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView btn_register;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btnLogin);
        btn_register = findViewById(R.id.txtRegister);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() == R.id.btnLogin) {
            // Intent to start LoginActivity
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.txtRegister) {
            // Intent to start RegisterActivity
            intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}