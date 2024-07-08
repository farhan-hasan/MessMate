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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MealRequestActivity extends AppCompatActivity {
    String messKey, messName;
    TextView breakfastmenuitemTextView, breakfastprice, lunchmenuitemTextView,lunchprice;
    TextView dinnermenuitemTextView, dinnerprice;
    TextView mealRequestMessName;
    Button buttonforbreakfast, buttonforlunch, buttonfordinner;
    int breakfastPrice, lunchPrice, dinnerPrice;
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
        mealRequestMessName = findViewById(R.id.mealRequestMessName);

        messKey = getIntent().getStringExtra("messKey");
        messName = getIntent().getStringExtra("messName");

        mealRequestMessName.setText(messName);
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
                    buttonforbreakfast.setText("Request");
                    buttonforbreakfast.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MealRequestActivity.this, R.color.primary_color)));
                    messesRef.child("breakfast").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Meal request decrement
                            {
                                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Messes")
                                        .child(messKey)
                                        .child("meal_request")
                                        .child("breakfast");
                                messesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int val = snapshot.getValue(Integer.class);
                                        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                                .child("Messes")
                                                .child(messKey)
                                                .child("meal_request")
                                                .child("breakfast");
                                        messesRef.setValue(val - 1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(MealRequestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            // Meal amount decrement
                            {
                                // Fetch lunch_amount
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(Constants.userKey);

                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int breakfastAmount = 0;
                                        int mealAmount = 0;
                                        if(snapshot.exists()) {
                                            breakfastAmount = snapshot.child("breakfast_amount").getValue(Integer.class);
                                            mealAmount = snapshot.child("meal_amount").getValue(Integer.class);
                                        }
                                        // Decrease lunch amount
                                        {
                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                                    .child(Constants.userKey)
                                                    .child("breakfast_amount")
                                                    ;
                                            userRef.setValue(breakfastAmount - breakfastPrice);
                                        }
                                        // Decrease meal amount
                                        {
                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                                    .child(Constants.userKey)
                                                    .child("meal_amount")
                                                    ;
                                            userRef.setValue(mealAmount - breakfastPrice);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    });
                } else {
                    buttonforbreakfast.setText("Remove");
                    buttonforbreakfast.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MealRequestActivity.this, R.color.red)));
                    messesRef.child("breakfast").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Meal request increment
                            {
                                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Messes")
                                        .child(messKey)
                                        .child("meal_request")
                                        .child("breakfast");
                                messesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int val = snapshot.getValue(Integer.class);
                                        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                                .child("Messes")
                                                .child(messKey)
                                                .child("meal_request")
                                                .child("breakfast");
                                        messesRef.setValue(val + 1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(MealRequestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            // Meal breakfast increment
                            {
                                // Fetch lunch_amount
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(Constants.userKey);

                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int breakfastAmount = 0;
                                        int mealAmount = 0;
                                        if(snapshot.exists()) {
                                            breakfastAmount = snapshot.child("breakfast_amount").getValue(Integer.class);
                                            mealAmount = snapshot.child("meal_amount").getValue(Integer.class);
                                        }
                                        // Increase breakfast amount
                                        {
                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                                    .child(Constants.userKey)
                                                    .child("breakfast_amount")
                                                    ;
                                            userRef.setValue(breakfastAmount + breakfastPrice);
                                        }
                                        // Increase meal amount
                                        {
                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                                    .child(Constants.userKey)
                                                    .child("meal_amount")
                                                    ;
                                            userRef.setValue(mealAmount + breakfastPrice);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    });;
                }


            }
        });

        buttonfordinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonState = buttonfordinner.getText().toString();
                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                        .child("Messes")
                        .child(messKey)
                        .child("residents")
                        .child(Constants.userKey);

                if(buttonState.equals("Remove")) {
                    buttonfordinner.setText("Request");
                    buttonfordinner.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MealRequestActivity.this, R.color.primary_color)));
                    messesRef.child("dinner").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Meal request decrement
                            {
                                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Messes")
                                        .child(messKey)
                                        .child("meal_request")
                                        .child("dinner");
                                messesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int val = snapshot.getValue(Integer.class);
                                        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                                .child("Messes")
                                                .child(messKey)
                                                .child("meal_request")
                                                .child("dinner");
                                        messesRef.setValue(val - 1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(MealRequestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            // Meal amount decrement
                            {
                                // Fetch dinner_amount
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(Constants.userKey);

                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int dinnerAmount = 0;
                                        int mealAmount  = 0;
                                        if(snapshot.exists()) {
                                            dinnerAmount = snapshot.child("dinner_amount").getValue(Integer.class);
                                            mealAmount = snapshot.child("meal_amount").getValue(Integer.class);
                                        }
                                        // Decrease dinner amount
                                        {
                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                                    .child(Constants.userKey)
                                                    .child("dinner_amount")
                                                    ;
                                            userRef.setValue(dinnerAmount - dinnerPrice);
                                        }
                                        // Decrease meal amount
                                        {
                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                                    .child(Constants.userKey)
                                                    .child("meal_amount")
                                                    ;
                                            userRef.setValue(mealAmount - dinnerPrice);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    });;
                } else {
                    buttonfordinner.setText("Remove");
                    buttonfordinner.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MealRequestActivity.this, R.color.red)));
                    messesRef.child("dinner").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Meal request increment
                            {
                                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Messes")
                                        .child(messKey)
                                        .child("meal_request")
                                        .child("dinner");
                                messesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int val = snapshot.getValue(Integer.class);
                                        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                                .child("Messes")
                                                .child(messKey)
                                                .child("meal_request")
                                                .child("dinner");
                                        messesRef.setValue(val + 1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(MealRequestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            // Meal amount increment
                            {
                                // Fetch dinner_amount
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(Constants.userKey);

                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int dinnerAmount = 0;
                                        int mealAmount = 0;
                                        if(snapshot.exists()) {
                                            dinnerAmount = snapshot.child("dinner_amount").getValue(Integer.class);
                                            mealAmount = snapshot.child("meal_amount").getValue(Integer.class);
                                        }
                                        // Increase Dinner amount
                                        {
                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                                    .child(Constants.userKey)
                                                    .child("dinner_amount")
                                                    ;
                                            userRef.setValue(dinnerAmount + dinnerPrice);
                                        }
                                        // Increase meal amount
                                        {
                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                                    .child(Constants.userKey)
                                                    .child("meal_amount")
                                                    ;
                                            userRef.setValue(mealAmount + dinnerPrice);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    });
                }


            }
        });

        buttonforlunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonState = buttonforlunch.getText().toString();
                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                        .child("Messes")
                        .child(messKey)
                        .child("residents")
                        .child(Constants.userKey);

                if(buttonState.equals("Remove")) {
                    buttonforlunch.setText("Request");
                    buttonforlunch.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MealRequestActivity.this, R.color.primary_color)));
                    messesRef.child("lunch").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Meal request decrement
                            {
                                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Messes")
                                        .child(messKey)
                                        .child("meal_request")
                                        .child("lunch");
                                messesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int val = snapshot.getValue(Integer.class);
                                        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                                .child("Messes")
                                                .child(messKey)
                                                .child("meal_request")
                                                .child("lunch");
                                        messesRef.setValue(val - 1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(MealRequestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            // Meal amount decrement
                            {
                                // Fetch lunch_amount
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(Constants.userKey);

                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int lunchAmount = 0;
                                        int mealAmount = 0;
                                        if(snapshot.exists()) {
                                            lunchAmount = snapshot.child("lunch_amount").getValue(Integer.class);
                                            mealAmount = snapshot.child("meal_amount").getValue(Integer.class);
                                        }
                                        // Decrease lunch amount
                                        {
                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                                    .child(Constants.userKey)
                                                    .child("lunch_amount")
                                                    ;
                                            userRef.setValue(lunchAmount - lunchPrice);
                                        }
                                        // Decrease meal amount
                                        {
                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                                    .child(Constants.userKey)
                                                    .child("meal_amount")
                                                    ;
                                            userRef.setValue(mealAmount - lunchPrice);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    });
                } else {
                    buttonforlunch.setText("Remove");
                    buttonforlunch.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MealRequestActivity.this, R.color.red)));
                    messesRef.child("lunch").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Meal request increment
                            {
                                DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Messes")
                                        .child(messKey)
                                        .child("meal_request")
                                        .child("lunch");
                                messesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int val = snapshot.getValue(Integer.class);
                                        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                                .child("Messes")
                                                .child(messKey)
                                                .child("meal_request")
                                                .child("lunch");
                                        messesRef.setValue(val + 1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(MealRequestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            // Meal amount increment
                            {
                                // Fetch lunch_amount
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(Constants.userKey);

                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int lunchAmount = 0;
                                        int mealAmount = 0;
                                        if(snapshot.exists()) {
                                            lunchAmount = snapshot.child("lunch_amount").getValue(Integer.class);
                                            mealAmount = snapshot.child("meal_amount").getValue(Integer.class);
                                        }
                                        // Increase lunch amount
                                        {
                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                                    .child(Constants.userKey)
                                                    .child("lunch_amount")
                                                    ;
                                            userRef.setValue(lunchAmount + lunchPrice);
                                        }
                                        // Decrease meal amount
                                        {
                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                                                    .child(Constants.userKey)
                                                    .child("meal_amount")
                                                    ;
                                            userRef.setValue(mealAmount + lunchPrice);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    });
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
                        breakfastPrice = price;
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
                        lunchPrice = price;
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
                        dinnerPrice = price;
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