package com.example.messmate.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.messmate.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MealFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MealFragment extends Fragment {

    public MealFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meal, container, false);



    }
}