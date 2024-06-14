package com.example.hoothub.activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.hoothub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity  {
    private Button btn_logout;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        btn_logout = findViewById(R.id.btnlogout);
//        btn_logout.setOnClickListener(this);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DBEDF3")));
        View customActionBarView = getSupportActionBar().getCustomView();
        customActionBarView.setPadding(32,32,32,32);
        loadFragment(new PostFragment(), false);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.home){
                            loadFragment(new PostFragment(), false);
                            return true;
                        } else if (itemId == R.id.profile) {
//                            loadFragment(new ProfileFrgament(), false);
                            return true;
                        }
                        return false;
                    }
                });
    }

//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.btnlogout){
//            SharedPreferences sp = getApplicationContext().getSharedPreferences("userCred", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putString("user_id", "");
//            editor.commit();
//        }
//    }
    private void loadFragment(Fragment fragment, boolean isAppInitalized){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(isAppInitalized == true) {
            fragmentTransaction.add(R.id.frame_layout, fragment);
        }else {
            fragmentTransaction.replace(R.id.frame_layout, fragment);
        }
        fragmentTransaction.commit();
    }
}