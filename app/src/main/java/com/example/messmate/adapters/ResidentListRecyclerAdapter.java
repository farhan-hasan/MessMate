package com.example.messmate.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.messmate.models.MessDetailsModel;
import com.example.messmate.models.UserDetailsModel;
import com.example.messmate.screens.RegisterActivity;
import com.example.messmate.screens.RentMessDetailsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
    List<UserDetailsModel> residentList;
    private final ResidentListRecyclerAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onPaidButtonClick(UserDetailsModel item);
        void onRemoveButtonClick(UserDetailsModel item);
    }

    public ResidentListRecyclerAdapter(Context context,
                                       ResidentListRecyclerAdapter.OnItemClickListener listener,
                                       List<UserDetailsModel> residentList) {
        this.context = context;
        this.listener = listener;
        this.residentList = residentList;
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
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.residentName.getContext());
                    builder.setTitle("Are you sure?");
                    builder.setMessage("Are you sure the person did not pay the rent?");

                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        holder.paidButton.setText("Unpaid");
                        holder.paidButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                .child("Messes")
                                .child(resident.getMess_name())
                                .child("residents")
                                .child(resident.getKey());
                        messesRef.child("rent").setValue(false).addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                Toast.makeText(context, "Set to Unpaid", Toast.LENGTH_SHORT).show();
                                resetActivity();
                            }
                            else {
                                Toast.makeText(context, "Failed to set to Unpaid", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }
                else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.residentName.getContext());
                    builder.setTitle("Are you sure?");
                    builder.setMessage("Are you sure the person paid the rent?");

                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        holder.paidButton.setText("Paid");
                        holder.paidButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
                        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                .child("Messes")
                                .child(resident.getMess_name())
                                .child("residents")
                                .child(resident.getKey());
                        messesRef.child("rent").setValue(true).addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                Toast.makeText(context, "Set to Paid", Toast.LENGTH_SHORT).show();
                                resetActivity();
                            }
                            else {
                                Toast.makeText(context, "Failed to set to Paid", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.residentName.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Do you want to remove this resident from the mess?");

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    Log.d("mess_name", resident.getMess_name());
                    Log.d("key", resident.getKey());
                    FirebaseDatabase.getInstance().getReference().child("Messes")
                            .child(resident.getMess_name())
                            .child("residents")
                            .child(resident.getKey()).removeValue().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()) {
                            Toast.makeText(context, "Resident removed successfully", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            Toast.makeText(context, "Failed to remove resident", Toast.LENGTH_SHORT).show();
                        }
                    });
                    FirebaseDatabase.getInstance().getReference().child("users")
                            .child(resident.getKey())
                            .child("mess_name").setValue("").addOnCompleteListener(task -> {
                                if(task.isSuccessful()) {
                                    // Setting isResident to false
                                    {
                                        FirebaseDatabase.getInstance().getReference().child("users")
                                                .child(resident.getKey())
                                                .child("is_resident").setValue(false).addOnCompleteListener(task1 -> {
                                                    if(task1.isSuccessful()) {
                                                        Toast.makeText(context, "User details updated", Toast.LENGTH_SHORT).show();
                                                        if (context instanceof Activity) {
                                                            resetActivity();
                                                        }
                                                    }
                                                    else {
                                                        Toast.makeText(context, "Failed to update user details", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }

                                    if (context instanceof Activity) {
                                        resetActivity();
                                    }
                                }
                                else {
                                    Toast.makeText(context, "Failed to update user details", Toast.LENGTH_SHORT).show();
                                }
                            });


                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Resident not removed", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

    }

    public void resetActivity() {
        Activity activity = (Activity) context;
        Intent intent = activity.getIntent();
        activity.finish();
        activity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return residentList.size();
    }
    public void updateList(UserDetailsModel newUser) {
        residentList.add(newUser);
        notifyDataSetChanged();
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
