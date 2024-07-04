package com.example.messmate.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    Button residentAddButton, closeButton;
    String messKey, messName;
    TextView messNameTextView, totalRentAmountTextView, collectedAmountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_mess_details);
        messKey = getIntent().getStringExtra("messKey");
        messName = getIntent().getStringExtra("messName");

        Toolbar toolbar = findViewById(R.id.rentMessDetailsToolbar);
        setSupportActionBar(toolbar);

        messNameTextView = findViewById(R.id.rentDetailsMessName);
        messNameTextView.setText(messName);
        totalRentAmountTextView = findViewById(R.id.totalRentAmountTextView);
        collectedAmountTextView = findViewById(R.id.collectedAmountTextView);
        closeButton = findViewById(R.id.closeButton);
        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTotalRentAmount();
        loadFragment();

        residentAddButton = findViewById(R.id.residentAddButton);
        residentAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addResident();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeCollection();
            }
        });

    }

    public void resetActivity() {
        Activity activity = (Activity) RentMessDetailsActivity.this;
        Intent intent = activity.getIntent();
        activity.finish();
        activity.startActivity(intent);
    }

    public void closeCollection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RentMessDetailsActivity.this);
        builder.setTitle("Are you sure?");
        builder.setMessage("Do you want to close collection for this month?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                    .child("Messes")
                    .child(messKey)
                    .child("residents");
            messesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int paidCounter = 0;
                    System.out.println(dataSnapshot.hasChildren());
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().child("rent").setValue(false);
                    }
                    resetActivity();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(RentMessDetailsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(RentMessDetailsActivity.this, "Collection not closed", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
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
                final int[] availableSeats = {0};



                Map<String, Object> data = new HashMap<>();

                data.put("breakfast", false);
                data.put("lunch", false);
                data.put("dinner", false);
                data.put("rent", false);

                if (residentEmailEditText.getText() != null) {
                    String email = residentEmailEditText.getText().toString();
                    String key = email.replace(".", "");

                    // Check available seats
                    DatabaseReference messRef = FirebaseDatabase.getInstance().getReference()
                            .child("Messes")
                            .child(messKey);

                    messRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            availableSeats[0] = snapshot.child("available_seats").getValue(Integer.class);
                            if (availableSeats[0] <= 0) {
                                Toast.makeText(RentMessDetailsActivity.this, "There is no available seats in this mess", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // Checking if user already exist in the mess
                            for (UserDetailsModel user : residentList) {
                                if (user.getKey().equals(key)) {
                                    Toast.makeText(RentMessDetailsActivity.this, "Resident already in this mess", Toast.LENGTH_SHORT).show();
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
                                        if (dbUser != null) {
                                            boolean isResident = dataSnapshot.child("is_resident").getValue(Boolean.class);
                                            if (isResident == true) {
                                                Toast.makeText(RentMessDetailsActivity.this, "Resident already in a mess", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            // Adding the user
                                            {
                                                // Add if user exists in user table
                                                DatabaseReference messRef = FirebaseDatabase.getInstance().getReference()
                                                        .child("Messes")
                                                        .child(messKey)
                                                        .child("residents")
                                                        .child(key);
                                                messRef.setValue(data).addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(RentMessDetailsActivity.this, "Resident added", Toast.LENGTH_SHORT).show();
                                                        residentListRecyclerAdapter.updateList(messKey);
                                                        setTotalRentAmount();
                                                        dialogPlus.dismiss();
                                                    } else {
                                                        Toast.makeText(RentMessDetailsActivity.this, "Failed to add resident", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                FirebaseDatabase.getInstance().getReference().child("users")
                                                        .child(key)
                                                        .child("mess_name").setValue(messKey).addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                //Setting isResident to true
                                                                {
                                                                    FirebaseDatabase.getInstance().getReference().child("users")
                                                                            .child(key)
                                                                            .child("is_resident").setValue(true).addOnCompleteListener(task1 -> {
                                                                                if (task1.isSuccessful()) {
                                                                                    Toast.makeText(RentMessDetailsActivity.this, "User details updated", Toast.LENGTH_SHORT).show();
                                                                                } else {
                                                                                    Toast.makeText(RentMessDetailsActivity.this, "Failed to update user details", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                }
                                                            } else {
                                                                Toast.makeText(RentMessDetailsActivity.this, "Failed to update user details", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                // reducing available seats by one
                                                {
                                                    messRef = FirebaseDatabase.getInstance().getReference()
                                                            .child("Messes")
                                                            .child(messKey);
                                                    messRef.child("available_seats").setValue(availableSeats[0] - 1);
                                                }

                                            }
                                        }
                                    } else {
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

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(RentMessDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
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


        residentListRecyclerAdapter = new ResidentListRecyclerAdapter(RentMessDetailsActivity.this, new ResidentListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onPaidButtonClick(UserDetailsModel item) {

            }

            @Override
            public void onRemoveButtonClick(UserDetailsModel item) {

            }
        }, residentList, totalRentAmountTextView, collectedAmountTextView, messKey);
        recyclerView.setAdapter(residentListRecyclerAdapter);
        setTotalRentAmount();
        residentListRecyclerAdapter.updateList(messKey);
        //fetchResidentKeys(messKey);

    }

    public void setTotalRentAmount() {

        final int[] totalResident = new int[1];
        final Integer[] rentPerSeat = {0};

        // Total residents query
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
                totalResident[0] = residentKeys.size();
                Log.d("TOTAL_RESIDENTS", "TOTAL_RESIDENTS" + totalResident[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RentMessDetailsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Initial Total Rent amount query
        messesRef = FirebaseDatabase.getInstance().getReference().child("Messes").child(messKey);
        messesRef.child("rent_per_seat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    rentPerSeat[0] = dataSnapshot.getValue(Integer.class);
                    Log.d("FETCH_RENT", "Rent per seat: " + rentPerSeat[0]);
                    String amount = String.valueOf(rentPerSeat[0] * totalResident[0]);
                    totalRentAmountTextView.setText(amount + " BDT");

                    // Initial Paid Amount state query
                    {

                        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                .child("Messes")
                                .child(messKey)
                                .child("residents");
                        System.out.println(messName);
                        messesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int paidCounter = 0;
                                System.out.println(dataSnapshot.hasChildren());
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Boolean rent = snapshot.child("rent").getValue(Boolean.class);
                                    if (rent != null && rent) {
                                        paidCounter++;
                                    }
                                }
                                collectedAmountTextView.setText(String.valueOf((paidCounter * rentPerSeat[0])) + " BDT");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(RentMessDetailsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } else {
                    Log.d("FETCH_RENT", "Rent per seat does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FETCH_RENT", "DatabaseError: " + databaseError.getMessage());
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
                            //residentListRecyclerAdapter.updateList(user);
                            residentListRecyclerAdapter.updateList(messKey);
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

