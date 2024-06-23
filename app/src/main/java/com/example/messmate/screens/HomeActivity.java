package com.example.messmate.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.messmate.R;
import com.example.messmate.fragments.MealFragment;
import com.example.messmate.fragments.ProfileFragment;
import com.example.messmate.fragments.RentFragment;
import com.example.messmate.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.homeScreenToolbar);
        setSupportActionBar(toolbar);

        bottomNavBarController();

    }

    public void bottomNavBarController() {
        bottomNavigationView = findViewById(R.id.homeBottomNavBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.nav_rent) {
                    loadFrag(new RentFragment());
                } else if (id==R.id.nav_meal) {
                    loadFrag(new MealFragment());
                } else if (id==R.id.nav_search) {
                    loadFrag(new SearchFragment());
                } else if (id==R.id.nav_profile) {
                    loadFrag(new ProfileFragment());
                }
                return true;
            }
            public void loadFrag(Fragment fragment) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.homeFragmentContainer, fragment);
                ft.commit();
            }

        });
        bottomNavigationView.setSelectedItemId(R.id.nav_rent);
    }

}