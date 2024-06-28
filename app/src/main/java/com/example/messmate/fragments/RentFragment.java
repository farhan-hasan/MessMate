package com.example.messmate.fragments;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.messmate.R;
import com.example.messmate.adapters.MessListRecyclerAdapter;
import com.example.messmate.models.Constants;
import com.example.messmate.models.MessListCardModel;
import com.example.messmate.screens.HomeActivity;
import com.example.messmate.screens.RentMessDetailsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RentFragment extends Fragment {
    private RecyclerView recyclerView;
    private MessListRecyclerAdapter messListRecyclerAdapter;
    private Button addButton;
    ArrayList<MessListCardModel> messListCardItems = new ArrayList<>();
    View view;
    public RentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_rent, container, false);

        addButton = view.findViewById(R.id.addMessButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(requireContext())
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.add_mess_popup))
                        .setExpanded(true, 1400)
                        .create();

                //dialogPlus.show();

                View popupView = dialogPlus.getHolderView();
                EditText messNameEditText = popupView.findViewById(R.id.messNameEditText);
                EditText messAddressEditText = popupView.findViewById(R.id.messAddressEditText);
                EditText numberOfSeatsEditText = popupView.findViewById(R.id.numberOfSeatsEditText);
                EditText rentPerSeatEditText = popupView.findViewById(R.id.rentPerSeatEditText);
                Button poppupAddButton = popupView.findViewById(R.id.messAddButton);



                dialogPlus.show();
                poppupAddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Map<String,Object> messDetails = new HashMap<>();
                        Map<String,Object> breakFastDetails = new HashMap<>();
                        Map<String,Object> lunchDetails = new HashMap<>();
                        Map<String,Object> dinnerDetails = new HashMap<>();
                        Map<String,Object> mealRequest = new HashMap<>();
                        if (messNameEditText.getText()!=null && messAddressEditText.getText()!=null &&
                                numberOfSeatsEditText.getText()!=null && rentPerSeatEditText.getText()!=null) {

                            String messName = messNameEditText.getText().toString().trim();
                            String messAddress = messAddressEditText.getText().toString().trim();
                            int numberOfSeats = Integer.parseInt(numberOfSeatsEditText.getText().toString().trim());
                            int rentPerSeat = Integer.parseInt(rentPerSeatEditText.getText().toString().trim());

                            messDetails.put("mess_name", messName);
                            messDetails.put("mess_address", messAddress);
                            messDetails.put("number_of_seats", numberOfSeats);
                            messDetails.put("rent_per_seat", rentPerSeat);
                            messDetails.put("admin", Constants.userKey);

                            breakFastDetails.put("menu", "");
                            breakFastDetails.put("price", 0);
                            lunchDetails.put("menu", "");
                            lunchDetails.put("price", 0);
                            dinnerDetails.put("menu", "");
                            dinnerDetails.put("price", 0);
                            mealRequest.put("breakfast", 0);
                            mealRequest.put("lunch", 0);
                            mealRequest.put("dinner", 0);

                            messDetails.put("breakfast", breakFastDetails);
                            messDetails.put("lunch", lunchDetails);
                            messDetails.put("dinner", dinnerDetails);
                            messDetails.put("meal_request", mealRequest);
                            messDetails.put("residents", "");

                            String messKey = messName.toLowerCase() + "_" + messAddress.toLowerCase();

                            FirebaseDatabase.getInstance().getReference().child("Messes")
                                    .child(messKey.replace(" ","")).setValue(messDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(requireContext(), "Mess added successfully", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(requireContext(), "Failed to add mess data", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

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