package com.example.hoothub.activity.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.hoothub.R;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private Button editBtn,signOutBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_fragment, container, false);
        editBtn = view.findViewById(R.id.edit_profile);
        editBtn.setOnClickListener(this);
        signOutBtn = view.findViewById(R.id.sign_out_profile);
        signOutBtn.setOnClickListener(this);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.edit_profile) {
            // Jika iya, buat intent untuk berpindah ke AddPost Activity
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.sign_out_profile) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    }
}