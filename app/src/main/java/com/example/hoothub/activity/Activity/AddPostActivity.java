package com.example.hoothub.activity.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hoothub.R;
import com.example.hoothub.model.post;
import com.example.hoothub.retrofit.ApiInterface;
import com.example.hoothub.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPostActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_cancel, btn_post;
    private EditText et_post;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        sp = getSharedPreferences("userCred", Context.MODE_PRIVATE);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_post = findViewById(R.id.btn_post);
        et_post = findViewById(R.id.et_post);
        btn_cancel.setOnClickListener(this);
        btn_post.setOnClickListener(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_cancel) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btn_post) {
            String content = et_post.getText().toString();
            if (content.isEmpty()) {
                Toast.makeText(AddPostActivity.this, "Please fill your content", Toast.LENGTH_SHORT).show();
            } else {
                btnCreateContentClicked();
            }
        }
    }

    private void btnCreateContentClicked() {
        String content = et_post.getText().toString();
        String userId = sp.getString("user_id", null);
        String username = sp.getString("username",null);
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<post>> call = apiInterface.createPost(
                content, userId, username,"return=representation"
        );

        call.enqueue(new Callback<List<post>>() {
            @Override
            public void onResponse(Call<List<post>> call, Response<List<post>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Log.d("AddPost", "Content created successfully: " + response.body());
                    Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Log.e("AddPost", "Failed to create post: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<post>> call, Throwable t) {
                Log.e("AddPost", "API call failed: " + t.getMessage(), t);
            }
        });
    }
}
