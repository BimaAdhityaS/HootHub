package com.example.hoothub.activity.Activity;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoothub.R;
import com.example.hoothub.model.reply;
import com.example.hoothub.retrofit.ApiInterface;
import com.example.hoothub.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddReplyActivity extends AppCompatActivity implements View.OnClickListener{
    private String comment_id;
    private Button btn_cancel_reply, btn_post_reply;
    private EditText et_reply;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_reply);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("comment_id")){
            comment_id = intent.getStringExtra("comment_id");
            Log.d("AddReplyActivity", "Received comment: " + comment_id);
        }
        sp = getSharedPreferences("userCred", Context.MODE_PRIVATE);
        btn_cancel_reply = findViewById(R.id.btn_cancel_reply);
        btn_post_reply = findViewById(R.id.btn_post_reply);
        et_reply = findViewById(R.id.et_reply);
        btn_cancel_reply.setOnClickListener(this);
        btn_post_reply.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_cancel_reply){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btn_post_reply) {
            String content = et_reply.getText().toString();
            if(content.isEmpty()){
                Toast.makeText(AddReplyActivity.this,"Please Fill Your Content",Toast.LENGTH_SHORT).show();
            }else{
                btnCreateContentClicked();
            }
        }
    }

    private void btnCreateContentClicked() {
        String content = et_reply.getText().toString();
        String userId = sp.getString("user_id", null);
        String username = sp.getString("username",null);
        if(comment_id == null){
            Log.e("AddReplyActivity", "Comment_id is null, cannot create reply");
            Toast.makeText(this, "Error: Comment ID is null", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<reply>> call = apiInterface.createReply(
                comment_id, userId, username, content,"return=representation"
        );
        call.enqueue(new Callback<List<reply>>() {
            @Override
            public void onResponse(Call<List<reply>> call, Response<List<reply>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()){
                    Log.d("AddReply", "Content created successfully: " + response.body());
                    Intent intent = new Intent(AddReplyActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Log.e("AddReply", "Failed to create reply: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<reply>> call, Throwable throwable) {
                Log.e("Addreply", "API call failed: " + throwable.getMessage(), throwable);
            }
        });
    }
}