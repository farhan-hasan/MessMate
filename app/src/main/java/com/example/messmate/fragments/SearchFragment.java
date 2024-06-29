package com.example.messmate.fragments;

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
import com.example.messmate.adapters.WrapContentLinearLayoutManager;
import com.example.messmate.models.MessDetailsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;


public class SearchFragment extends Fragment {
    private MessListRecyclerAdapter messListRecyclerAdapter;
    View view;
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        loadFragment();

        // Inflate the layout for this fragment
        return view;
    }
    public void loadFragment() {

        RecyclerView recyclerView = view.findViewById(R.id.searchMessListRecyclerView);
        // Added wrapper for back button issue
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        messListCardItems.add(new MessListCardModel("10", "My mess 1", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 2", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 3", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 4", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 5", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 6", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 7", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 8", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 9", "Sylhet", "+8801723232323"));
//        messListCardItems.add(new MessListCardModel("10", "My mess 10", "Sylhet", "+8801723232323"));

        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference().child("Messes");
        Query query = messesRef.orderByChild("available_seats").startAt(1);

        FirebaseRecyclerOptions<MessDetailsModel> options =
                new FirebaseRecyclerOptions.Builder<MessDetailsModel>()
                        .setQuery(query, MessDetailsModel.class).build();

        messListRecyclerAdapter = new MessListRecyclerAdapter(getActivity(), new MessListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MessDetailsModel item) {
                String message = "Clicked " + item.mess_name + " ";
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }, options);
        recyclerView.setAdapter(messListRecyclerAdapter);
    }

    public void onStart() {
        super.onStart();
        if (messListRecyclerAdapter != null) {
            messListRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (messListRecyclerAdapter != null) {
            messListRecyclerAdapter.stopListening();
        }
    }
}