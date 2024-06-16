package com.example.hoothub.activity.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoothub.R;
import com.example.hoothub.model.user;
import com.example.hoothub.retrofit.ApiInterface;
import com.example.hoothub.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    Button cancelBtn, saveBtn;
    EditText input_firstName, input_lastName, input_userName, input_Bio;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.actionbar_layout);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DBEDF3")));
            View customActionBarView = getSupportActionBar().getCustomView();
            customActionBarView.setPadding(32, 32, 32, 32);
        }

        cancelBtn = findViewById(R.id.btn_cancel_editProfile);
        saveBtn = findViewById(R.id.btn_save_editProfile);

        input_firstName = findViewById(R.id.formFirstName);
        input_lastName = findViewById(R.id.formLastName);
        input_userName = findViewById(R.id.formUsername);
        input_Bio = findViewById(R.id.formBio);

        sp = getSharedPreferences("userCred", Context.MODE_PRIVATE);
        getCurrentUser();

        cancelBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cancel_editProfile) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_save_editProfile) {
            String firstname = input_firstName.getText().toString();
            String lastname = input_lastName.getText().toString();
            String username = input_userName.getText().toString();
            String Bio = input_Bio.getText().toString();
            if (firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() || Bio.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Please fill all the fields!!", Toast.LENGTH_SHORT).show();
            } else {
                updateUserProfile();
            }
        }
    }

    public void updateUserProfile(){
        String user_id = sp.getString("user_id", "");

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<user>> call = apiInterface.updateUser(
                "eq."+user_id,
                input_firstName.getText().toString(),
                input_lastName.getText().toString(),
                input_userName.getText().toString(),
                input_Bio.getText().toString()
        );

        call.enqueue(new Callback<List<user>>() {
            @Override
            public void onResponse(Call<List<user>> call, Response<List<user>> response) {
                if (response.isSuccessful()) {
                    Log.d("Edit Profile", "User Updated!!!");

                    Intent intent;
                    intent = new Intent(EditProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Log.e("Edit Profile", "Failed to update user data: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<user>> call, Throwable t) {
                Log.e("Edit Profile", "API call failed: " + t.getMessage(), t);
            }
        });
    }

    public void getCurrentUser(){
        String user_id = sp.getString("user_id", "");

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<user>> call = apiInterface.getCurrentUser(
                "eq."+user_id,
                "*"
        );

        call.enqueue(new Callback<List<user>>() {
            @Override
            public void onResponse(Call<List<user>> call, Response<List<user>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    user user_data = response.body().get(0); // Get the first user
                    Log.d("Edit Profile", "Current User Profile: " + response.body());
                    Log.d("Edit Profile", "User ID: " + user_data.getId());

                    input_firstName.setText(user_data.getFirstName());
                    input_lastName.setText(user_data.getLastName());
                    input_userName.setText(user_data.getUsername());
                    input_Bio.setText(user_data.getBio());

                } else {
                    Log.e("Edit Profile", "Failed to fetch user data: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<user>> call, Throwable t) {
                Log.e("Edit Profile", "API call failed: " + t.getMessage(), t);
            }
        });
    }
}