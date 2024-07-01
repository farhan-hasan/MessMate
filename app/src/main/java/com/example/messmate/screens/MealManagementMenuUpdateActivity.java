package com.example.messmate.screens;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.messmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MealManagementMenuUpdateActivity extends AppCompatActivity {

    String messKey, messName;
    EditText breakfastMenuEditText, breakfastPriceEditText, lunchMenuEditText;
    EditText lunchPriceEditText, dinnerMenuEditText, dinnerPriceEditText;
    Button breakfastSaveButton, lunchSaveButton, dinnerSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_management_menu_update);
        messKey = getIntent().getStringExtra("messKey");
        messName = getIntent().getStringExtra("messName");
        breakfastMenuEditText = findViewById(R.id.breakfastMenuEditText);
        breakfastPriceEditText = findViewById(R.id.breakfastPriceEditText);
        lunchMenuEditText = findViewById(R.id.lunchMenuEditText);
        lunchPriceEditText = findViewById(R.id.lunchPriceEditText);
        dinnerMenuEditText = findViewById(R.id.dinnerMenuEditText);
        dinnerPriceEditText = findViewById(R.id.dinnerPriceEditText);
        breakfastSaveButton = findViewById(R.id.breakfastSaveButton);
        lunchSaveButton = findViewById(R.id.lunchSaveButton);
        dinnerSaveButton = findViewById(R.id.dinnerSaveButton);

        Toolbar toolbar = findViewById(R.id.mealRequestToolbar);
        setSupportActionBar(toolbar);

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        initMenuDetails();

        breakfastSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String menu = breakfastMenuEditText.getText().toString();
                String price = breakfastPriceEditText.getText().toString();

                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                        .child("Messes")
                        .child(messKey);
                messesRef.child("breakfast").child("menu").setValue(menu).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {

                            {
                                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Messes")
                                        .child(messKey);
                                messesRef.child("breakfast").child("price").setValue(Integer.parseInt(price)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(MealManagementMenuUpdateActivity.this, "Breakfast menu updated successfully", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(MealManagementMenuUpdateActivity.this, "Failed to update breakfast menu", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                        }
                        else {
                            Toast.makeText(MealManagementMenuUpdateActivity.this, "Failed to update breakfast menu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        lunchSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String menu = lunchMenuEditText.getText().toString();
                String price = lunchPriceEditText.getText().toString();

                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                        .child("Messes")
                        .child(messKey);
                messesRef.child("lunch").child("menu").setValue(menu).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {

                            {
                                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Messes")
                                        .child(messKey);
                                messesRef.child("lunch").child("price").setValue(Integer.parseInt(price)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(MealManagementMenuUpdateActivity.this, "Lunch menu updated successfully", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(MealManagementMenuUpdateActivity.this, "Failed to update lunch menu", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                        }
                        else {
                            Toast.makeText(MealManagementMenuUpdateActivity.this, "Failed to update lunch menu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        dinnerSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String menu = dinnerMenuEditText.getText().toString();
                String price = dinnerPriceEditText.getText().toString();

                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                        .child("Messes")
                        .child(messKey);
                messesRef.child("dinner").child("menu").setValue(menu).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {

                            {
                                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Messes")
                                        .child(messKey);
                                messesRef.child("dinner").child("price").setValue(Integer.parseInt(price)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(MealManagementMenuUpdateActivity.this, "Dinner menu updated successfully", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(MealManagementMenuUpdateActivity.this, "Failed to update dinner menu", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                        }
                        else {
                            Toast.makeText(MealManagementMenuUpdateActivity.this, "Failed to update dinner menu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


    }

    public void initMenuDetails() {
        //Breakfast
        {
            DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                    .child("Messes")
                    .child(messKey);
            messesRef.child("breakfast").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String menu = "";
                    int price = 0;
                    if(snapshot.exists()) {
                        menu = snapshot.child("menu").getValue(String.class);
                        price = snapshot.child("price").getValue(Integer.class);
                    }
                    breakfastMenuEditText.setText(menu);
                    breakfastPriceEditText.setText(String.valueOf(price));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MealManagementMenuUpdateActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        //Lunch
        {
            DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                    .child("Messes")
                    .child(messKey);
            messesRef.child("lunch").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String menu = "";
                    int price = 0;
                    if(snapshot.exists()) {
                        menu = snapshot.child("menu").getValue(String.class);
                        price = snapshot.child("price").getValue(Integer.class);
                    }
                    lunchMenuEditText.setText(menu);
                    lunchPriceEditText.setText(String.valueOf(price));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MealManagementMenuUpdateActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        //Dinner
        {
            DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                    .child("Messes")
                    .child(messKey);
            messesRef.child("dinner").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String menu = "";
                    int price = 0;
                    if(snapshot.exists()) {
                        menu = snapshot.child("menu").getValue(String.class);
                        price = snapshot.child("price").getValue(Integer.class);
                    }
                    dinnerMenuEditText.setText(menu);
                    dinnerPriceEditText.setText(String.valueOf(price));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MealManagementMenuUpdateActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(MealManagementMenuUpdateActivity.this, MealManagementActivity.class);
            startActivity(intent);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}