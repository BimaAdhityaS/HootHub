package com.example.hoothub.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoothub.R;
import com.example.hoothub.activity.Activity.CommentFragment;
import com.example.hoothub.activity.Activity.ReplyFragment;
import com.example.hoothub.model.comment;
import com.example.hoothub.model.like_comment;
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

public class ListCommentAdapter extends RecyclerView.Adapter<ListCommentAdapter.ListViewHolder>{
    private Context context;
    private ArrayList<comment> commentList;
    private SharedPreferences sp;
    public ListCommentAdapter(Context context, ArrayList<comment> commentList, SharedPreferences sp){
        this.commentList = commentList;
        this.context = context;
        this.sp = sp;
    }
    @NonNull
    @Override
    public ListCommentAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListCommentAdapter.ListViewHolder holder, int position) {
        comment currentComment = commentList.get(position);
        String userId = sp.getString("user_id", null);
        fetchLikeComment(currentComment.getId(), userId, holder);
        // Use a dummy image from drawable resources
        holder.tvimg.setImageResource(R.drawable.dummy_image);  // Replace 'dummy_image' with your actual drawable resource name

        holder.tvname.setText(currentComment.getUsername());
        holder.tvcontent.setText(currentComment.getContent());
        holder.tvcomment.setText(currentComment.getReply_count() + " replies");
        holder.tvliked.setText(currentComment.getLike_count());
        String formattedDate = formatDate(currentComment.getCreated_at());
        holder.tvtime.setText(formattedDate);

        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment replyfragment = ReplyFragment.newInstance(Integer.parseInt(currentComment.getId()));
                FragmentTransaction fragmentTransaction = ((FragmentActivity) context)
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, replyfragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        holder.btnLike.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (holder.isLiked) {
                    fetchDeleteLike(currentComment.getId(), userId, holder);
                } else {
                    // Check if user exists before adding like
                    fetchAddLike(currentComment.getId(), userId, holder);
                }
            }
        });
    }

    private void fetchAddLike(String id, String userId, ListViewHolder holder) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<like_comment>> call = apiInterface.createLikeComment(
                userId, id, "return=representation"
        );
        call.enqueue(new Callback<List<like_comment>>() {
            @Override
            public void onResponse(Call<List<like_comment>> call, Response<List<like_comment>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Log.d("AddLikeComment", "Like created successfully: " + response.body());
                    holder.isLiked = true;
                    holder.btnLike.setImageResource(R.drawable.like_vektor);
                    int currentLikes = Integer.parseInt(holder.tvliked.getText().toString().split(" ")[0]);
                    holder.tvliked.setText(String.valueOf(currentLikes + 1));
                } else {
                    try {
                        Log.e("AddLikeComment", "Failed to create LikeComment: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("AddLikeComment", "Failed to create LikeComment: errorBody null or not readable", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<like_comment>> call, Throwable throwable) {
                Log.e("AddLikeComment", "API call failed: " + throwable.getMessage(), throwable);
            }
        });
    }

    private void fetchDeleteLike(String id, String userId, ListViewHolder holder) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<like_comment>> call = apiInterface.deleteLikeComment(
                "eq." + userId, "eq." + id
        );
        call.enqueue(new Callback<List<like_comment>>() {
            @Override
            public void onResponse(Call<List<like_comment>> call, Response<List<like_comment>> response) {
                if (response.isSuccessful()) {
                    Log.d("DeleteLikeComment", "Like created successfully: " + response.body());
                    holder.isLiked = false;
                    holder.btnLike.setImageResource(R.drawable.img_like);
                    int currentLikes = Integer.parseInt(holder.tvliked.getText().toString().split(" ")[0]);
                    holder.tvliked.setText(String.valueOf(currentLikes - 1));
                } else {
                    try {
                        Log.e("DeleteLikeComment", "Failed to create LikePost: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("DeleteLikeComment", "Failed to create LikePost: errorBody null or not readable", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<like_comment>> call, Throwable throwable) {
                Log.e("DeleteLikeComment", "API call failed: " + throwable.getMessage(), throwable);
            }
        });
    }

    private void fetchLikeComment(String id, String userId, ListViewHolder holder) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<like_comment>> call = apiInterface.getLikeComment(
                "eq." + userId, "eq." + id, "*"
        );
        call.enqueue(new Callback<List<like_comment>>() {
            @Override
            public void onResponse(Call<List<like_comment>> call, Response<List<like_comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<like_comment> likeComments = response.body();
                    if(likeComments.isEmpty()){
                        holder.isLiked = false;
                        holder.btnLike.setImageResource(R.drawable.img_like);
                    }else{
                        holder.isLiked = true;
                        holder.btnLike.setImageResource(R.drawable.like_vektor);
                    }
                }else{
                    Log.e("FetchLikeComment", "Failed to fetch like status: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<like_comment>> call, Throwable throwable) {
                Log.e("FetchLikeComment", "API call failed: " + throwable.getMessage(), throwable);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
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
        ImageButton btnComment, btnLike;;
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
        }
    }
}
