package com.example.hoothub.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.hoothub.R;
import com.example.hoothub.activity.Activity.CommentFragment;
import com.example.hoothub.model.like_post;
import com.example.hoothub.model.post;
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
        holder.tvimg.setImageResource(R.drawable.dummy_image);

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
                            showReportDialog();
                        }
                        return false;
                    });
                }else{
                    popupMenu.inflate(R.menu.popup_edit_delete_menu);
                    popupMenu.setOnMenuItemClickListener(item -> {
                        if(item.getItemId() == R.id.edit_content){
                            Toast.makeText(context, "You Edit this Content",Toast.LENGTH_SHORT).show();
                        } else if (item.getItemId() == R.id.delete_content) {
                            Toast.makeText(context, "You Delete this Content",Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    });
                }
                popupMenu.show();
            });


    }

    private void showReportDialog() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_report, null);
        RadioGroup radioGroupReport = dialogView.findViewById(R.id.radioGroupReport);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Report Post");
        builder.setView(dialogView);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            // Handle the report submission
            int selectedId = radioGroupReport.getCheckedRadioButtonId();
            String reportType = "";
            if(selectedId == R.id.radioSpam){
                reportType= "Spam";
            } else if (selectedId == R.id.radioInappropriate) {
                reportType = "inapriorite";
            }else if(selectedId == R.id.radioOther){
                reportType= "Other";
            }
            Log.d("ReportDialog", "Report type: " + reportType);
            // Add code to handle report submission
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
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
