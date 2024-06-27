package com.example.messmate.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.messmate.R;
import com.example.messmate.models.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ProfileFragment extends Fragment {

    View view;
    EditText nameEditText, emailEditText, phoneEditText, oldPasswordEditText, newPasswordEditText,
    confirmNewPasswordEditText;
    Button saveButton;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private FirebaseAuth firebaseAuthAuth;
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
        userRef = database.getReference("Users").child(userId);

        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        oldPasswordEditText = view.findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        confirmNewPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        saveButton = view.findViewById(R.id.profileSaveButton);

        saveButton.setOnClickListener(view -> {
            String username = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String oldPassword = oldPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String confirmNewPassword = confirmNewPasswordEditText.getText().toString();

            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(phone) &&
                    !TextUtils.isEmpty(oldPassword) && !TextUtils.isEmpty(newPassword) && !TextUtils.isEmpty(confirmNewPassword)
            ) {
                updateUserDetails(username, email, phone);
                updatePassword(email, oldPassword, newPassword, confirmNewPassword);
            }
            else if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(phone)) {
                updateUserDetails(username, email, phone);
            }
            else {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }

    private void updatePassword(String email, String oldPassword, String newPassword, String confirmNewPassword) {

        if (newPassword.length()<8) {
            newPasswordEditText.setError("Enter 8 Characters");
        } else if (!newPassword.equals(confirmNewPassword)) {
            confirmNewPasswordEditText.setError("Password is not matching");
        } else {
            FirebaseUser user = firebaseAuthAuth.getCurrentUser();
            if (user != null) {
                AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        changePassword(newPassword);
                    } else {
                        Toast.makeText(getActivity(), "Re-authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "No authenticated user found.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void changePassword(String newPassword) {
        FirebaseUser user = firebaseAuthAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Password updated successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to update password: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "No authenticated user found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserDetails(String username, String email, String phone) {

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("username", username);
        userUpdates.put("email", email);
        userUpdates.put("phone", phone);

        userRef.updateChildren(userUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "User details updated successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Failed to update user details: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}