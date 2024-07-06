package com.example.messmate.screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messmate.R;
import com.example.messmate.adapters.MealResidentListRecyclerAdapter;
import com.example.messmate.adapters.ResidentListRecyclerAdapter;
import com.example.messmate.adapters.WrapContentLinearLayoutManager;
import com.example.messmate.models.UserDetailsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealManagementResidentListActivity extends AppCompatActivity {
    List<UserDetailsModel> residentList = new ArrayList<>();
    MealResidentListRecyclerAdapter residentListRecyclerAdapter;
    Button  mealCloseButton, menuUpdateButton;
    String messKey, messName;
    TextView breakfastAmountTextView, lunchAmountTextView, dinnerAmountTextView;
    TextView mealManagementMessDetailsMessName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_management_resident_list);
        messKey = getIntent().getStringExtra("messKey");
        messName = getIntent().getStringExtra("messName");

        Toolbar toolbar = findViewById(R.id.rentMessDetailsToolbar);
        setSupportActionBar(toolbar);

        mealManagementMessDetailsMessName = findViewById(R.id.mealManagementMessDetailsMessName);
        mealManagementMessDetailsMessName.setText(messName);
        breakfastAmountTextView = findViewById(R.id.breakfastAmountTextView);
        lunchAmountTextView = findViewById(R.id.lunchAmountTextView);
        dinnerAmountTextView = findViewById(R.id.dinnerAmountTextView);
        mealCloseButton = findViewById(R.id.mealCloseButton);
        mealCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MealManagementResidentListActivity.this);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
                        TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);

                        dialogTitle.setText("Are you sure?");
                        dialogMessage.setText("Do you want to close meal requests for today?");

                        // Set button click listeners
                        Button negativeButton = dialogView.findViewById(R.id.custom_negative_button);
                        Button positiveButton = dialogView.findViewById(R.id.custom_positive_button);

                        negativeButton.setText("No");
                        positiveButton.setText("Yes");

                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle negative button click
                                Toast.makeText(MealManagementResidentListActivity.this, "Meal requests not closed", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });

                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle positive button click
                                // Do something and then dismiss the dialog
                                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Messes")
                                        .child(messKey)
                                        .child("meal_request");
                                Map<String, Object> resetData = new HashMap<>();
                                resetData.put("breakfast", 0);
                                resetData.put("lunch", 0);
                                resetData.put("dinner", 0);
                                messesRef.updateChildren(resetData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        breakfastAmountTextView.setText("0");
                                        lunchAmountTextView.setText("0");
                                        dinnerAmountTextView.setText("0");

                                        // set false to all meals for the residents
                                        {
                                            DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                                    .child("Messes")
                                                    .child(messKey)
                                                    .child("residents");
                                            messesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    List<String> residentKeys = new ArrayList<>();
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        if(!snapshot.getKey().equals("dummy@dummycom")) {
                                                            residentKeys.add(snapshot.getKey());
                                                        }
                                                    }

                                                    {
                                                        Map<String, Object> resetData = new HashMap<>();
                                                        resetData.put("breakfast", false);
                                                        resetData.put("lunch", false);
                                                        resetData.put("dinner", false);
                                                        for(String key: residentKeys) {
                                                            DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                                                    .child("Messes")
                                                                    .child(messKey)
                                                                    .child("residents")
                                                                    .child(key);
                                                            messesRef.updateChildren(resetData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Toast.makeText(MealManagementResidentListActivity.this, "Meals closed successfully", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                    }
                });

//                builder.setPositiveButton("Yes", (dialogRef, which) -> {
//                    DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
//                            .child("Messes")
//                            .child(messKey)
//                            .child("meal_request");
//                    Map<String, Object> resetData = new HashMap<>();
//                    resetData.put("breakfast", 0);
//                    resetData.put("lunch", 0);
//                    resetData.put("dinner", 0);
//                    messesRef.updateChildren(resetData).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            breakfastAmountTextView.setText("0");
//                            lunchAmountTextView.setText("0");
//                            dinnerAmountTextView.setText("0");
//
//                            // set false to all meals for the residents
//                            {
//                                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
//                                        .child("Messes")
//                                        .child(messKey)
//                                        .child("residents");
//                                messesRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        List<String> residentKeys = new ArrayList<>();
//                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                            if(!snapshot.getKey().equals("dummy@dummycom")) {
//                                                residentKeys.add(snapshot.getKey());
//                                            }
//                                        }
//
//                                        {
//                                            Map<String, Object> resetData = new HashMap<>();
//                                            resetData.put("breakfast", false);
//                                            resetData.put("lunch", false);
//                                            resetData.put("dinner", false);
//                                            for(String key: residentKeys) {
//                                                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
//                                                        .child("Messes")
//                                                        .child(messKey)
//                                                        .child("residents")
//                                                        .child(key);
//                                                messesRef.updateChildren(resetData).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                        Toast.makeText(MealManagementResidentListActivity.this, "Meals closed successfully", Toast.LENGTH_SHORT).show();
//                                                    }
//                                                });
//                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//                            }
//
//                        }
//                    });
//                });
//
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
                dialog.show();
                //builder.show();
            }
        });
        menuUpdateButton = findViewById(R.id.menuUpdateButton);
        menuUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MealManagementResidentListActivity.this, MealManagementMenuUpdateActivity.class);
                intent.putExtra("messKey", messKey);
                intent.putExtra("messName", messName);
                startActivity(intent);
            }
        });
        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        fetchResidentKeys(messKey);
        loadFragment();
        initMealRequest();

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(MealManagementResidentListActivity.this, MealManagementActivity.class);
            startActivity(intent);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadFragment() {

        RecyclerView recyclerView = findViewById(R.id.mealManagementMessDetailsRecyclerView);

        // Added wrapper for back button issue
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(MealManagementResidentListActivity.this, LinearLayoutManager.VERTICAL, false));

        String messKey = getIntent().getStringExtra("messKey");
        assert messKey != null;


        residentListRecyclerAdapter = new MealResidentListRecyclerAdapter(MealManagementResidentListActivity.this, residentList);
        recyclerView.setAdapter(residentListRecyclerAdapter);

    }
    public void initMealRequest() {
        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                .child("Messes")
                .child(messKey)
                .child("meal_request");
        messesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int breakfast = snapshot.child("breakfast").getValue(Integer.class);
                int lunch = snapshot.child("lunch").getValue(Integer.class);
                int dinner = snapshot.child("dinner").getValue(Integer.class);

                breakfastAmountTextView.setText(String.valueOf(breakfast));
                lunchAmountTextView.setText(String.valueOf(lunch));
                dinnerAmountTextView.setText(String.valueOf(dinner));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MealManagementResidentListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void fetchResidentKeys(String messKey) {
        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                .child("Messes")
                .child(messKey)
                .child("residents");

        messesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> residentKeys = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.getKey().equals("dummy@dummycom")) {
                        residentKeys.add(snapshot.getKey());
                    }
                }
                fetchUserDetails(residentKeys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MealManagementResidentListActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchUserDetails(List<String> residentKeys) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        for (String key : residentKeys) {
            usersRef.orderByChild("key").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserDetailsModel user = snapshot.getValue(UserDetailsModel.class);
                        if (user != null) {
                            residentList.add(user);
                        }
                    }
                    loadFragment();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MealManagementResidentListActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}

