package com.example.messmate.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messmate.R;
import com.example.messmate.models.MessDetailsModel;
import com.example.messmate.models.UserDetailsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealResidentListRecyclerAdapter extends RecyclerView.Adapter<MealResidentListRecyclerAdapter.ViewHolder> {
    Context context;
    List<UserDetailsModel> residentList;

    public MealResidentListRecyclerAdapter(Context context,
                                           List<UserDetailsModel> residentList) {
        this.context = context;
        this.residentList = residentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.resident_meal_management_card, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {
        UserDetailsModel resident = residentList.get(position);

        holder.residentName.setText(resident.getUsername());
        holder.mealResidentBreakfastAmountTextView.setText(String.valueOf(resident.getBreakfast_amount()));
        holder.mealResidentLunchAmountTextView.setText(String.valueOf(resident.getLunch_amount()));
        holder.mealResidentDinnerAmountTextView.setText(String.valueOf(resident.getDinner_amount()));
        holder.itemView.setPadding(0, 0, 0, 0);

    }

    @Override
    public int getItemCount() {
        return residentList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView residentName, mealResidentBreakfastAmountTextView, mealResidentLunchAmountTextView;
        TextView mealResidentDinnerAmountTextView;
        public CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            residentName = itemView.findViewById(R.id.mealResidentNameTextView);
            mealResidentBreakfastAmountTextView =  itemView.findViewById(R.id.mealResidentBreakfastAmountTextView);
            mealResidentLunchAmountTextView =  itemView.findViewById(R.id.mealResidentLunchAmountTextView);
            mealResidentDinnerAmountTextView =  itemView.findViewById(R.id.mealResidentDinnerAmountTextView);
        }
    }


}
