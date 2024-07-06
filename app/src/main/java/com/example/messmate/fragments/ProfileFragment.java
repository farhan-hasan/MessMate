package com.example.messmate.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.messmate.R;
import com.example.messmate.models.Constants;
import com.example.messmate.screens.HomeActivity;
import com.example.messmate.screens.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private View view;
    private EditText nameEditText, emailEditText, phoneEditText, oldPasswordEditText, newPasswordEditText,
            confirmNewPasswordEditText;
    private Button saveButton, updatePassButton;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton fab;
    private PopupWindow popupWindow;
    TextView totat_rent,total_meal;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        fab = view.findViewById(R.id.totalamountfloatbutton); // Assuming your FloatingActionButton id is 'totalamountfloatbutton'

        // Set onClickListener for FloatingActionButton
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });

        // Initialize Firebase components and UI elements
        initializeFirebase();
        initializeUIElements();

        return view;
    }

    private void initializeFirebase() {
        database = FirebaseDatabase.getInstance();
        String userId = Constants.userKey; // Replace with the actual user ID
        userRef = database.getReference("users").child(userId);
    }

    private void initializeUIElements() {
        nameEditText = view.findViewById(R.id.nameEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        emailEditText = view.findViewById(R.id.profileEmailEditText);
        oldPasswordEditText = view.findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        confirmNewPasswordEditText = view.findViewById(R.id.NewconfirmPasswordEditText);
        saveButton = view.findViewById(R.id.profileSaveButton);
        updatePassButton = view.findViewById(R.id.profileUpdatePassButton);

        // Load user data from Firebase
        loadUserData();

        // Set onClickListener for update password button
        updatePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                String confirmNewPassword = confirmNewPasswordEditText.getText().toString();

                if (!oldPassword.isEmpty() && !newPassword.isEmpty() && !confirmNewPassword.isEmpty()) {
                    updatePassword(oldPassword, newPassword, confirmNewPassword);
                } else {
                    Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set onClickListener for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = nameEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();

                if (!username.isEmpty() && !phone.isEmpty()) {
                    updateUserDetails(username, phone);
                } else {
                    Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = "", phone = "", email = "";
                if (snapshot.exists()) {
                    name = snapshot.child("username").getValue(String.class);
                    phone = snapshot.child("phone").getValue(String.class);
                    email = snapshot.child("email").getValue(String.class);
                }
                nameEditText.setText(name);
                phoneEditText.setText(phone);
                emailEditText.setText(email + " *");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ProfileFragment", "loadUserData:onCancelled", error.toException());
            }
        });
    }

    private void updatePassword(String oldPassword, String newPassword, String confirmNewPassword) {
        if (newPassword.length() < 8) {
            newPasswordEditText.setError("Enter 8 Characters");
        } else if (!newPassword.equals(confirmNewPassword)) {
            confirmNewPasswordEditText.setError("Password is not matching");
        } else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                AuthCredential credential = EmailAuthProvider.getCredential(Constants.userEmail, oldPassword);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        changePassword(newPassword);
                    } else {
                        Log.d("ProfileFragment", "Re-authentication failed");
                        Toast.makeText(requireContext(), "Re-authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.d("ProfileFragment", "No authenticated user found");
                Toast.makeText(getActivity(), "No authenticated user found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void changePassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("ProfileFragment", "Password updated successfully");
                    Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("ProfileFragment", "Failed to update password", task.getException());
                    Toast.makeText(getActivity(), "Failed to update password", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.d("ProfileFragment", "No authenticated user found");
            Toast.makeText(getActivity(), "No authenticated user found", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserDetails(String username, String phone) {
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("username", username);
        userUpdates.put("email", Constants.userKey);
        userUpdates.put("phone", phone);

        userRef.updateChildren(userUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ProfileFragment", "User details updated successfully");
                Toast.makeText(requireContext(), "User details updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("ProfileFragment", "Failed to update user details", task.getException());
                Toast.makeText(getActivity(), "Failed to update user details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPopupWindow() {
        // Inflate the popup window layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.float_action_button_pop_up_window, null);
        TextView titleTextview =  dialogView.findViewById(R.id.dialog_title_profile);
        // Initialize a new instance of PopupWindow
         totat_rent = dialogView.findViewById(R.id.dialog_rent_message);
         total_meal = dialogView.findViewById(R.id.dialog_meal_message);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {


            @Override
            public void onShow(DialogInterface dialogInterface) {

                TextView dialogTitle = dialogView.findViewById(R.id.dialog_title_profile);
               // TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);

                dialogTitle.setText("My Expense");

                // Set button click listeners
                Button positiveButton = dialogView.findViewById(R.id.custom_positive_button_profile);

                positiveButton.setText("OK");


                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            dialog.dismiss();

                    }
                });
            }
        });

        final Integer[] meal_amount = new Integer[1];
        final Integer[] rent_amount = new Integer[1];
        final String[] messkey = new String[1];

        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference().child("users").child(Constants.userKey);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    meal_amount[0] = snapshot.child("meal_amount").getValue(Integer.class);
                    messkey[0] = snapshot.child("mess_name").getValue(String.class);
                }
                DatabaseReference rentref = FirebaseDatabase.getInstance().getReference().child("Messes").child(messkey[0]);
                rentref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            rent_amount[0] = snapshot.child("rent_per_seat").getValue(Integer.class);
                        }
                        total_meal.setText("Total Meal Cost: "+meal_amount[0]);
                        totat_rent.setText("Total Rent Cost: "+ rent_amount[0]);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.show();

    }
}
