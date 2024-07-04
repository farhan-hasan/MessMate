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
import com.example.messmate.adapters.MealManagementMessListRecyclerAdapter;
import com.example.messmate.adapters.MessListRecyclerAdapter;
import com.example.messmate.adapters.WrapContentLinearLayoutManager;
import com.example.messmate.models.Constants;
import com.example.messmate.models.MessDetailsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class MealManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MealManagementMessListRecyclerAdapter mealManagementMessListRecyclerAdapter;

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
        // Added wrapper for back button issue
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(MealManagementActivity.this, LinearLayoutManager.VERTICAL, false));
        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference().child("Messes");
        Query query = messesRef.orderByChild("admin").equalTo(Constants.userKey);

        FirebaseRecyclerOptions<MessDetailsModel> options =
                new FirebaseRecyclerOptions.Builder<MessDetailsModel>()
                        .setQuery(query, MessDetailsModel.class).build();
        mealManagementMessListRecyclerAdapter = new MealManagementMessListRecyclerAdapter(MealManagementActivity.this, new MealManagementMessListRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(MessDetailsModel item) {
                String key = item.mess_name.toLowerCase() + "_" + item.mess_address.toLowerCase();
                String message = "Key " + key.replace(" ","");
                Intent intent = new Intent(MealManagementActivity.this, MealManagementMenuUpdateActivity.class);
                intent.putExtra("messKey", item.mess_key);
                intent.putExtra("messName", item.mess_name);
                startActivity(intent);
            }
        }, options);
        recyclerView.setAdapter(mealManagementMessListRecyclerAdapter);
    }

    public void onStart() {
        super.onStart();
        if (mealManagementMessListRecyclerAdapter != null) {
            mealManagementMessListRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mealManagementMessListRecyclerAdapter != null) {
            mealManagementMessListRecyclerAdapter.stopListening();
        }
    }
}