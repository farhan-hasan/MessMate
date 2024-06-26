package com.example.messmate.fragments;

import android.content.Intent;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.messmate.R;
import com.example.messmate.adapters.MessListRecyclerAdapter;
import com.example.messmate.models.MessListCardModel;
import com.example.messmate.screens.HomeActivity;
import com.example.messmate.screens.RentMessDetailsActivity;

import java.util.ArrayList;

public class RentFragment extends Fragment {
    private RecyclerView recyclerView;
    private MessListRecyclerAdapter messListRecyclerAdapter;
    ArrayList<MessListCardModel> messListCardItems = new ArrayList<>();
    View view;
    public RentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_rent, container, false);

        loadFragment();

        // Inflate the layout for this fragment
        return view;
    }

    public void loadFragment() {

        recyclerView = view.findViewById(R.id.rentMessListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        messListCardItems.add(new MessListCardModel("10", "My mess 1", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 2", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 3", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 4", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 5", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 6", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 7", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 8", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 9", "Sylhet", "+8801723232323"));
        messListCardItems.add(new MessListCardModel("10", "My mess 10", "Sylhet", "+8801723232323"));

        messListRecyclerAdapter = new MessListRecyclerAdapter(getActivity(), messListCardItems, new MessListRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(MessListCardModel item) {
                String message = "Clicked " + item.messName + " ";
                startActivity(new Intent(getActivity(), RentMessDetailsActivity.class));
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(messListRecyclerAdapter);
    }
}