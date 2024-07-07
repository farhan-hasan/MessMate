package com.example.messmate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messmate.R;
import com.example.messmate.models.MessDetailsModel;
import com.example.messmate.models.SearchMessDetailsModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchMessListRecyclerAdapter extends FirebaseRecyclerAdapter<SearchMessDetailsModel, SearchMessListRecyclerAdapter.ViewHolder> {
    Context context;


    public SearchMessListRecyclerAdapter(Context context,
                                         @NonNull FirebaseRecyclerOptions<SearchMessDetailsModel> options) {
        super(options);
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.search_mess_list_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull SearchMessDetailsModel model) {
        holder.messName.setText(model.mess_name);
        holder.messAddress.setText("Address: " + model.mess_address);
        holder.adminPhone.setText("Admin phone: " + model.admin_phone);
        holder.availableSeats.setText("Available seats: " + String.valueOf(model.available_seats));
        holder.itemView.setPadding(0, 0, 0, 0);
        loadProfileImage(model.mess_key, holder);

    }
    private void loadProfileImage(String messKey,@NonNull ViewHolder  holder) {


        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("mess_images/" + messKey + ".jpg");

        // Fetch the download URL
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Use Glide to load the image into the ImageView
            Glide.with(context)
                    .load(uri)
                    .into(holder.searchMessImage);

        }).addOnFailureListener(exception -> {
            // Handle any errors
        });
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messName, messAddress, adminPhone, availableSeats;

        public CardView cardView;
        CircleImageView searchMessImage;

        public ViewHolder(View itemView) {
            super(itemView);
            messName = itemView.findViewById(R.id.messNameTextView);
            messAddress = itemView.findViewById(R.id.messAddressTextView);
            adminPhone = itemView.findViewById(R.id.adminPhone);
            availableSeats = itemView.findViewById(R.id.availableSeatsTextView);
            searchMessImage = itemView.findViewById(R.id.searchMessImage);
        }
    }
}
