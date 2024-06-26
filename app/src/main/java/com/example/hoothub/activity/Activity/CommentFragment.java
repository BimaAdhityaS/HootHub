package com.example.hoothub.activity.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hoothub.R;
import com.example.hoothub.adapter.ListCommentAdapter;
import com.example.hoothub.model.comment;
import com.example.hoothub.model.post;
import com.example.hoothub.model.user;
import com.example.hoothub.retrofit.ApiInterface;
import com.example.hoothub.retrofit.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentFragment extends Fragment implements View.OnClickListener{
    private static final String ARG_POST_ID = "post_id";
    private int postId;

    private TextView tvTitle, tvUsername, tvUsername_2, tvCommentCount, tvLikeCount;
    private RecyclerView rvComment;
    private FloatingActionButton floatingActionButton;
    private ArrayList<comment> list = new ArrayList<>();
    private ListCommentAdapter listCommentAdapter;
    private ImageView tvImageUser;
    SharedPreferences sp;
    public CommentFragment() {
        // Required empty public constructor
    }

    public static CommentFragment newInstance(int postId) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POST_ID, postId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getInt(ARG_POST_ID);
            Log.e("tag2", String.valueOf(postId));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        sp = getActivity().getSharedPreferences("userCred", Context.MODE_PRIVATE);
        tvTitle = view.findViewById(R.id.et_Title);
        tvImageUser = view.findViewById(R.id.profile_image);
        tvUsername = view.findViewById(R.id.et_username);
        tvUsername_2 = view.findViewById(R.id.et_username_2);
        rvComment = view.findViewById(R.id.rvComment);  // Inisialisasi RecyclerView
        tvCommentCount = view.findViewById(R.id.comments_count);
        tvLikeCount = view.findViewById(R.id.comment_likes_count);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showRecylcerList(view.getContext());
        fetchPost();
        fetchComment();
    }

    private void showRecylcerList(Context context) {
        rvComment.setLayoutManager(new LinearLayoutManager(context));
        listCommentAdapter = new ListCommentAdapter(context, list, sp);
        rvComment.setAdapter(listCommentAdapter);
    }

    private void fetchComment() {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<comment>> call = apiInterface.getComment(
                "eq." + postId,
                "*",
                "created_at.desc"
        );
        call.enqueue(new Callback<List<comment>>() {
            @Override
            public void onResponse(Call<List<comment>> call, Response<List<comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    list.clear();
                    list.addAll(response.body());
                    listCommentAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to fetch comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<comment>> call, Throwable t) {
                Toast.makeText(getContext(), "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPost() {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<post>> call = apiInterface.getContent("eq." + postId, "*");
        call.enqueue(new Callback<List<post>>() {
            @Override
            public void onResponse(Call<List<post>> call, Response<List<post>> response) {
                Log.d("Response", String.valueOf(response.body()));
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {

                    post post = response.body().get(0);
                    tvTitle.setText(post.getContent());
                    tvUsername.setText(post.getUser_name());
                    tvUsername_2.setText("@"+post.getUser_name());
                    tvCommentCount.setText(post.getComment_count());
                    tvLikeCount.setText(post.getLike_count());
                    getCurrentUser(post.getUser_id());
                } else {
                    Log.e("CommentFragment", "Failed to fetch post");
                }
            }

            @Override
            public void onFailure(Call<List<post>> call, Throwable t) {
                Log.e("CommentFragment", "Error fetching post: " + t.getMessage());
            }
        });
    }

    private void getCurrentUser(String user_id) {
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
                    Glide.with(getContext())
                            .load(user_data.getImg_profile())
                            .placeholder(R.drawable.img_dummyprofilepic) // optional placeholder image
                            .error(R.drawable.dummy_image) // optional error image
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(tvImageUser);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.floatingActionButton) {
            Intent intent = new Intent(getActivity(), AddCommentActivity.class);
            intent.putExtra("post_id", String.valueOf(postId));
            startActivity(intent);
        }
    }
}
