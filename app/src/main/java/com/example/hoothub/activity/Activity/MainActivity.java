package com.example.hoothub.activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.hoothub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.actionbar_layout);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DBEDF3")));
            View customActionBarView = getSupportActionBar().getCustomView();
            customActionBarView.setPadding(32, 32, 32, 32);
        }

        // Load default fragment
        Intent intent = getIntent();
        if (savedInstanceState == null) {
            Log.d("homeFragment1", String.valueOf(intent));
            loadFragment(new PostFragment(), false);
        }
        if (intent != null && intent.hasExtra("post_id")) {
            Log.d("homeFragment2", String.valueOf(intent));
            String post_id = intent.getStringExtra("post_id");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            CommentFragment commentFragment = CommentFragment.newInstance(Integer.parseInt(post_id));
            fragmentTransaction.replace(R.id.frame_layout, commentFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        if (intent != null && intent.hasExtra("comment_id")) {
            String comment_id = intent.getStringExtra("comment_id");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ReplyFragment replyFragment = ReplyFragment.newInstance(Integer.parseInt(comment_id));
            fragmentTransaction.replace(R.id.frame_layout, replyFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    loadFragment(new PostFragment(), false);
                    return true;
                } else if (itemId == R.id.profile) {
                    loadFragment(new ProfileFragment(), false);
                    return true;
                }
                return false;
            }
        });
    }

    public void navigateToProfileFragment(boolean isOwnProfile) {
        if (isOwnProfile) {
            bottomNavigationView.setSelectedItemId(R.id.profile);
        } else {
            loadFragment(new ProfileFragment(), false);
        }
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frame_layout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frame_layout, fragment);
        }
        fragmentTransaction.commit();
    }
}
