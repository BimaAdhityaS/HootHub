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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoothub.R;
import com.example.hoothub.model.comment;
import com.example.hoothub.model.post;
import com.example.hoothub.retrofit.ApiInterface;
import com.example.hoothub.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCommentActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btn_cancel, btn_post;
    private EditText et_post;
    private String post_id;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("post_id")){
            post_id = intent.getStringExtra("post_id");
            Log.d("AddCommentActivity", "Received post_id: " + post_id);
        }
        sp = getSharedPreferences("userCred", Context.MODE_PRIVATE);

        btn_cancel = findViewById(R.id.btn_cancel_comment);
        btn_post = findViewById(R.id.btn_post_comment);
        et_post = findViewById(R.id.et_comment);
        btn_cancel.setOnClickListener(this);
        btn_post.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_cancel_comment) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else if (view.getId() == R.id.btn_post_comment) {
            String content = et_post.getText().toString();
            if (content.isEmpty()) {
                Toast.makeText(AddCommentActivity.this, "Please fill your content", Toast.LENGTH_SHORT).show();
            }else {
                btnCreateContentClicked();
            }
        }
    }

    private void btnCreateContentClicked() {
        String content = et_post.getText().toString();
        String userId = sp.getString("user_id", null);
        String username = sp.getString("username",null);
        if (post_id == null) {
            Log.e("AddCommentActivity", "post_id is null, cannot create comment");
            Toast.makeText(this, "Error: Post ID is null", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<comment>> call = apiInterface.createComment(
                post_id, userId, username, content, "return=representation"
        );
        call.enqueue(new Callback<List<comment>>() {
            @Override
            public void onResponse(Call<List<comment>> call, Response<List<comment>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Log.d("AddComment", "Content created successfully: " + response.body());

                    Intent intent = new Intent(AddCommentActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Log.e("AddComment", "Failed to create comment: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<comment>> call, Throwable t) {
                Log.e("AddPost", "API call failed: " + t.getMessage(), t);
            }
        });
    }
}