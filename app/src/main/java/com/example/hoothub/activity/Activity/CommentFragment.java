package com.example.hoothub.activity.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hoothub.R;
import com.example.hoothub.model.post;
import com.example.hoothub.retrofit.ApiInterface;
import com.example.hoothub.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentFragment extends Fragment {
    private static final String ARG_POST_ID = "post_id";
    private int postId;

    private TextView tvTitle;

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
        tvTitle = view.findViewById(R.id.et_Title);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchPost();
    }

    private void fetchPost() {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<post>> call = apiInterface.getContent("eq." + postId, "*");
        call.enqueue(new Callback<List<post>>() {
            @Override
            public void onResponse(Call<List<post>> call, Response<List<post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    post post = response.body().get(0);
                    // Pastikan post.getUser_name() adalah metode yang benar pada objek post
                    tvTitle.setText(post.getUser_name());
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
}
