package com.example.messmate.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messmate.R;
import com.example.messmate.adapters.MessListRecyclerAdapter;
import com.example.messmate.models.Constants;
import com.example.messmate.models.MessDetailsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class MealManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessListRecyclerAdapter messListRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_management);
        Toolbar toolbar = findViewById(R.id.mealRequestToolbar);
        setSupportActionBar(toolbar);

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loadFragment();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(MealManagementActivity.this, HomeActivity.class);
            startActivity(intent);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadFragment() {

        recyclerView = findViewById(R.id.mealManagementMessListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MealManagementActivity.this));
//        messListCardItems.add(new MessListCardModel("10", "My mess 1", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 2", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 3", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 4", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 5", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 6", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 7", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 8", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 9", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 10", "Sylhet", "+8801723232323"));
        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference().child("Messes");
        Query query = messesRef.orderByChild("admin").equalTo(Constants.userKey);

        FirebaseRecyclerOptions<MessDetailsModel> options =
                new FirebaseRecyclerOptions.Builder<MessDetailsModel>()
                        .setQuery(query, MessDetailsModel.class).build();
        messListRecyclerAdapter = new MessListRecyclerAdapter(MealManagementActivity.this, new MessListRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(MessDetailsModel item) {
                String message = "Clicked " + item.mess_name + " ";
                startActivity(new Intent(MealManagementActivity.this, RentMessDetailsActivity.class));
                Toast.makeText(MealManagementActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }, options);
        recyclerView.setAdapter(messListRecyclerAdapter);
    }

    public void onStart() {
        super.onStart();
        if (messListRecyclerAdapter != null) {
            messListRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (messListRecyclerAdapter != null) {
            messListRecyclerAdapter.stopListening();
        }
    }
}