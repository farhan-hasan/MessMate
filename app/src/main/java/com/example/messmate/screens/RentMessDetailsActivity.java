package com.example.messmate.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.messmate.R;
import com.example.messmate.adapters.ResidentListRecyclerAdapter;
import com.example.messmate.adapters.WrapContentLinearLayoutManager;
import com.example.messmate.models.UserDetailsModel;
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

public class RentMessDetailsActivity extends AppCompatActivity {
    List<UserDetailsModel> residentList = new ArrayList<>();
    ResidentListRecyclerAdapter residentListRecyclerAdapter;
    Button residentAddButton;
    String messKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_mess_details);

        Toolbar toolbar = findViewById(R.id.rentMessDetailsToolbar);
        setSupportActionBar(toolbar);
         messKey = getIntent().getStringExtra("messKey");

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loadFragment();

        residentAddButton = findViewById(R.id.residentAddButton);
        residentAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addResident();
            }
        });

    }

    public void addResident() {
        final DialogPlus dialogPlus = DialogPlus.newDialog(RentMessDetailsActivity.this)
                .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.add_resident_popup))
                .setExpanded(true, 700)
                .create();

        View popupView = dialogPlus.getHolderView();
        EditText residentEmailEditText = popupView.findViewById(R.id.addResidentEmailEditText);
        Button poppupAddButton = popupView.findViewById(R.id.residentAddButton);

        dialogPlus.show();

        poppupAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> data = new HashMap<>();

                data.put("breakfast", false);
                data.put("lunch", false);
                data.put("dinner", false);
                data.put("rent", false);

                if (residentEmailEditText.getText()!=null) {
                    String email = residentEmailEditText.getText().toString();
                    String key = email.replace(".","");


                    // Checking if user already exist in the mess
                    for(UserDetailsModel user: residentList) {
                        if(user.getKey().equals(key)) {
                            Toast.makeText(RentMessDetailsActivity.this, "Resident already in the mess", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }


                    // Checking if user exists in user table or not
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                    usersRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // User exists
                                UserDetailsModel dbUser = dataSnapshot.getValue(UserDetailsModel.class);
                                if(dbUser!=null) {
                                    {

                                        // Add if user exists in user table
                                        DatabaseReference messRef = FirebaseDatabase.getInstance().getReference()
                                                .child("Messes")
                                                .child(messKey)
                                                .child("residents")
                                                .child(key);
                                        messRef.setValue(data).addOnCompleteListener(task -> {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(RentMessDetailsActivity.this, "Resident added", Toast.LENGTH_SHORT).show();
                                                residentList.clear();
                                                fetchResidentKeys(messKey);
                                                dialogPlus.dismiss();
                                            }
                                            else {
                                                Toast.makeText(RentMessDetailsActivity.this, "Failed to add resident", Toast.LENGTH_SHORT).show();
                                            }
                                        });



                                    }
                                }
                            }
                            else {
                                Toast.makeText(RentMessDetailsActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle possible errors
                            Toast.makeText(RentMessDetailsActivity.this, "DatabaseError: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });



                }
                else {
                    Toast.makeText(RentMessDetailsActivity.this, "Please enter resident email", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(RentMessDetailsActivity.this, HomeActivity.class);
            startActivity(intent);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadFragment() {

        RecyclerView recyclerView = findViewById(R.id.rentResidentListRecyclerView);

        // Added wrapper for back button issue
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(RentMessDetailsActivity.this, LinearLayoutManager.VERTICAL, false));

        String messKey = getIntent().getStringExtra("messKey");
        assert messKey != null;

        //residentList.add(new UserDetailsModel("dummy", "dummy", "dummy","dummy","dummy",false));


        residentListRecyclerAdapter = new ResidentListRecyclerAdapter(RentMessDetailsActivity.this, new ResidentListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onPaidButtonClick(UserDetailsModel item) {

            }

            @Override
            public void onRemoveButtonClick(UserDetailsModel item) {

            }
        }, residentList);
        recyclerView.setAdapter(residentListRecyclerAdapter);

        fetchResidentKeys(messKey);

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
                    residentKeys.add(snapshot.getKey());
                }
                Log.d("log", "residentKeys:");
                for (String key : residentKeys) {
                    System.out.println(key);
                }

                fetchUserDetails(residentKeys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RentMessDetailsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                            residentListRecyclerAdapter.updateList(user);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(RentMessDetailsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}

