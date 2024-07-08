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

import com.bumptech.glide.Glide;
import com.example.messmate.R;
import com.example.messmate.models.MessDetailsModel;
import com.example.messmate.models.UserDetailsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

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

        loadProfilImages(holder, resident);

    }

    public void loadProfilImages(@NonNull ViewHolder holder, UserDetailsModel resident) {
        StorageReference storageReference = null;
        
        try {
            storageReference = FirebaseStorage.getInstance().getReference().child("user_images/" + resident.getKey() + ".jpg");
        } catch (Exception e) {
            Log.d("EXCEPTION", e.getMessage());
        }
        // Fetch the download URL
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Use Glide to load the image into the ImageView
            Glide.with(context)
                    .load(uri)
                    .into(holder.mealResidentImage);

            // If you prefer to use Picasso:
            // Picasso.get().load(uri).into(imageView);
        }).addOnFailureListener(exception -> {
            Log.d("IMAGE", resident.getKey() +  " image does not exist");
        });
    }

    @Override
    public int getItemCount() {
        return residentList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView residentName, mealResidentBreakfastAmountTextView, mealResidentLunchAmountTextView;
        TextView mealResidentDinnerAmountTextView;
        CircleImageView mealResidentImage;
        public CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            residentName = itemView.findViewById(R.id.mealResidentNameTextView);
            mealResidentBreakfastAmountTextView =  itemView.findViewById(R.id.mealResidentBreakfastAmountTextView);
            mealResidentLunchAmountTextView =  itemView.findViewById(R.id.mealResidentLunchAmountTextView);
            mealResidentDinnerAmountTextView =  itemView.findViewById(R.id.mealResidentDinnerAmountTextView);
            mealResidentImage =  itemView.findViewById(R.id.mealResidentImage);
        }
    }


}
