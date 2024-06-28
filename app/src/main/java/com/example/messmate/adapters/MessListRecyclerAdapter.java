package com.example.messmate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messmate.R;
import com.example.messmate.models.MessDetailsModel;
import com.example.messmate.models.MessDetailsModel;
import com.firebase.ui.database.*;

import java.util.ArrayList;

public class MessListRecyclerAdapter extends FirebaseRecyclerAdapter<MessDetailsModel, MessListRecyclerAdapter.ViewHolder> {
    Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MessDetailsModel item);
    }

    public MessListRecyclerAdapter(Context context,
                                   OnItemClickListener listener,
                                   @NonNull FirebaseRecyclerOptions<MessDetailsModel> options) {
        super(options);
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.mess_list_card, parent, false);
        return new ViewHolder(view);
    }



    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull MessDetailsModel model) {
        holder.messName.setText(model.mess_name);
        holder.messAddress.setText("Address: " + model.mess_address);
        holder.adminPhone.setText("Admin Phone: " + model.admin_phone);
        holder.availableSeats.setText("Avalailabel Seats: " + String.valueOf(model.available_seats));
        holder.itemView.setPadding(0, 0, 0, 0);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(model);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messName, messAddress, adminPhone, availableSeats;

        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            messName = itemView.findViewById(R.id.messNameTextView);
            messAddress = itemView.findViewById(R.id.messAddressTextView);
            adminPhone = itemView.findViewById(R.id.adminPhone);
            availableSeats = itemView.findViewById(R.id.availableSeatsTextView);
            cardView = itemView.findViewById(R.id.mess_list_card_view);

        }
    }
}
