package com.example.messmate.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.messmate.R;
import com.example.messmate.models.Constants;
import com.example.messmate.screens.MealManagementActivity;
import com.example.messmate.screens.MealManagementMenuUpdateActivity;
import com.example.messmate.screens.MealRequestActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MealFragment extends Fragment {

    CardView mealRequestCardView, mealManagementCardView;

    public MealFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meal, container, false);

        mealRequestCardView = view.findViewById(R.id.mealRequestCard);
        mealRequestCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] messKey = new String[1];
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(Constants.userKey);

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            boolean isResident = snapshot.child("is_resident").getValue(Boolean.class);
                            if(isResident == false) {
                                Toast.makeText(requireContext(), "You are not a resident of a mess", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            messKey[0] = snapshot.child("mess_name").getValue(String.class);
                            Intent intent = new Intent(getActivity(), MealRequestActivity.class);
                            intent.putExtra("messKey", messKey[0]);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        mealManagementCardView = view.findViewById(R.id.mealManagementCard);
        mealManagementCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event
                startActivity(new Intent(getActivity(), MealManagementActivity.class));
            }
        });


        return view;


    }
}