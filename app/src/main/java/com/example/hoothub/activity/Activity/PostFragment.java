package com.example.hoothub.activity.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.hoothub.R;
import com.example.hoothub.adapter.ListPostAdapter;
import com.example.hoothub.model.post;
import com.example.hoothub.retrofit.ApiInterface;
import com.example.hoothub.retrofit.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostFragment extends Fragment implements View.OnClickListener {

    private RecyclerView rvText;
    private ArrayList<post> list = new ArrayList<>();
    private ListPostAdapter listPostAdapter;
    private FloatingActionButton floatingActionButton;
    private SharedPreferences sp;
    private SearchView searchView;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        rvText = view.findViewById(R.id.rvText);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        rvText.setHasFixedSize(true);
        sp = getActivity().getSharedPreferences("userCred", Context.MODE_PRIVATE);
        floatingActionButton.setOnClickListener(this);

        showRecyclerList(view.getContext());

        fetchPosts("");

        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchPosts(newText);
                    }
                }, 300);
                return true;
            }
        });

        handler = new Handler();

        return view;
    }

    private void showRecyclerList(Context context) {
        rvText.setLayoutManager(new LinearLayoutManager(context));
        listPostAdapter = new ListPostAdapter(context, list, sp);
        rvText.setAdapter(listPostAdapter);
    }

    private void fetchPosts(String username) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<post>> call;
        if (username.isEmpty()) {
            call = apiInterface.getPosts("created_at.desc");
        } else {
            call = apiInterface.searchPosts("ilike.*" + username, "created_at.desc");
        }

        call.enqueue(new Callback<List<post>>() {
            @Override
            public void onResponse(Call<List<post>> call, Response<List<post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    list.clear();
                    list.addAll(response.body());
                    listPostAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<post>> call, Throwable t) {
                Toast.makeText(getContext(), "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.floatingActionButton) {
            Intent intent = new Intent(getActivity(), AddPostActivity.class);
            startActivity(intent);
        }
    }
}
