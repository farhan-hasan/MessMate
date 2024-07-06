package com.example.messmate.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messmate.R;
import com.example.messmate.models.UserDetailsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResidentListRecyclerAdapter extends RecyclerView.Adapter<ResidentListRecyclerAdapter.ViewHolder> {
    Context context;
    String messKey;
    List<UserDetailsModel> residentList;
    private final ResidentListRecyclerAdapter.OnItemClickListener listener;
    TextView totalRentAmountTextView, collectedAmountTextView;

    public interface OnItemClickListener {
        void onPaidButtonClick(UserDetailsModel item);
        void onRemoveButtonClick(UserDetailsModel item);
    }

    public ResidentListRecyclerAdapter(Context context,
                                       ResidentListRecyclerAdapter.OnItemClickListener listener,
                                       List<UserDetailsModel> residentList,
                                       TextView totalRentAmountTextView,
                                       TextView collectedAmountTextView,
                                       String messKey) {
        this.context = context;
        this.listener = listener;
        this.residentList = residentList;
        this.totalRentAmountTextView = totalRentAmountTextView;
        this.collectedAmountTextView = collectedAmountTextView;
        this.messKey = messKey;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.resident_card_view, parent,false);
        return new ViewHolder(view);
    }

    public int getListSize() {
        return residentList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        UserDetailsModel resident = residentList.get(position);
        final Boolean[] isPaid = {false};


        // Initial paid button state
        {
            DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                    .child("Messes")
                    .child(resident.getMess_name())
                    .child("residents")
                    .child(resident.getKey());
            messesRef.child("rent").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        isPaid[0] = dataSnapshot.getValue(Boolean.class);
                        Log.d("IS_PAID", "paid: " + isPaid[0]);
                        if(isPaid[0]) {
                            holder.paidButton.setText("Paid");
                            holder.paidButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
                        }
                        else {
                            holder.paidButton.setText("Unpaid");
                            holder.paidButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
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


        holder.paidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPaid = false;
                if(holder.paidButton.getText().toString().equals("Paid")) {
                    isPaid = true;
                }

                if(isPaid) {



                    {
                        //LayoutInflater inflater = getLayoutInflater();
                        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setView(dialogView);

                        AlertDialog dialog = builder.create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {

                                TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
                                TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);

                                dialogTitle.setText("Are you sure?");
                                dialogMessage.setText("Are you sure the person did not pay the rent?");

                                // Set button click listeners
                                Button negativeButton = dialogView.findViewById(R.id.custom_negative_button);
                                Button positiveButton = dialogView.findViewById(R.id.custom_positive_button);

                                negativeButton.setText("No");
                                positiveButton.setText("Yes");

                                negativeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Handle negative button clickToast.makeText(HomeActivity.this, "Collection not closed", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });

                                positiveButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Handle positive button click
                                        // Do something and then dismiss the dialog
                                        holder.paidButton.setText("Unpaid");
                                        holder.paidButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                                        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                                .child("Messes")
                                                .child(resident.getMess_name())
                                                .child("residents")
                                                .child(resident.getKey());
                                        messesRef.child("rent").setValue(false).addOnCompleteListener(task -> {
                                            if(task.isSuccessful()) {
                                                setTotalRentAmount(messKey);
                                                dialog.dismiss();
                                                //resetActivity();
                                            }
                                            else {
                                                Toast.makeText(context, "Failed to set to Unpaid", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });

                        dialog.show();
                    }




//                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.residentName.getContext());
//                    builder.setTitle("Are you sure?");
//                    builder.setMessage("Are you sure the person did not pay the rent?");
//
//                    builder.setPositiveButton("Yes", (dialog, which) -> {
//                        holder.paidButton.setText("Unpaid");
//                        holder.paidButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
//                        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
//                                .child("Messes")
//                                .child(resident.getMess_name())
//                                .child("residents")
//                                .child(resident.getKey());
//                        messesRef.child("rent").setValue(false).addOnCompleteListener(task -> {
//                            if(task.isSuccessful()) {
//                                setTotalRentAmount(messKey);
//                                //resetActivity();
//                            }
//                            else {
//                                Toast.makeText(context, "Failed to set to Unpaid", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    });
//
//                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    });
//                    builder.show();
                }
                else {


                    {
                        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setView(dialogView);

                        AlertDialog dialog = builder.create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {

                                TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
                                TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);

                                dialogTitle.setText("Are you sure?");
                                dialogMessage.setText("Are you sure the person paid the rent?");

                                // Set button click listeners
                                Button negativeButton = dialogView.findViewById(R.id.custom_negative_button);
                                Button positiveButton = dialogView.findViewById(R.id.custom_positive_button);

                                negativeButton.setText("No");
                                positiveButton.setText("Yes");

                                negativeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Handle negative button clickToast.makeText(HomeActivity.this, "Collection not closed", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });

                                positiveButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Handle positive button click
                                        // Do something and then dismiss the dialog
                                        holder.paidButton.setText("Paid");
                                        holder.paidButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
                                        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                                .child("Messes")
                                                .child(resident.getMess_name())
                                                .child("residents")
                                                .child(resident.getKey());
                                        messesRef.child("rent").setValue(true).addOnCompleteListener(task -> {
                                            if(task.isSuccessful()) {
                                                setTotalRentAmount(messKey);
                                                dialog.dismiss();
                                                //resetActivity();
                                            }
                                            else {
                                                Toast.makeText(context, "Failed to set to Paid", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });

                        dialog.show();
                    }



//                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.residentName.getContext());
//                    builder.setTitle("Are you sure?");
//                    builder.setMessage("Are you sure the person paid the rent?");
//
//                    builder.setPositiveButton("Yes", (dialog, which) -> {
//                        holder.paidButton.setText("Paid");
//                        holder.paidButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
//                        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
//                                .child("Messes")
//                                .child(resident.getMess_name())
//                                .child("residents")
//                                .child(resident.getKey());
//                        messesRef.child("rent").setValue(true).addOnCompleteListener(task -> {
//                            if(task.isSuccessful()) {
//                                setTotalRentAmount(messKey);
//                                //resetActivity();
//                            }
//                            else {
//                                Toast.makeText(context, "Failed to set to Paid", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    });
//
//                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    });
//                    builder.show();
                }

            }
        });
        holder.residentName.setText(resident.getUsername());
        holder.residentEmail.setText("Email: " + resident.getEmail());
        holder.residentPhone.setText("Phone: " + resident.getPhone());
        holder.itemView.setPadding(0, 0, 0, 0);
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                {
                    //LayoutInflater inflater = getLayoutInflater();
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(dialogView);

                    AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {

                            TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
                            TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);

                            dialogTitle.setText("Are you sure?");
                            dialogMessage.setText("Do you want to remove this resident from the mess?");

                            // Set button click listeners
                            Button negativeButton = dialogView.findViewById(R.id.custom_negative_button);
                            Button positiveButton = dialogView.findViewById(R.id.custom_positive_button);

                            negativeButton.setText("No");
                            positiveButton.setText("Yes");

                            negativeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Handle negative button clickToast.makeText(HomeActivity.this, "Collection not closed", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });

                            positiveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Handle positive button click
                                    // Do something and then dismiss the dialog
                                    final int[] numberOfSeats = {0};
                                    final int[] availableSeats = {0};


                                    FirebaseDatabase.getInstance().getReference().child("Messes")
                                            .child(resident.getMess_name())
                                            .child("residents")
                                            .child(resident.getKey()).removeValue().addOnCompleteListener(task1 -> {
                                                if(task1.isSuccessful()) {
                                                    Toast.makeText(context, "Resident removed successfully", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    updateList(resident.getMess_name());
                                                }
                                                else {
                                                    Toast.makeText(context, "Failed to remove resident", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    // updating user details
                                    {

                                        Map<String, Object> updateData = new HashMap<>();
                                        updateData.put("mess_name", "");
                                        updateData.put("is_resident", false);
                                        updateData.put("breakfast_amount", 0);
                                        updateData.put("lunch_amount", 0);
                                        updateData.put("dinner_amount", 0);
                                        updateData.put("meal_amount", 0);
                                        FirebaseDatabase.getInstance().getReference().child("users")
                                                .child(resident.getKey())
                                                .updateChildren(updateData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(context, "User details updated", Toast.LENGTH_SHORT).show();
                                                            setTotalRentAmount(messKey);
                                                        } else {
                                                            Toast.makeText(context, "Failed to update user details", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                    // Fetching number of seats
                                    {
                                        DatabaseReference messRef = FirebaseDatabase.getInstance().getReference()
                                                .child("Messes")
                                                .child(resident.getMess_name());

                                        messRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                numberOfSeats[0] = snapshot.child("number_of_seats").getValue(Integer.class);
                                                // Incrementing available seats by one
                                                {
                                                    // Fetching available seats
                                                    DatabaseReference messRef = FirebaseDatabase.getInstance().getReference()
                                                            .child("Messes")
                                                            .child(resident.getMess_name());

                                                    messRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            availableSeats[0] = snapshot.child("available_seats").getValue(Integer.class);
                                                            // incrementing available seats
                                                            {
                                                                DatabaseReference messRef = FirebaseDatabase.getInstance().getReference()
                                                                        .child("Messes")
                                                                        .child(resident.getMess_name());
                                                                messRef.child("available_seats").setValue(Math.min(availableSeats[0] + 1, numberOfSeats[0]));
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });

                    dialog.show();
                }


//                AlertDialog.Builder builder = new AlertDialog.Builder(holder.residentName.getContext());
//                builder.setTitle("Are you sure?");
//                builder.setMessage("Do you want to remove this resident from the mess?");
//
//                builder.setPositiveButton("Yes", (dialog, which) -> {
//                    final int[] numberOfSeats = {0};
//                    final int[] availableSeats = {0};
//
//
//                    FirebaseDatabase.getInstance().getReference().child("Messes")
//                            .child(resident.getMess_name())
//                            .child("residents")
//                            .child(resident.getKey()).removeValue().addOnCompleteListener(task1 -> {
//                        if(task1.isSuccessful()) {
//                            Toast.makeText(context, "Resident removed successfully", Toast.LENGTH_SHORT).show();
//                            updateList(resident.getMess_name());
//                        }
//                        else {
//                            Toast.makeText(context, "Failed to remove resident", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                    // updating user details
//                    {
//
//                        Map<String, Object> updateData = new HashMap<>();
//                        updateData.put("mess_name", "");
//                        updateData.put("is_resident", false);
//                        updateData.put("breakfast_amount", 0);
//                        updateData.put("lunch_amount", 0);
//                        updateData.put("dinner_amount", 0);
//                        updateData.put("meal_amount", 0);
//                        FirebaseDatabase.getInstance().getReference().child("users")
//                                .child(resident.getKey())
//                                .updateChildren(updateData).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            Toast.makeText(context, "User details updated", Toast.LENGTH_SHORT).show();
//                                            setTotalRentAmount(messKey);
//                                        } else {
//                                            Toast.makeText(context, "Failed to update user details", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });
//                    }
//
//                    // Fetching number of seats
//                    {
//                        DatabaseReference messRef = FirebaseDatabase.getInstance().getReference()
//                                .child("Messes")
//                                .child(resident.getMess_name());
//
//                        messRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                numberOfSeats[0] = snapshot.child("number_of_seats").getValue(Integer.class);
//                                // Incrementing available seats by one
//                                {
//                                    // Fetching available seats
//                                    DatabaseReference messRef = FirebaseDatabase.getInstance().getReference()
//                                            .child("Messes")
//                                            .child(resident.getMess_name());
//
//                                    messRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                            availableSeats[0] = snapshot.child("available_seats").getValue(Integer.class);
//                                            // incrementing available seats
//                                            {
//                                                DatabaseReference messRef = FirebaseDatabase.getInstance().getReference()
//                                                        .child("Messes")
//                                                        .child(resident.getMess_name());
//                                                messRef.child("available_seats").setValue(Math.min(availableSeats[0] + 1, numberOfSeats[0]));
//                                            }
//
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//                                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//
//
//                });
//
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(context, "Resident not removed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                builder.show();
            }
        });

    }

    public void setTotalRentAmount(String messKey) {
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
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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


    @Override
    public int getItemCount() {
        return residentList.size();
    }
    public void updateList(String messKey) {
        residentList.clear();
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
                    if (!snapshot.getKey().equals("dummy@dummycom")) {
                        residentKeys.add(snapshot.getKey());
                    }
                }
                fetchUserDetails(residentKeys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                            residentList.add(user);
                        }
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView residentName, residentEmail, residentPhone;
        Button paidButton, removeButton;
        public CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            residentName = itemView.findViewById(R.id.residentNameTextView);
            residentEmail = itemView.findViewById(R.id.residentEmailTextView);
            residentPhone = itemView.findViewById(R.id.residentPhoneTextView);
            paidButton = itemView.findViewById(R.id.paidButton);
            removeButton = itemView.findViewById(R.id.removeButton);
            cardView = itemView.findViewById(R.id.resident_card_view);
        }
    }


}
