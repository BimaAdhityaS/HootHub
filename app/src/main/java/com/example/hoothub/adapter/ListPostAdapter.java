package com.example.hoothub.adapter;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.hoothub.model.like_post;
import com.example.hoothub.model.post;
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
        holder.tvimg.setImageResource(R.drawable.dummy_image);

        holder.tvname.setText(currentPost.getUser_name());
        holder.tvcontent.setText(currentPost.getContent());
        holder.tvcomment.setText(currentPost.getComment_count() + " comments");
        holder.tvliked.setText(currentPost.getLike_count() + " likes");
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
    }

    private void checkUserExistsAndLikePost(String postId, String userId, ListViewHolder holder) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<user>> call = apiInterface.getCurrentUser(userId, "*");
        call.enqueue(new Callback<List<user>>() {
            @Override
            public void onResponse(Call<List<user>> call, Response<List<user>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    fetchAddLike(postId, userId, holder);
                } else {
                    Log.e("CheckUserExists", "User does not exist: " + userId);
                }
            }

            @Override
            public void onFailure(Call<List<user>> call, Throwable throwable) {
                Log.e("CheckUserExists", "API call failed: " + throwable.getMessage(), throwable);
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
                    holder.btnLike.setImageResource(R.drawable.img_liked);
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
                        holder.btnLike.setImageResource(R.drawable.img_liked);
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
        ImageButton btnComment, btnLike;
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
