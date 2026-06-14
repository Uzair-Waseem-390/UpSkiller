package com.example.upskiller.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.example.upskiller.R;
import com.example.upskiller.ui.base.BaseActivity;
import com.example.upskiller.ui.home.HomeFragment;
import com.example.upskiller.ui.profile.ProfileFragment;
import com.example.upskiller.ui.roadmap.CreateRoadmapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected;
            int id = item.getItemId();
            if (id == R.id.nav_home)    selected = new HomeFragment();
            else if (id == R.id.nav_create)   selected = new CreateRoadmapFragment();
            else                              selected = new ProfileFragment();
            loadFragment(selected);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
