package com.example.hoothub.activity.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hoothub.R;
import com.example.hoothub.model.file_image;
import com.example.hoothub.model.reply;
import com.example.hoothub.model.comment;
import com.example.hoothub.model.post;
import com.example.hoothub.model.user;
import com.example.hoothub.retrofit.ApiInterface;
import com.example.hoothub.retrofit.RealPathUtil;
import com.example.hoothub.retrofit.RetrofitClient;
import com.example.hoothub.retrofit.StorageClient;
import com.example.hoothub.retrofit.StorageInterface;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_IMAGE = 1;

    private boolean HaveImage = false;
    private Uri selectedImageUri;

    String path;
    de.hdodenhof.circleimageview.CircleImageView imgUpload;
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

        getWindow().setStatusBarColor(getResources().getColor(R.color.background));

        cancelBtn = findViewById(R.id.btn_cancel_editProfile);
        saveBtn = findViewById(R.id.btn_save_editProfile);

        imgUpload = findViewById(R.id.imgButton);

        input_firstName = findViewById(R.id.formFirstName);
        input_lastName = findViewById(R.id.formLastName);
        input_userName = findViewById(R.id.formUsername);
        input_Bio = findViewById(R.id.formBio);

        sp = getSharedPreferences("userCred", Context.MODE_PRIVATE);
        getCurrentUser();

        cancelBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

        if (!checkPermission()) {
            requestPermission();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cancel_editProfile) {
            finish();
        } else if (v.getId() == R.id.btn_save_editProfile) {
            String firstname = input_firstName.getText().toString();
            String lastname = input_lastName.getText().toString();
            String username = input_userName.getText().toString();
            String Bio = input_Bio.getText().toString();
            if (firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() || Bio.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Please fill all the fields!!", Toast.LENGTH_SHORT).show();
            } else {
                updateUserProfile();
                updateAllPostUsername();
                updateAllCommentUsername();
                updateAllReplyUsername();
                if (selectedImageUri != null && HaveImage == false) {
                    String user_id = sp.getString("user_id", "");
                    uploadImage(path, user_id + ".png");
                }

                if (selectedImageUri != null && HaveImage == true) {
                    String user_id = sp.getString("user_id", "");
                    updateImage(path, user_id + ".png");
                }
                Toast.makeText(getApplicationContext(), "User updated successfully", Toast.LENGTH_SHORT).show();
                finish();
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

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", input_userName.getText().toString());
                    editor.apply();
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

    public void updateAllPostUsername(){
        String user_id = sp.getString("user_id", "");
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<post>> call = apiInterface.updatePostUserName(
                "eq."+user_id,
                input_userName.getText().toString()
        );
        call.enqueue(new Callback<List<post>>() {
            @Override
            public void onResponse(Call<List<post>> call, Response<List<post>> response) {
                if (response.isSuccessful()) {
                    Log.d("Edit Profile", "All Post Successfully Updated");
                }
            }
            @Override
            public void onFailure(Call<List<post>> call, Throwable t) {
                Log.e("Edit Profile", "API call failed: " + t.getMessage(), t);
            }
        });
    }

    public void updateAllCommentUsername(){
        String user_id = sp.getString("user_id", "");
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<comment>> call = apiInterface.updateCommentUserName(
                "eq."+user_id,
                input_userName.getText().toString()
        );
        call.enqueue(new Callback<List<comment>>() {
            @Override
            public void onResponse(Call<List<comment>> call, Response<List<comment>> response) {
                if (response.isSuccessful()) {
                    Log.d("Edit Profile", "All Comment Successfully Updated");
                }
            }
            @Override
            public void onFailure(Call<List<comment>> call, Throwable t) {
                Log.e("Edit Profile", "API call failed: " + t.getMessage(), t);
            }
        });
    }

    public void updateAllReplyUsername(){
        String user_id = sp.getString("user_id", "");
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<reply>> call = apiInterface.updateReplyUserName(
                "eq."+user_id,
                input_userName.getText().toString()
        );
        call.enqueue(new Callback<List<reply>>() {
            @Override
            public void onResponse(Call<List<reply>> call, Response<List<reply>> response) {
                if (response.isSuccessful()) {
                    Log.d("Edit Profile", "All Reply Successfully Updated");
                }
            }
            @Override
            public void onFailure(Call<List<reply>> call, Throwable t) {
                Log.e("Edit Profile", "API call failed: " + t.getMessage(), t);
            }
        });
    }

    public void getCurrentUser() {
        String user_id = sp.getString("user_id", "");

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<user>> call = apiInterface.getCurrentUser(
                "eq." + user_id,
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

                    if (user_data.getImg_profile() != null) {
                        Glide.with(EditProfileActivity.this)
                                .load(user_data.getImg_profile())
                                .placeholder(R.drawable.img_dummyprofilepic) // Optional placeholder image
                                .error(R.drawable.dummy_image) // Optional error image
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true) // Optional skipping memory cache
                                .into(imgUpload);

                        HaveImage = true;
                    }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Context context = EditProfileActivity.this;
            path = RealPathUtil.getRealPath(context, selectedImageUri);

            Glide.with(EditProfileActivity.this)
                    .load(selectedImageUri) // Load the selected image URI directly
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.img_dummyprofilepic)
                    .error(R.drawable.dummy_image)
                    .into(imgUpload);
        }
    }


    public void onImageClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    public void uploadImage(String filePath, String filename) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file_path", file.getName(), requestFile);

        // Base URL of your API
        String baseUrl = "https://ueoyxhouvztjluzxwbvg.supabase.co/storage/v1/object/";
        // Full URL including the endpoint and filename
        String fullUrl = baseUrl + "profile_image/" + filename;

        // Log the full URL
        Log.d("Upload Image", "POST URL: " + fullUrl);

        StorageInterface service = StorageClient.getRetrofitInstance().create(StorageInterface.class);
        Call<file_image> call = service.uploadImage(body, filename);

        call.enqueue(new Callback<file_image>() {
            @Override
            public void onResponse(Call<file_image> call, Response<file_image> response) {
                if (response.isSuccessful()) {
                    file_image uploadedFile = response.body();
                    if (uploadedFile != null) {
                        Log.d("Upload Image", "Image uploaded successfully: " + uploadedFile.getId());
                        // Handle success scenario
                        String url = "https://ueoyxhouvztjluzxwbvg.supabase.co/storage/v1/object/public/" + uploadedFile.getKey();
                        updateUserImage(url);
                    } else {
                        Log.e("Upload Image", "Empty response body");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("Upload Image", "Failed to upload image: " + response.code() + " " + response.message() + response.raw());
                        Log.e("Upload Image", "Error Body: " + errorBody);

                    } catch (IOException e) {
                        Log.e("Upload Image", "IOException while handling error response: " + e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onFailure(Call<file_image> call, Throwable t) {
                Log.e("Upload Image", "API call failed: " + t.getMessage(), t);
            }
        });
    }

    public void updateImage(String filePath, String filename) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file_path", file.getName(), requestFile);

        // Base URL of your API
        String baseUrl = "https://ueoyxhouvztjluzxwbvg.supabase.co/storage/v1/object/";
        // Full URL including the endpoint and filename
        String fullUrl = baseUrl + "profile_image/" + filename;

        // Log the full URL
        Log.d("Upload Image", "POST URL: " + fullUrl);

        StorageInterface service = StorageClient.getRetrofitInstance().create(StorageInterface.class);
        Call<file_image> call = service.updateImage(body, filename);

        call.enqueue(new Callback<file_image>() {
            @Override
            public void onResponse(Call<file_image> call, Response<file_image> response) {
                if (response.isSuccessful()) {
                    file_image uploadedFile = response.body();
                    if (uploadedFile != null) {
                        Log.d("Upload Existing Image", "Image changed successfully: " + uploadedFile.getId());
                        // Handle success scenario
                        String url = "https://ueoyxhouvztjluzxwbvg.supabase.co/storage/v1/object/public/" + uploadedFile.getKey();
                        updateUserImage(url);
                    } else {
                        Log.e("Upload Existing Image", "Empty response body");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("Upload Existing Image", "Failed to upload image: " + response.code() + " " + response.message() + response.raw());
                        Log.e("Upload Existing Image", "Error Body: " + errorBody);

                    } catch (IOException e) {
                        Log.e("Upload Existing Image", "IOException while handling error response: " + e.getMessage(), e);
                    }
                }
            }

            @Override
            public void onFailure(Call<file_image> call, Throwable t) {
                Log.e("Upload Image", "API call failed: " + t.getMessage(), t);
            }
        });
    }

    public void updateUserImage(String url){
        String user_id = sp.getString("user_id", "");
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<user>> call = apiInterface.updateUserImgProfile(
                "eq."+user_id,
                url
        );

        call.enqueue(new Callback<List<user>>() {
            @Override
            public void onResponse(Call<List<user>> call, Response<List<user>> response) {
                if (response.isSuccessful()) {
                    Log.d("Update Image User", "User Image Updated!!!");
                } else {
                    Log.e("Update Image User", "Failed to update user profile image: " + response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<List<user>> call, Throwable t) {
                Log.e("Update Image User", "API call failed: " + t.getMessage(), t);
            }
        });
    }

    private boolean checkPermission() {
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (readAccepted && writeAccepted) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    (dialog, which) -> requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            PERMISSION_REQUEST_CODE));
                            return;
                        }
                    }
                }
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(EditProfileActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void openGallery(View view) {
        if (checkPermission()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        } else {
            requestPermission();
        }
    }
}