package com.example.messmate.fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messmate.R;
import com.example.messmate.adapters.MessListRecyclerAdapter;
import com.example.messmate.adapters.SearchMessListRecyclerAdapter;
import com.example.messmate.adapters.SearchSpinnerAdapter;
import com.example.messmate.adapters.WrapContentLinearLayoutManager;
import com.example.messmate.models.MessDetailsModel;
import com.example.messmate.models.SearchMessDetailsModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SearchFragment extends Fragment {
    private SearchMessListRecyclerAdapter searchMessListRecyclerAdapter;
    View view;
    Spinner searchSpinner;
    EditText searchEditText;
    SearchView searchView;
    FrameLayout searchViewContainer;
    String selectedItem = "Name";
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        searchSpinner = view.findViewById(R.id.spinner);
        searchView = view.findViewById(R.id.searchView);
        searchViewContainer = view.findViewById(R.id.searchViewContainer);
        //searchEditText = view.findViewById(R.id.searchEditText);

        // Create a list of items
        ArrayList<String> items = new ArrayList<>();
        items.add("Name");
        items.add("Address");
        ArrayAdapter<String>  adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_items_layout, items);

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);

        searchSpinner.setAdapter(adapter);

        searchViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
            }
        });
        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedItem = parentView.getItemAtPosition(position).toString();
                if(selectedItem.equals("Name")) {
                    selectedItem = "name_key";
                } else if(selectedItem.equals("Address")) {
                    selectedItem = "address_key";
                }
                Log.d("SPINNER", selectedItem);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });



        customizeSearchView(searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query, selectedItem);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                search(query, selectedItem);
                return false;
            }
        });
        //init available_messes in database
        {
            // Initialize Firebase Database
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

            // Read data from Firebase
            mDatabase.child("Messes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Object> result = new HashMap<>();

                    for (DataSnapshot messSnapshot : dataSnapshot.getChildren()) {
                        String messKey = messSnapshot.getKey();
                        String messName = (String) messSnapshot.child("mess_name").getValue();
                        String messAddress = (String) messSnapshot.child("mess_address").getValue();
                        String adminPhone = (String) messSnapshot.child("admin_phone").getValue();
                        Long availableSeats = messSnapshot.child("available_seats").getValue(Long.class);

                        if (availableSeats != null && availableSeats > 0) {
                            Map<String, Object> messDetails = new HashMap<>();
                            messDetails.put("mess_name", messName);
                            messDetails.put("mess_address", messAddress);
                            messDetails.put("admin_phone", adminPhone);
                            messDetails.put("available_seats", availableSeats);
                            messDetails.put("name_key", messName.toLowerCase());
                            messDetails.put("address_key", messAddress.toLowerCase());
                            messDetails.put("mess_key", messKey);

                            result.put(messKey, messDetails);
                        }
                    }

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                    ref.child("available_messes").setValue(result);

                    // Log or use the result map
                    Log.d("TAG", "Filtered Messes: " + result);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                }
            });
        }
        loadFragment();



        // Inflate the layout for this fragment
        return view;
    }

    public void search(String s, String category) {
        RecyclerView recyclerView = view.findViewById(R.id.searchMessListRecyclerView);

        FirebaseRecyclerOptions<SearchMessDetailsModel> options =
                new FirebaseRecyclerOptions.Builder<SearchMessDetailsModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("available_messes")
                                .orderByChild(category)
                                .startAt(s.toLowerCase())
                                .endAt(s.toLowerCase()+"~"), SearchMessDetailsModel.class).build();

        searchMessListRecyclerAdapter = new SearchMessListRecyclerAdapter(getContext(), options);
        searchMessListRecyclerAdapter.startListening();
        recyclerView.setAdapter(searchMessListRecyclerAdapter);


    }
    public void loadFragment() {

        RecyclerView recyclerView = view.findViewById(R.id.searchMessListRecyclerView);
        // Added wrapper for back button issue
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        DatabaseReference messesRef = FirebaseDatabase.getInstance().getReference().child("Messes");
        Query query = messesRef.orderByChild("available_seats").startAt(1);

        FirebaseRecyclerOptions<SearchMessDetailsModel> options =
                new FirebaseRecyclerOptions.Builder<SearchMessDetailsModel>()
                        .setQuery(query, SearchMessDetailsModel.class).build();

        searchMessListRecyclerAdapter = new SearchMessListRecyclerAdapter(getActivity(), options);
        recyclerView.setAdapter(searchMessListRecyclerAdapter);
    }

    private void customizeSearchView(SearchView searchView) {
        try {
            // Access the SearchView's internal mSearchSrcTextView
            Field searchTextField = SearchView.class.getDeclaredField("mSearchSrcTextView");
            searchTextField.setAccessible(true);
            TextView searchText = (TextView) searchTextField.get(searchView);
            // Change the text color and hint color
            searchText.setTextColor(getResources().getColor(R.color.primary_color));
            searchText.setHintTextColor(getResources().getColor(R.color.primary_color));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void onStart() {
        super.onStart();
        if (searchMessListRecyclerAdapter != null) {
            searchMessListRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (searchMessListRecyclerAdapter != null) {
            searchMessListRecyclerAdapter.stopListening();
        }
    }
}