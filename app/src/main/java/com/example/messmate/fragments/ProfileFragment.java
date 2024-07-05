package com.example.messmate.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.messmate.R;
import com.example.messmate.models.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.*;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ProfileFragment extends Fragment {

    View view;
    EditText nameEditText, emailEditText, phoneEditText, oldPasswordEditText, newPasswordEditText,
    confirmNewPasswordEditText;
    Button saveButton, updatePassButton;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private FirebaseAuth firebaseAuth;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        database = FirebaseDatabase.getInstance();
        String userId = Constants.userKey; // Replace with the actual user ID
        userRef = database.getReference("users").child(userId);

        nameEditText = view.findViewById(R.id.nameEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        emailEditText = view.findViewById(R.id.profileEmailEditText);
        oldPasswordEditText = view.findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        confirmNewPasswordEditText = view.findViewById(R.id.NewconfirmPasswordEditText);
        saveButton = view.findViewById(R.id.profileSaveButton);
        updatePassButton = view.findViewById(R.id.profileUpdatePassButton);

        {
            userRef = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(Constants.userKey);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = "", phone = "", email = "";
                    if(snapshot.exists()) {
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

                }
            });
        }


        updatePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = "";
                String newPassword = "";
                String confirmNewPassword = "";
                oldPassword = oldPasswordEditText.getText().toString();

                newPassword = newPasswordEditText.getText().toString();

                confirmNewPassword = confirmNewPasswordEditText.getText().toString();
                if (!oldPassword.isEmpty() && !newPassword.isEmpty() && !confirmNewPassword.isEmpty()) {
                    Log.d("log", "first if");
                    updatePassword(oldPassword, newPassword, confirmNewPassword);
                }
                else {
                    Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }

            }
        });


        saveButton.setOnClickListener(view -> {
            String username = "";
            String phone = "";


            username = nameEditText.getText().toString().trim();

            phone = phoneEditText.getText().toString().trim();


            if (!username.isEmpty() && !phone.isEmpty()) {
                Log.d("log", "second if");
                updateUserDetails(username, phone);
            }
            else {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }

    private void updatePassword( String oldPassword, String newPassword, String confirmNewPassword) {

        if (newPassword.length()<8) {
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
                        Log.d("REAUTHENTICATE", "Re-authentication failed: ");
                        Toast.makeText(requireContext(), "Re-authentication failed: ", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.d("REAUTHENTICATE", "No authenticated user found.");
                Toast.makeText(getActivity(), "No authenticated user found.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void changePassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("REAUTHENTICATE", "Password updated successfully.");
                    Toast.makeText(getActivity(), "Password updated successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("REAUTHENTICATE", "Failed to update password: ");
                    Toast.makeText(getActivity(), "Failed to update password: ", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.d("REAUTHENTICATE", "No authenticated user found.");
            Toast.makeText(getActivity(), "No authenticated user found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserDetails(String username, String phone) {

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("username", username);
        userUpdates.put("email", Constants.userKey);
        userUpdates.put("phone", phone);

        userRef.updateChildren(userUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("REAUTHENTICATE", "User details updated successfully.");
                Toast.makeText(requireContext(), "User details updated successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("REAUTHENTICATE", "Failed to update user details: ");
                Toast.makeText(getActivity(), "Failed to update user details: ", Toast.LENGTH_SHORT).show();
            }
        });


    }
}