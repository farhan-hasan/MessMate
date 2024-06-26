package com.example.messmate.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messmate.R;
import com.example.messmate.adapters.MessListRecyclerAdapter;
import com.example.messmate.models.MessListCardModel;

import java.util.ArrayList;

public class MealManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessListRecyclerAdapter messListRecyclerAdapter;
    ArrayList<MessListCardModel> messListCardItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_management);

        loadFragment();


    }

    public void loadFragment() {

        recyclerView = findViewById(R.id.mealManagementMessListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MealManagementActivity.this));
        messListCardItems.add(new MessListCardModel("10", "My mess 1", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 2", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 3", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 4", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 5", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 6", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 7", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 8", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 9", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 10", "Sylhet", "+8801723232323"));

        messListRecyclerAdapter = new MessListRecyclerAdapter(MealManagementActivity.this, messListCardItems, new MessListRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(MessListCardModel item) {
                String message = "Clicked " + item.messName + " ";
                startActivity(new Intent(MealManagementActivity.this, MealManagementMenuUpdateActivity.class));
                Toast.makeText(MealManagementActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(messListRecyclerAdapter);
    }
}