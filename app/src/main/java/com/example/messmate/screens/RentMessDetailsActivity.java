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
import android.widget.Toast;

import com.example.messmate.R;
import com.example.messmate.adapters.ResidentListRecyclerAdapter;
import com.example.messmate.models.Constants;
import com.example.messmate.models.MessDetailsModel;
import com.example.messmate.models.UserDetailsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RentMessDetailsActivity extends AppCompatActivity {
    List<UserDetailsModel> residentList = new ArrayList<>();
    ResidentListRecyclerAdapter residentListRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_mess_details);

        Toolbar toolbar = findViewById(R.id.rentMessDetailsToolbar);
        setSupportActionBar(toolbar);

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loadFragment();

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
        recyclerView.setLayoutManager(new LinearLayoutManager(RentMessDetailsActivity.this));

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

