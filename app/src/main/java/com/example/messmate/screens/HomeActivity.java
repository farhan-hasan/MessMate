package com.example.messmate.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.messmate.R;
import com.example.messmate.fragments.MealFragment;
import com.example.messmate.fragments.ProfileFragment;
import com.example.messmate.fragments.RentFragment;
import com.example.messmate.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.homeScreenToolbar);
        setSupportActionBar(toolbar);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logout in progress...");
        progressDialog.setTitle("Logout");
        progressDialog.setCanceledOnTouchOutside(false);

        bottomNavBarController();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.logouttoolbar){
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setMessage("Do you want to Log Out?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
        progressDialog.show();
        // Use a Handler to delay the intent for 2 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Dismiss the DialogPlus dialog

                FirebaseAuth.getInstance().signOut();
                // Start the new activity
                Intent intent=new Intent(HomeActivity.this,LoginActivity.class) ;
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                progressDialog.dismiss();


                finish();
            }
        }, 1500);
    });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();

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