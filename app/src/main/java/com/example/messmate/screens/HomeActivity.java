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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messmate.R;
import com.example.messmate.fragments.MealFragment;
import com.example.messmate.fragments.ProfileFragment;
import com.example.messmate.fragments.RentFragment;
import com.example.messmate.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
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
        FirebaseApp.initializeApp(this);

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


        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
                TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);

                dialogTitle.setText("Are you sure?");
                dialogMessage.setText("Do you want to Logout?");

                // Set button click listeners
                Button negativeButton = dialogView.findViewById(R.id.custom_negative_button);
                Button positiveButton = dialogView.findViewById(R.id.custom_positive_button);

                negativeButton.setText("No");
                positiveButton.setText("Yes");

                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        progressDialog.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent=new Intent(HomeActivity.this,LoginActivity.class) ;
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            }
                        }, 1500);
                    }
                });
            }
        });
        dialog.show();

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