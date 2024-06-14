package com.example.hoothub.activity.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.hoothub.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_logout = findViewById(R.id.btnlogout);
        btn_logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnlogout){
            SharedPreferences sp = getApplicationContext().getSharedPreferences("userCred", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("user_id", "");
            editor.commit();
        }
    }
}