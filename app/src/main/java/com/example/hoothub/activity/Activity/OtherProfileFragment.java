package com.example.hoothub.activity.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hoothub.R;
import com.example.hoothub.adapter.ListPostAdapter;
import com.example.hoothub.model.post;
import com.example.hoothub.model.user;
import com.example.hoothub.retrofit.ApiInterface;
import com.example.hoothub.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtherProfileFragment extends Fragment {

    private de.hdodenhof.circleimageview.CircleImageView imgProfile;
    private TextView profileName, userName, bio;
    private SharedPreferences sp;
    private RecyclerView rvText;
    private ArrayList<post> list = new ArrayList<>();
    private ListPostAdapter listPostAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other_profile, container, false);

        imgProfile = view.findViewById(R.id.other_user_profile_image1);
        profileName = view.findViewById(R.id.other_name_profile);
        userName = view.findViewById(R.id.other_username_profile);
        bio = view.findViewById(R.id.other_bio_profile);
        rvText = view.findViewById(R.id.rvOtherUserPosts); // Ensure this ID matches your layout file

        sp = getActivity().getSharedPreferences("userCred", Context.MODE_PRIVATE);

        // Get the user ID from the arguments
        String userId = getArguments().getString("user_id");
        fetchUserData(userId);

        // Initialize the RecyclerView
        showRecyclerList(view.getContext());

        return view;
    }

    private void fetchUserData(String userId) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<user>> call = apiInterface.getCurrentUser("eq." + userId, "*");

        call.enqueue(new Callback<List<user>>() {
            @Override
            public void onResponse(Call<List<user>> call, Response<List<user>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    user userData = response.body().get(0); // Get the first user

                    Glide.with(OtherProfileFragment.this)
                            .load(userData.getImg_profile())
                            .placeholder(R.drawable.img_dummyprofilepic) // optional placeholder image
                            .error(R.drawable.dummy_image) // optional error image
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imgProfile);

                    if (userData.getFirstName() == null || userData.getLastName() == null ){
                        profileName.setText(userData.getUsername());
                    }else {
                        profileName.setText(userData.getFirstName() + " " + userData.getLastName());
                    }

                    userName.setText("@" + userData.getUsername());
                    bio.setText(userData.getBio() != null ? userData.getBio() : "No bio available");
                    // Fetch posts for this user
                    fetchPosts(userId);
                } else {
                    Log.e("OtherProfile", "Failed to fetch user data: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<user>> call, Throwable t) {
                Log.e("OtherProfile", "API call failed: " + t.getMessage(), t);
            }
        });
    }

    private void showRecyclerList(Context context) {
        rvText.setLayoutManager(new LinearLayoutManager(context));
        listPostAdapter = new ListPostAdapter(context, list, sp);
        rvText.setAdapter(listPostAdapter);
    }

    private void fetchPosts(String userId) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<post>> call = apiInterface.getCurrentUserPost("eq." + userId, "*", "created_at.desc");

        call.enqueue(new Callback<List<post>>() {
            @Override
            public void onResponse(Call<List<post>> call, Response<List<post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    list.clear();
                    list.addAll(response.body());
                    listPostAdapter.notifyDataSetChanged();
                } else {
                    Log.e("OtherProfile", "Failed to fetch posts: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<post>> call, Throwable t) {
                Log.e("OtherProfile", "API call failed: " + t.getMessage(), t);
            }
        });
    }
}
