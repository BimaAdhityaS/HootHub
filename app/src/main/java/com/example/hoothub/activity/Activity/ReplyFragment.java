package com.example.hoothub.activity.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hoothub.R;
import com.example.hoothub.adapter.ListCommentAdapter;
import com.example.hoothub.adapter.ListReplyAdapter;
import com.example.hoothub.model.comment;
import com.example.hoothub.model.post;
import com.example.hoothub.model.reply;
import com.example.hoothub.retrofit.ApiInterface;
import com.example.hoothub.retrofit.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReplyFragment extends Fragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "comment_id";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private TextView tvTitle, tvUsername, tvUsername_2, tvReplyCount;
    private FloatingActionButton floatingActionButton;
    private ArrayList<reply> list = new ArrayList<>();
    private ListReplyAdapter listReplyAdapter;
    private RecyclerView rvReply;
    private SharedPreferences sp;
    public ReplyFragment() {
        // Required empty public constructor
    }

    public static ReplyFragment newInstance(int param1) {
        ReplyFragment fragment = new ReplyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, String.valueOf(param1));

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            Log.e("Comment Id", mParam1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reply, container, false);
        sp = getActivity().getSharedPreferences("userCred", Context.MODE_PRIVATE);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        tvTitle = view.findViewById(R.id.et_content);
        tvUsername = view.findViewById(R.id.et_username_1);
        tvUsername_2 = view.findViewById(R.id.et_username_2);
        tvReplyCount = view.findViewById(R.id.reply_count);
        rvReply = view.findViewById(R.id.rvReply);
        floatingActionButton.setOnClickListener(this);
        return view;
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showRecylcerList(view.getContext());
        fetchComment();
        fetchReply();
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.floatingActionButton){
            Intent intent = new Intent(getActivity(), AddReplyActivity.class);
            intent.putExtra("comment_id", String.valueOf(mParam1));
            startActivity(intent);
        }
    }
    private void showRecylcerList(Context context) {
        rvReply.setLayoutManager(new LinearLayoutManager(context));
        listReplyAdapter = new ListReplyAdapter(context, list, sp);
        rvReply.setAdapter(listReplyAdapter);
    }
    private void fetchReply() {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<reply>> call = apiInterface.getReply("eq." + mParam1, "*",
                "created_at.desc");
        call.enqueue(new Callback<List<reply>>() {
            @Override
            public void onResponse(Call<List<reply>> call, Response<List<reply>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    list.clear();
                    list.addAll(response.body());
                    listReplyAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to fetch reply", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<reply>> call, Throwable throwable) {
                Log.e("ReplyFragment", "API call failed: " + throwable.getMessage(), throwable);
            }
        });
    }

    private void fetchComment() {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<comment>> call = apiInterface.getCommentById("eq." + mParam1, "*");
        call.enqueue(new Callback<List<comment>>() {
            @Override
            public void onResponse(Call<List<comment>> call, Response<List<comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    comment comment = response.body().get(0);
                    tvTitle.setText(comment.getContent());
                    tvUsername.setText(comment.getUsername());
                    tvUsername_2.setText("@"+comment.getUsername());
                    tvReplyCount.setText(comment.getReply_count());
                } else {
                    Log.e("ReplyFragment", "Failed to fetch comment");
                }
            }

            @Override
            public void onFailure(Call<List<comment>> call, Throwable throwable) {
                Log.e("ReplyFragment", "Error fetching comment: " + throwable.getMessage());
            }
        });
    }


}