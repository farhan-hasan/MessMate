package com.example.messmate.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messmate.R;
import com.example.messmate.models.Constants;
import com.example.messmate.models.MessDetailsModel;
import com.example.messmate.screens.HomeActivity;
import com.example.messmate.screens.LoginActivity;
import com.firebase.ui.database.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.HashMap;
import java.util.Map;

public class MessListRecyclerAdapter extends FirebaseRecyclerAdapter<MessDetailsModel, MessListRecyclerAdapter.ViewHolder> {
    Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MessDetailsModel item);
    }

    public MessListRecyclerAdapter(Context context,
                                   OnItemClickListener listener,
                                   @NonNull FirebaseRecyclerOptions<MessDetailsModel> options) {
        super(options);
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mess_list_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull MessDetailsModel model) {
        holder.messName.setText(model.mess_name);
        holder.messAddress.setText("Address: " + model.mess_address);
        holder.adminPhone.setText("Admin phone: " + model.admin_phone);
        holder.availableSeats.setText("Available seats: " + String.valueOf(model.available_seats));
        holder.itemView.setPadding(0, 0, 0, 0);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(model);
            }
        });

        String messKey = model.mess_key;
        holder.messRemoveButton.setOnClickListener(new View.OnClickListener() {
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
                            dialogMessage.setText("Do you want to remove this mess?");

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
                                    DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
                                            .child("Messes")
                                            .child(messKey);

                                    Log.d("MESS_KEY", messKey);

                                    messesRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(context, "Mess removed successfully", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });
                        }
                    });

                    dialog.show();
                }




//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("Are you sure?");
//                builder.setMessage("Do you want to remove this mess?");
//
//                builder.setPositiveButton("Yes", (dialog, which) -> {
//                    DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference()
//                            .child("Messes")
//                            .child(messKey);
//
//                    Log.d("MESS_KEY", messKey);
//
//                    messesRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            Toast.makeText(context, "Mess removed successfully", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//
//                });
//
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//                builder.show();
            }
        });
        holder.messEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMess(messKey, holder, model);
            }
        });

    }

    public void editMess(String messKey, ViewHolder holder, MessDetailsModel model) {
        final DialogPlus dialogPlus = DialogPlus.newDialog(context)
                .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.edit_mess_popup))
                .setExpanded(true, 1400)
                .create();

        View popupView = dialogPlus.getHolderView();
        EditText messNameEditText = popupView.findViewById(R.id.messNameEditText);
        EditText messAddressEditText = popupView.findViewById(R.id.messAddressEditText);
        EditText numberOfSeatsEditText = popupView.findViewById(R.id.numberOfSeatsEditText);
        EditText rentPerSeatEditText = popupView.findViewById(R.id.rentPerSeatEditText);
        Button poppupEditButton = popupView.findViewById(R.id.messEditButton);


        // Initializing popup edit texts
        {
            DatabaseReference messRef = FirebaseDatabase.getInstance().getReference()
                    .child("Messes")
                    .child(model.mess_key);
            messRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String messName = snapshot.child("mess_name").getValue(String.class);
                    String messAddress = snapshot.child("mess_address").getValue(String.class);
                    int numberOfSeats = snapshot.child("number_of_seats").getValue(Integer.class);
                    int rentPerSeat = snapshot.child("rent_per_seat").getValue(Integer.class);
                    messNameEditText.setText(messName);
                    messAddressEditText.setText(messAddress);
                    numberOfSeatsEditText.setText(String.valueOf(numberOfSeats));
                    rentPerSeatEditText.setText(String.valueOf(rentPerSeat));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        dialogPlus.show();
        poppupEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Map<String, Object> messDetails = new HashMap<>();
                if (messNameEditText.getText() != null && messAddressEditText.getText() != null &&
                        numberOfSeatsEditText.getText() != null && rentPerSeatEditText.getText() != null) {

                    String messName = messNameEditText.getText().toString().trim();
                    String messAddress = messAddressEditText.getText().toString().trim();
                    int numberOfSeats = Integer.parseInt(numberOfSeatsEditText.getText().toString().trim());
                    int rentPerSeat = Integer.parseInt(rentPerSeatEditText.getText().toString().trim());


                    DatabaseReference messRef = FirebaseDatabase.getInstance().getReference()
                            .child("Messes")
                            .child(model.mess_key);

                    messRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int existingNumberOfSeats = snapshot.child("number_of_seats").getValue(Integer.class);
                            // Increasing number of seats
                            if (numberOfSeats >= existingNumberOfSeats) {
                                int diff = numberOfSeats - existingNumberOfSeats;

                                DatabaseReference seatRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Messes")
                                        .child(model.mess_key);
                                seatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int availableSeats = snapshot.child("available_seats").getValue(Integer.class);
                                        messDetails.put("mess_name", messName);
                                        messDetails.put("mess_address", messAddress);
                                        messDetails.put("number_of_seats", numberOfSeats);
                                        messDetails.put("available_seats", availableSeats + diff);
                                        messDetails.put("rent_per_seat", rentPerSeat);

                                        DatabaseReference seatRef = FirebaseDatabase.getInstance().getReference()
                                                .child("Messes")
                                                .child(model.mess_key);
                                        seatRef.updateChildren(messDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(context, "Mess details updated successfully", Toast.LENGTH_SHORT).show();
                                                dialogPlus.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                            // Increasing number of seats
                            else if (numberOfSeats < existingNumberOfSeats) {
                                int diff = existingNumberOfSeats - numberOfSeats;


                                DatabaseReference seatRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Messes")
                                        .child(model.mess_key);
                                seatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int availableSeats = snapshot.child("available_seats").getValue(Integer.class);
                                        // available seats check
                                        if (diff <= availableSeats) {
                                            messDetails.put("mess_name", messName);
                                            messDetails.put("mess_address", messAddress);
                                            messDetails.put("number_of_seats", numberOfSeats);
                                            messDetails.put("available_seats", availableSeats - diff);
                                            messDetails.put("rent_per_seat", rentPerSeat);

                                            DatabaseReference seatRef = FirebaseDatabase.getInstance().getReference()
                                                    .child("Messes")
                                                    .child(model.mess_key);
                                            seatRef.updateChildren(messDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(context, "Mess details updated successfully", Toast.LENGTH_SHORT).show();
                                                    dialogPlus.dismiss();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(context, "Not enough available seats to reduce.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messName, messAddress, adminPhone, availableSeats;
        Button messEditButton, messRemoveButton;

        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            messName = itemView.findViewById(R.id.messNameTextView);
            messAddress = itemView.findViewById(R.id.messAddressTextView);
            adminPhone = itemView.findViewById(R.id.adminPhone);
            availableSeats = itemView.findViewById(R.id.availableSeatsTextView);
            cardView = itemView.findViewById(R.id.mess_list_card_view);
            messEditButton = itemView.findViewById(R.id.messEditButton);
            messRemoveButton = itemView.findViewById(R.id.messRemoveButton);

        }
    }
}
