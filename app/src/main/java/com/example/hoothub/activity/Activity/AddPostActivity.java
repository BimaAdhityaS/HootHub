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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hoothub.R;
import com.example.hoothub.model.post;
import com.example.hoothub.model.user;
import com.example.hoothub.retrofit.ApiInterface;
import com.example.hoothub.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPostActivity extends AppCompatActivity implements View.OnClickListener {

    private de.hdodenhof.circleimageview.CircleImageView imgProfile;
    private Button btn_cancel, btn_post;
    private EditText et_post;
    private String postIdIntent = "",contentIntent = "";
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Intent intent = getIntent();
        if (intent != null) {
            postIdIntent = intent.getStringExtra("POST_ID");
            contentIntent = intent.getStringExtra("POST_CONTENT");
        }

        imgProfile = findViewById(R.id.addPost_profile_image);
        sp = getSharedPreferences("userCred", Context.MODE_PRIVATE);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_post = findViewById(R.id.btn_post);
        et_post = findViewById(R.id.et_post);
        if(contentIntent != null && !contentIntent.isEmpty()){
            et_post.setText(contentIntent);
        }
        btn_cancel.setOnClickListener(this);
        btn_post.setOnClickListener(this);

        getCurrentUser();
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
                if ((postIdIntent == null || postIdIntent.isEmpty()) && (contentIntent == null || contentIntent.isEmpty())) {
                    btnCreateContentClicked();
                }
                else {
                    btnUpdateContentClicked();
                }
            }
        }
    }

    private void btnUpdateContentClicked() {
        String content = et_post.getText().toString();
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        if(postIdIntent.isEmpty()){
            Toast.makeText(this, "Cannot Edit this Post", Toast.LENGTH_SHORT).show();
        }
        Call<List<post>> call = apiInterface.updateContentPost(
                "eq."+postIdIntent, content,"return=representation"
        );
        call.enqueue(new Callback<List<post>>() {
            @Override
            public void onResponse(Call<List<post>> call, Response<List<post>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()){
                    Log.d("EditPost", "Content Updated successfully: " + response.body());
                    Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Log.e("EditPost", "Failed to update post: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<post>> call, Throwable throwable) {
                Log.e("EditPost", "API call failed: " + throwable.getMessage(), throwable);
            }
        });
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
                    Log.d("Profile", "Current User Profile: " + response.body());
                    Log.d("Profile", "User ID: " + user_data.getId());
                    Glide.with(AddPostActivity.this)
                            .load(user_data.getImg_profile())
                            .placeholder(R.drawable.img_dummyprofilepic) // optional placeholder image
                            .error(R.drawable.dummy_image) // optional error image
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imgProfile);
                } else {
                    Log.e("Profile", "Failed to fetch user data: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<user>> call, Throwable t) {
                Log.e("Profile", "API call failed: " + t.getMessage(), t);
            }
        });
    }
}
