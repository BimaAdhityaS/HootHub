package com.example.hoothub.activity.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoothub.R;
import com.example.hoothub.model.user;
import com.example.hoothub.retrofit.ApiInterface;
import com.example.hoothub.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView btn_register;
    private Button btn_login;

    private EditText et_email, et_password;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btnLogin);
        btn_register = findViewById(R.id.txtRegister);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        et_email = findViewById(R.id.input_loginEmail);
        et_password = findViewById(R.id.input_loginPassword);

        sp = getSharedPreferences("userCred", Context.MODE_PRIVATE);
        getSupportActionBar().hide();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() == R.id.btnLogin) {
            String password = et_password.getText().toString();
            String email = et_email.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "You must fill all the input fields", Toast.LENGTH_SHORT).show();
            } else {
                login();
            }
        } else if (v.getId() == R.id.txtRegister) {
            // Intent to start RegisterActivity
            intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    private void login() {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<user>> call = apiInterface.login(
                "eq."+et_email.getText().toString(),
                "eq."+et_password.getText().toString(),
                "*"
        );

        call.enqueue(new Callback<List<user>>() {
            @Override
            public void onResponse(Call<List<user>> call, Response<List<user>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    user login_data = response.body().get(0); // Get the first user
                    Log.d("LoginActivity", "User login Succesfully: " + response.body());
                    Log.d("LoginActivity", "User ID: " + login_data.getId());

                    //simpan user_id yang login ke sharedpreference
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("user_id", login_data.getId());
                    editor.putString("username", login_data.getUsername());
                    editor.apply();

                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                    // Intent to start RegisterActivity
                    Intent intent;
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Log.e("LoginActivity", "Failed to create user: " + response.errorBody());
                    Toast.makeText(LoginActivity.this, "Your password or email is incorrect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<user>> call, Throwable t) {
                Log.e("RegisterActivity", "API call failed: " + t.getMessage(), t);
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}