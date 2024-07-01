package com.example.messmate.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messmate.R;
import com.example.messmate.models.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MealRequestActivity extends AppCompatActivity {
    String messKey;
    TextView breakfastmenuitemTextView, breakfastprice, lunchmenuitemTextView,lunchprice;
    TextView dinnermenuitemTextView, dinnerprice;
    Button buttonforbreakfast, buttonforlunch, buttonfordinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_request);
        breakfastmenuitemTextView = findViewById(R.id.breakfastmessitemTextView);
        breakfastprice = findViewById(R.id.breakfastprice);
        lunchmenuitemTextView = findViewById(R.id.lunchmessitemTextView);
        lunchprice = findViewById(R.id.lunchprice);
        dinnermenuitemTextView = findViewById(R.id.dinnermessitemTextView);
        dinnerprice = findViewById(R.id.dinnerprice);
        buttonforbreakfast = findViewById(R.id.buttonforbreakfast);
        buttonforlunch = findViewById(R.id.buttonforlunch);
        buttonfordinner = findViewById(R.id.buttonfordinner);

        messKey = getIntent().getStringExtra("messKey");

        Toolbar toolbar = findViewById(R.id.mealRequestToolbar);
        setSupportActionBar(toolbar);

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        initMenuDetails();

        // Initial paid button state
        initRequestButton();

        buttonforbreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonState = buttonforbreakfast.getText().toString();
                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                        .child("Messes")
                        .child(messKey)
                        .child("residents")
                        .child(Constants.userKey);

                if(buttonState.equals("Remove")) {
                    messesRef.child("breakfast").setValue(false);
                    buttonforbreakfast.setText("Request");
                    buttonforbreakfast.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MealRequestActivity.this, R.color.primary_color)));
                } else {
                    messesRef.child("breakfast").setValue(true);
                    buttonforbreakfast.setText("Remove");
                    buttonforbreakfast.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MealRequestActivity.this, R.color.red)));
                }


            }
        });

        buttonfordinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonState = buttonforbreakfast.getText().toString();
                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                        .child("Messes")
                        .child(messKey)
                        .child("residents")
                        .child(Constants.userKey);

                if(buttonState.equals("Remove")) {
                    messesRef.child("dinner").setValue(false);
                    buttonfordinner.setText("Request");
                    buttonfordinner.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MealRequestActivity.this, R.color.primary_color)));
                } else {
                    messesRef.child("dinner").setValue(true);
                    buttonfordinner.setText("Remove");
                    buttonfordinner.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MealRequestActivity.this, R.color.red)));
                }


            }
        });

        buttonforlunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonState = buttonforbreakfast.getText().toString();
                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                        .child("Messes")
                        .child(messKey)
                        .child("residents")
                        .child(Constants.userKey);

                if(buttonState.equals("Remove")) {
                    messesRef.child("lunch").setValue(false);
                    buttonforlunch.setText("Request");
                    buttonforlunch.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MealRequestActivity.this, R.color.primary_color)));
                } else {
                    messesRef.child("lunch").setValue(true);
                    buttonforlunch.setText("Remove");
                    buttonforlunch.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MealRequestActivity.this, R.color.red)));
                }


            }
        });

    }

    public void initRequestButton() {
        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                .child("Messes")
                .child(messKey)
                .child("residents")
                .child(Constants.userKey);
        messesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String,Boolean> mealRequestData = new HashMap<>();
                    mealRequestData = (Map<String,Boolean>)dataSnapshot.getValue();
                    boolean breakfast = mealRequestData.get("breakfast");
                    boolean lunch = mealRequestData.get("lunch");
                    boolean dinner = mealRequestData.get("dinner");
                    Log.d("FETCH_BREAKFAST", String.valueOf(breakfast));
                    Log.d("FETCH_LUNCH", String.valueOf(lunch));
                    Log.d("FETCH_DINNER", String.valueOf(dinner));

                    if(breakfast) {
                        buttonforbreakfast.setText("Remove");
                        buttonforbreakfast.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MealRequestActivity.this, R.color.red)));
                    }
                    if(lunch) {
                        buttonforlunch.setText("Remove");
                        buttonforlunch.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MealRequestActivity.this, R.color.red)));
                    }
                    if(dinner) {
                        buttonfordinner.setText("Remove");
                        buttonfordinner.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MealRequestActivity.this, R.color.red)));
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
                    breakfastmenuitemTextView.setText("Menu: " + menu);
                    breakfastprice.setText(String.valueOf("Price: " + price));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MealRequestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                    lunchmenuitemTextView.setText("Menu: " + menu);
                    lunchprice.setText(String.valueOf("Price: " + price));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MealRequestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                    dinnermenuitemTextView.setText("Menu: " + menu);
                    dinnerprice.setText(String.valueOf("Price: " + price));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MealRequestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(MealRequestActivity.this, HomeActivity.class);
            startActivity(intent);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}