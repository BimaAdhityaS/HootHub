package com.example.hoothub.activity.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoothub.R;
import com.example.hoothub.adapter.ListPostAdapter;
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

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private Button editBtn,signOutBtn;
    private TextView profileName, userName, bio;

    private FloatingActionButton floatingActionButton;

    private RecyclerView rvText;

    private ArrayList<post> list = new ArrayList<>();
    private ListPostAdapter listPostAdapter;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        editBtn = view.findViewById(R.id.edit_profile);
        editBtn.setOnClickListener(this);

        signOutBtn = view.findViewById(R.id.sign_out_profile);
        signOutBtn.setOnClickListener(this);

        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);

        rvText = view.findViewById(R.id.rvText);
        rvText.setHasFixedSize(true);

        profileName = view.findViewById(R.id.name_profile);
        userName = view.findViewById(R.id.username_profile);
        bio = view.findViewById(R.id.bio_profile);

        sp = getActivity().getSharedPreferences("userCred", Context.MODE_PRIVATE);
        getCurrentUser();

        // Initialize the adapter and RecyclerView
        showRecylcerList(view.getContext());

        // Fetch posts from the API
        fetchPosts();

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.edit_profile) {
            // Jika iya, buat intent untuk berpindah ke AddPost Activity
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.sign_out_profile) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("user_id", "");
            editor.putString("username", "");
            editor.apply();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.floatingActionButton) {
            Intent intent = new Intent(getActivity(), AddPost.class);
            startActivity(intent);
        }
    }

    public void getCurrentUser(){
        String user_id = sp.getString("user_id", "");

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

                    if (user_data.getFirstName() == null && user_data.getLastName() == null) {
                        profileName.setText(user_data.getUsername());
                    } else {
                        profileName.setText(user_data.getFirstName() + " " + user_data.getLastName());
                    }

                    userName.setText(user_data.getUsername());

                    if (user_data.getBio() == null) {
                        bio.setText("Fill your bio with hobbies, inquiry, and etc.");
                    } else {
                        bio.setText(user_data.getBio());
                    }

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

    private void showRecylcerList(Context context) {
        rvText.setLayoutManager(new LinearLayoutManager(context));
        listPostAdapter = new ListPostAdapter(context, list);
        rvText.setAdapter(listPostAdapter);
    }

    private void fetchPosts() {
        String user_id = sp.getString("user_id", "");

        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<post>> call = apiInterface.getCurrentUserPost(
                "eq." + user_id,
                "*"
        );

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
}