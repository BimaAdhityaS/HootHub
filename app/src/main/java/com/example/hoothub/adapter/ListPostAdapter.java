package com.example.hoothub.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hoothub.R;
import com.example.hoothub.activity.Activity.AddPostActivity;
import com.example.hoothub.activity.Activity.CommentFragment;
import com.example.hoothub.activity.Activity.OtherProfileFragment;
import com.example.hoothub.activity.Activity.PostFragment;
import com.example.hoothub.model.like_post;
import com.example.hoothub.model.post;
import com.example.hoothub.model.report;
import com.example.hoothub.model.user;
import com.example.hoothub.retrofit.ApiInterface;
import com.example.hoothub.retrofit.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListPostAdapter extends RecyclerView.Adapter<ListPostAdapter.ListViewHolder> {
    private ArrayList<post> postList;
    private Context context;
    private SharedPreferences sp;

    public ListPostAdapter(Context context, ArrayList<post> list, SharedPreferences sp) {
        this.context = context;
        this.postList = list;
        this.sp = sp;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        post currentPost = postList.get(position);
        String userId = sp.getString("user_id", null);

        getCurrentUser(currentPost.getUser_id(), holder);

        holder.tvname.setText(currentPost.getUser_name());
        holder.tvcontent.setText(currentPost.getContent());
        holder.tvcomment.setText(currentPost.getComment_count() + " comments");
        holder.tvliked.setText(currentPost.getLike_count());
        String formattedDate = formatDate(currentPost.getCreated_at());
        holder.tvtime.setText(formattedDate);
        fetchLikePost(currentPost.getId(), userId, holder);
        holder.btnComment.setOnClickListener(view -> {
            Fragment commentFragment = CommentFragment.newInstance(Integer.parseInt(currentPost.getId()));
            FragmentTransaction fragmentTransaction = ((FragmentActivity) context)
                    .getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, commentFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        holder.tvimg.setOnClickListener(view -> {
            Fragment otherProfileFragment = new OtherProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putString("user_id", currentPost.getUser_id()); // Pass user ID to the fragment
            otherProfileFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = ((FragmentActivity) context)
                    .getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, otherProfileFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        holder.btnLike.setOnClickListener(view -> {
            if (holder.isLiked) {
                fetchDeleteLike(currentPost.getId(), userId, holder);
            } else {
                // Check if user exists before adding like
                fetchAddLike(currentPost.getId(), userId, holder);
            }
        });
            holder.btnOption.setImageResource(R.drawable.options);
            holder.btnOption.setOnClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(context, holder.btnOption);
                if(userId != null && !userId.equals(String.valueOf(currentPost.getUser_id()))) {
                    popupMenu.inflate(R.menu.popup_report_menu);
                    popupMenu.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == R.id.report) {
                            Log.d("PopupMenu", "Edit clicked");
                            showReportDialog(currentPost.getId(), currentPost.getUser_id());
                        }
                        return false;
                    });
                }else{
                    popupMenu.inflate(R.menu.popup_edit_delete_menu);
                    popupMenu.setOnMenuItemClickListener(item -> {
                        if(item.getItemId() == R.id.edit_content){
                            fetchEditPostById(currentPost.getId(), currentPost.getContent());
                        } else if (item.getItemId() == R.id.delete_content) {
                            fetchDeletePostById(currentPost.getId(), userId);

                        }
                        return false;
                    });
                }
                popupMenu.show();
            });
    }
    public void getCurrentUser(String user_id, ListViewHolder holder){

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
                    Glide.with(holder.itemView.getContext())
                            .load(user_data.getImg_profile())
                            .placeholder(R.drawable.img_dummyprofilepic) // optional placeholder image
                            .error(R.drawable.dummy_image) // optional error image
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(holder.tvimg);
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
    private void showReportDialog(String postId, String userId) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_report, null);
        RadioGroup radioGroupReport = dialogView.findViewById(R.id.radioGroupReport);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        TextView tvDescription = dialogView.findViewById(R.id.tvDescription);

        radioGroupReport.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioOther) {
                    tvDescription.setVisibility(View.VISIBLE);
                    etDescription.setVisibility(View.VISIBLE);
                } else {
                    tvDescription.setVisibility(View.GONE);
                    etDescription.setVisibility(View.GONE);
                }
            }
        });

        AlertDialog.Builder builder = getBuilder(dialogView, radioGroupReport, etDescription, postId, userId);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        });
        builder.create().show();
    }

    @NonNull
    private AlertDialog.Builder getBuilder(View dialogView, RadioGroup radioGroupReport, EditText etDescription, String postId, String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Report Post");
        builder.setView(dialogView);


        builder.setPositiveButton("Submit", (dialog, which) -> {
            // Handle the report submission
            int selectedId = radioGroupReport.getCheckedRadioButtonId();
            String reportType = getString(selectedId);
            String description = etDescription.getText().toString();
            Log.d("ReportDialog", "Report type: " + reportType);
            Log.d("ReportDialog", "Description: " + description);
            if(reportType == "Other") {
                fetchReportPost(description, postId, userId);
            }else{
                fetchReportPost(reportType, postId, userId);
            }
            // Add code to handle report submission
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        return builder;
    }

    private void fetchReportPost(String reason, String postId, String userId) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<report>> call = apiInterface.createPostReport(
                userId, postId, reason, "return=representation"
        );
        call.enqueue(new Callback<List<report>>() {
            @Override
            public void onResponse(Call<List<report>> call, Response<List<report>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "SuccessFully Report", Toast.LENGTH_LONG).show();
                    Log.d("response", String.valueOf(response));
                }else{
                    Toast.makeText(context, "UnSuccessFully Report", Toast.LENGTH_SHORT).show();
                    Log.d("response", String.valueOf(response));
                }
            }

            @Override
            public void onFailure(Call<List<report>> call, Throwable throwable) {
                Log.e("ReportPost", "API call failed: " + throwable.getMessage(), throwable);
            }
        });
    }

    @NonNull
    private static String getString(int selectedId) {
        String reportType = "";
        if (selectedId == R.id.radioAbusive) {
            reportType = "Abusive or rude behaviour";
        } else if (selectedId == R.id.radioFraud) {
            reportType = "Possible Fraud";
        } else if (selectedId == R.id.radioPrivacy) {
            reportType = "Privacy Violation";
        } else if (selectedId == R.id.radioSpam) {
            reportType = "Spam";
        } else if (selectedId == R.id.radioOther) {
            reportType = "Other";
        }
        return reportType;
    }

    private void fetchEditPostById(String postId, String content){
            Intent intent = new Intent(context, AddPostActivity.class);
            intent.putExtra("POST_ID", postId);
            intent.putExtra("POST_CONTENT", content);
            context.startActivity(intent);
    }

    private  void fetchDeletePostById(String postId, String userId){
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<post>> call = apiInterface.deletContentPost(
                "eq." + postId, "eq." + userId
        );
        call.enqueue(new Callback<List<post>>() {
            @Override
            public void onResponse(Call<List<post>> call, Response<List<post>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Delete SuccessFully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Delete UnSuccessFully", Toast.LENGTH_SHORT).show();
                }

                FragmentTransaction fragmentTransaction = ((FragmentActivity) context)
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new PostFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

            @Override
            public void onFailure(Call<List<post>> call, Throwable throwable) {
                Log.e("DeleteContentPost", "API call failed: " + throwable.getMessage(), throwable);
            }
        });
    }
    private void fetchAddLike(String postId, String userId, ListViewHolder holder) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<like_post>> call = apiInterface.createLikePost(userId, postId,"return=representation");
        call.enqueue(new Callback<List<like_post>>() {
            @Override
            public void onResponse(Call<List<like_post>> call, Response<List<like_post>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Log.d("AddLikePost", "Like created successfully: " + response.body());
                    holder.isLiked = true;
                    holder.btnLike.setImageResource(R.drawable.like_vektor);
                    int currentLikes = Integer.parseInt(holder.tvliked.getText().toString().split(" ")[0]);
                    holder.tvliked.setText(String.valueOf(currentLikes + 1));
                } else {
                    try {
                        Log.e("AddLikePost", "Failed to create LikePost: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("AddLikePost", "Failed to create LikePost: errorBody null or not readable", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<like_post>> call, Throwable throwable) {
                Log.e("AddLikePost", "API call failed: " + throwable.getMessage(), throwable);
            }
        });

    }

    private void fetchDeleteLike(String postId, String userId, ListViewHolder holder) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<like_post>> call = apiInterface.deleteLikePost("eq." + userId, "eq." + postId);
        call.enqueue(new Callback<List<like_post>>() {
            @Override
            public void onResponse(Call<List<like_post>> call, Response<List<like_post>> response) {
                if (response.isSuccessful()) {
                    Log.d("DeleteLikePost", "Like created successfully: " + response.body());
                    holder.isLiked = false;
                    holder.btnLike.setImageResource(R.drawable.img_like);
                    int currentLikes = Integer.parseInt(holder.tvliked.getText().toString().split(" ")[0]);
                    holder.tvliked.setText(String.valueOf(currentLikes - 1));
                } else {
                    try {
                        Log.e("DeleteLikePost", "Failed to create LikePost: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("DeleteLikePost", "Failed to create LikePost: errorBody null or not readable", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<like_post>> call, Throwable throwable) {
                Log.e("DeleteLikePost", "API call failed: " + throwable.getMessage(), throwable);
            }
        });
    }

    private void fetchLikePost(String postId, String userId, ListViewHolder holder) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<like_post>> call = apiInterface.getLikePost("eq." + userId, "eq." + postId, "*");
        call.enqueue(new Callback<List<like_post>>() {
            @Override
            public void onResponse(Call<List<like_post>> call, Response<List<like_post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<like_post> likePosts = response.body();
                    if (likePosts.isEmpty()) {
                        holder.isLiked = false;
                        holder.btnLike.setImageResource(R.drawable.img_like);
                    } else {
                        holder.isLiked = true;
                        holder.btnLike.setImageResource(R.drawable.like_vektor);
                    }
                } else {
                    Log.e("FetchLikePost", "Failed to fetch like status: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<like_post>> call, Throwable throwable) {
                Log.e("FetchLikePost", "API call failed: " + throwable.getMessage(), throwable);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    private String formatDate(String inputDateString) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.ENGLISH);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm a", Locale.ENGLISH);
        try {
            Date date = inputDateFormat.parse(inputDateString);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDateString; // Return the original string in case of error
        }
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        public boolean isLiked = false;
        TextView tvname, tvcontent, tvtime, tvliked, tvcomment;
        ImageButton btnComment, btnLike , btnOption;
        ImageView tvimg;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvname = itemView.findViewById(R.id.name);
            tvcomment = itemView.findViewById(R.id.post_comment);
            tvcontent = itemView.findViewById(R.id.message);
            tvtime = itemView.findViewById(R.id.time);
            tvliked = itemView.findViewById(R.id.post_liked);
            tvimg = itemView.findViewById(R.id.user_profile_image1);
            btnComment = itemView.findViewById(R.id.comment);
            btnLike = itemView.findViewById(R.id.like);
            btnOption = itemView.findViewById(R.id.options);
        }
    }
}
