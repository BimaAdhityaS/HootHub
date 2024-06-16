package com.example.hoothub.activity.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hoothub.R;
import com.example.hoothub.adapter.ListPostAdapter;
import com.example.hoothub.lib.PostData;
import com.example.hoothub.lib.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PostFragment extends Fragment implements View.OnClickListener{


    private RecyclerView rvText;
    private ArrayList<Post> list = new ArrayList<>();
    private FloatingActionButton floatingActionButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        rvText = view.findViewById(R.id.rvText);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        rvText.setHasFixedSize(true);


        list.addAll(PostData.getListData());
        showRecylcerList(view.getContext());
        // Inflate the layout for this fragment
        floatingActionButton.setOnClickListener(this);
        return view;
    }

    private void showRecylcerList(Context context) {
        rvText.setLayoutManager(new LinearLayoutManager(context));
        ListPostAdapter listPostAdapter = new ListPostAdapter(context, list);
        rvText.setAdapter(listPostAdapter);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.floatingActionButton){
            Intent intent = new Intent(getActivity(), AddPost.class);
            startActivity(intent);
        }
    }
}