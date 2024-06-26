package com.example.messmate.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.messmate.R;
import com.example.messmate.adapters.ResidentListRecyclerAdapter;
import com.example.messmate.models.ResidentDetailsModel;

import java.util.ArrayList;

public class RentMessDetailsActivity extends AppCompatActivity {

    private ResidentListRecyclerAdapter residentListRecyclerAdapter;
    ArrayList<ResidentDetailsModel> residentList = new ArrayList<>();

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
        residentList.add(new ResidentDetailsModel("Person 1", "abc@gmail.com", "+8801723232323"));
        residentList.add(new ResidentDetailsModel("Person 2", "abc@gmail.com", "+8801723232323"));
        residentList.add(new ResidentDetailsModel("Person 3", "abc@gmail.com", "+8801723232323"));
        residentList.add(new ResidentDetailsModel("Person 4", "abc@gmail.com", "+8801723232323"));
        residentList.add(new ResidentDetailsModel("Person 5", "abc@gmail.com", "+8801723232323"));
        residentList.add(new ResidentDetailsModel("Person 6", "abc@gmail.com", "+8801723232323"));
        residentList.add(new ResidentDetailsModel("Person 7", "abc@gmail.com", "+8801723232323"));

        residentListRecyclerAdapter = new ResidentListRecyclerAdapter(RentMessDetailsActivity.this, residentList);
        recyclerView.setAdapter(residentListRecyclerAdapter);
    }
}