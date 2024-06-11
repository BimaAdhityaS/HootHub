package com.example.hoothub.activity.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hoothub.R;
import com.example.hoothub.model.user;
import com.example.hoothub.retrofit.ApiInterface;
import com.example.hoothub.retrofit.RetrofitClient;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView btn_login;
    private Button btn_register;

    private EditText et_username, et_email, et_password, et_confirmpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_register = findViewById(R.id.btnSignUp);
        btn_login = findViewById(R.id.txtLogin);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        et_username = (EditText) findViewById(R.id.input_signupUsername);
        et_email = (EditText) findViewById(R.id.input_signupEmail);
        et_password = (EditText) findViewById(R.id.input_signupPassword);
        et_confirmpassword = (EditText) findViewById(R.id.input_signupConfirmPassword);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() == R.id.txtLogin) {
            // Intent to start RegisterActivity
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btnSignUp) {
            btnCreateUserClicked();
        }
    }

    private void btnCreateUserClicked() {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<user>> call = apiInterface.createUser(
                et_username.getText().toString(),
                et_email.getText().toString(),
                et_password.getText().toString(),
                "return=representation"
        );

        call.enqueue(new Callback<List<user>>() {
            @Override
            public void onResponse(Call<List<user>> call, Response<List<user>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    user createdUser = response.body().get(0); // Get the first user
                    Log.d("RegisterActivity", "User created successfully: " + response.body());
                } else {
                    Log.e("RegisterActivity", "Failed to create user: " + response.errorBody());
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