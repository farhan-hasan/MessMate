package com.example.messmate.screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.messmate.R;
import com.example.messmate.adapters.ResidentListRecyclerAdapter;
import com.example.messmate.adapters.WrapContentLinearLayoutManager;
import com.example.messmate.models.UserDetailsModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orhanobut.dialogplus.DialogPlus;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RentMessDetailsActivity extends AppCompatActivity {
    List<UserDetailsModel> residentList = new ArrayList<>();
    ResidentListRecyclerAdapter residentListRecyclerAdapter;
    Button  closeButton;
    FloatingActionButton residentAddButton;
    String messKey, messName;
    TextView messNameTextView, totalRentAmountTextView, collectedAmountTextView;
    CircleImageView messImage;
    RecyclerView recyclerView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_mess_details);
        messKey = getIntent().getStringExtra("messKey");
        messName = getIntent().getStringExtra("messName");

        Toolbar toolbar = findViewById(R.id.rentMessDetailsToolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.rentResidentListRecyclerView);

        messNameTextView = findViewById(R.id.rentDetailsMessName);
        messNameTextView.setText(messName);
        totalRentAmountTextView = findViewById(R.id.totalRentAmountTextView);
        collectedAmountTextView = findViewById(R.id.collectedAmountTextView);
        messImage = findViewById(R.id.rentMessImage);
        closeButton = findViewById(R.id.closeButton);
        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTotalRentAmount();
        loadFragment();
        loadProfileImage();

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

        messImage.setOnClickListener(v -> showImageSourceOptions());

    }

    private void showImageSourceOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option")
                .setItems(new CharSequence[]{"Gallery", "Camera"}, (dialog, which) -> {
                    switch (which) {
                        case 0: // Gallery
                            openFileChooser();
                            break;
                        case 1: // Camera
                            openCamera();
                            break;
                    }
                });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImage();
        } else if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageUri = getImageUriFromBitmap(photo);
            uploadImage();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
    private void uploadImage() {
        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("mess_images").child(messKey + ".jpg");

            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        loadProfileImage();
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Image uploaded failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
        }
    }
    private void loadProfileImage() {


        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("mess_images/" + messKey + ".jpg");

        // Fetch the download URL
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Use Glide to load the image into the ImageView
            Glide.with(this)
                    .load(uri)
                    .into(messImage);

        }).addOnFailureListener(exception -> {
            // Handle any errors
        });
    }

    public void resetActivity() {
        Activity activity = (Activity) RentMessDetailsActivity.this;
        Intent intent = activity.getIntent();
        activity.finish();
        activity.startActivity(intent);
    }

    public void closeCollection() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(RentMessDetailsActivity.this);
        builder.setView(dialogView);
//        builder.setTitle("Are you sure?");
//        builder.setMessage("Do you want to close collection for this month?");
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
                TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);

                dialogTitle.setText("Are you sure?");
                dialogMessage.setText("Do you want to close collection for this month?");

                // Set button click listeners
                Button negativeButton = dialogView.findViewById(R.id.custom_negative_button);
                Button positiveButton = dialogView.findViewById(R.id.custom_positive_button);

                negativeButton.setText("No");
                positiveButton.setText("Yes");

                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle negative button click
                        Toast.makeText(RentMessDetailsActivity.this, "Collection not closed", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle positive button click
                        // Do something and then dismiss the dialog
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
                                setTotalRentAmount();
                                residentListRecyclerAdapter.updateList(messKey);
                                //resetActivity();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(RentMessDetailsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
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
                                                        recyclerView.setAdapter(residentListRecyclerAdapter);
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

       // RecyclerView recyclerView = findViewById(R.id.rentResidentListRecyclerView);

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

