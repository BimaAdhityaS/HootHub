package com.example.hoothub.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoothub.R;
import com.example.hoothub.model.comment;
import com.example.hoothub.model.like_post;
import com.example.hoothub.model.like_reply;
import com.example.hoothub.model.reply;
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

public class ListReplyAdapter extends RecyclerView.Adapter<ListReplyAdapter.ListViewHolder>{
    private Context context;
    private ArrayList<reply> replylist;
    private SharedPreferences sp;

    public ListReplyAdapter(Context context, ArrayList<reply> replylist, SharedPreferences sp) {
        this.context = context;
        this.replylist = replylist;
        this.sp = sp;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListReplyAdapter.ListViewHolder holder, int position) {
        reply currentReply = replylist.get(position);
        String userId = sp.getString("user_id", null);
        fetchLikeReply(currentReply.getId(), userId, holder);
        // Use a dummy image from drawable resources
        holder.tvimg.setImageResource(R.drawable.dummy_image);  // Replace 'dummy_image' with your actual drawable resource name

        holder.tvname.setText(currentReply.getUsername());
        holder.tvcontent.setText(currentReply.getContent());

        holder.tvliked.setText(currentReply.getLike_count());
        String formattedDate = formatDate(currentReply.getCreated_at());
        holder.tvtime.setText(formattedDate);
        holder.btnLike.setOnClickListener(view -> {
            if (holder.isLiked) {
                fetchDeleteLike(currentReply.getId(), userId, holder);
            } else {
                // Check if user exists before adding like
                fetchAddLike(currentReply.getId(), userId, holder);
            }
        });

    }

    private void fetchAddLike(String id, String userId, ListViewHolder holder) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<like_reply>> call = apiInterface.createLikeReply(
                userId, id, "return=representation"
        );
        call.enqueue(new Callback<List<like_reply>>() {
            @Override
            public void onResponse(Call<List<like_reply>> call, Response<List<like_reply>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Log.d("AddLikeReply", "Like created successfully: " + response.body());
                    holder.isLiked = true;
                    holder.btnLike.setImageResource(R.drawable.like_vektor);
                    int currentLikes = Integer.parseInt(holder.tvliked.getText().toString().split(" ")[0]);
                    holder.tvliked.setText(String.valueOf(currentLikes + 1));
                } else {
                    try {
                        Log.e("AddLikeReply", "Failed to create LikeReply: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("AddLikeReply", "Failed to create LikeReply: errorBody null or not readable", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<like_reply>> call, Throwable throwable) {
                Log.e("AddLikeReply", "API call failed: " + throwable.getMessage(), throwable);            }
        });
    }

    private void fetchDeleteLike(String id, String userId, ListViewHolder holder) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<like_reply>> call = apiInterface.deleteLikeReply(
                "eq." + userId, "eq." + id
        );
        call.enqueue(new Callback<List<like_reply>>() {
            @Override
            public void onResponse(Call<List<like_reply>> call, Response<List<like_reply>> response) {
                if (response.isSuccessful()) {
                    Log.d("DeleteLikeReply", "Like created successfully: " + response.body());
                    holder.isLiked = false;
                    holder.btnLike.setImageResource(R.drawable.img_like);
                    int currentLikes = Integer.parseInt(holder.tvliked.getText().toString().split(" ")[0]);
                    holder.tvliked.setText(String.valueOf(currentLikes - 1));
                } else {
                    try {
                        Log.e("DeleteLikeReply", "Failed to create LikeReply: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("DeleteLikeReply", "Failed to create LikeReply: errorBody null or not readable", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<like_reply>> call, Throwable throwable) {
                Log.e("DeleteLikeReply", "API call failed: " + throwable.getMessage(), throwable);
            }
        });
    }

    private void fetchLikeReply(String id, String userId, ListViewHolder holder) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<like_reply>> call = apiInterface.getLikeReply(
                "eq." + userId,"eq." + id, "*"
        );
        call.enqueue(new Callback<List<like_reply>>() {
            @Override
            public void onResponse(Call<List<like_reply>> call, Response<List<like_reply>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<like_reply> likeReplies = response.body();
                    if (likeReplies.isEmpty()) {
                        holder.isLiked = false;
                        holder.btnLike.setImageResource(R.drawable.img_like);
                    } else {
                        holder.isLiked = true;
                        holder.btnLike.setImageResource(R.drawable.like_vektor);
                    }
                } else {
                    Log.e("FetchLikeReply", "Failed to fetch like status: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<like_reply>> call, Throwable throwable) {
                Log.e("FetchLikeReply", "API call failed: " + throwable.getMessage(), throwable);
            }
        });
    }

    @Override
    public int getItemCount() {
        return replylist.size();
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
        public boolean isLiked =false;
        TextView tvname, tvcontent, tvtime, tvliked;
        ImageView tvimg;
        ImageButton btnLike;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvname = itemView.findViewById(R.id.name);
            tvcontent = itemView.findViewById(R.id.message);
            tvtime = itemView.findViewById(R.id.time);
            tvliked = itemView.findViewById(R.id.post_liked);
            tvimg = itemView.findViewById(R.id.user_profile_image1);
            btnLike = itemView.findViewById(R.id.like);
        }
    }
}
