package com.example.messmate.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.messmate.R;
import com.example.messmate.screens.HomeActivity;
import com.example.messmate.screens.LoginActivity;
import com.example.messmate.screens.MealRequestActivity;
import com.example.messmate.screens.RegisterActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MealFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MealFragment extends Fragment {

    CardView cardView;

    public MealFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meal, container, false);

        cardView = view.findViewById(R.id.mealRequestCard);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event
                startActivity(new Intent(getActivity(), MealRequestActivity.class));
            }
        });


        return view;


    }
}