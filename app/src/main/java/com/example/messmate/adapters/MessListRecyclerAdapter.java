package com.example.messmate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messmate.R;
import com.example.messmate.models.MessListCardModel;

import java.util.ArrayList;

public class MessListRecyclerAdapter extends RecyclerView.Adapter<MessListRecyclerAdapter.ViewHolder> {
    Context context;
    ArrayList<MessListCardModel> messList;
    public MessListRecyclerAdapter(Context context, ArrayList<MessListCardModel> messList) {
        this.context = context;
        this.messList = messList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.mess_list_card, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.messName.setText(messList.get(position).messName);
        holder.messAddress.setText(messList.get(position).messAddress);
        holder.adminPhone.setText(messList.get(position).adminPhone);
        holder.availableSeats.setText(messList.get(position).availabelSeats);

        holder.itemView.setPadding(0, 0, 0, 0);
    }

    @Override
    public int getItemCount() {
        return messList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messName, messAddress, adminPhone, availableSeats;
        public ViewHolder(View itemView) {
            super(itemView);
            messName = itemView.findViewById(R.id.messNameTextView);
            messAddress = itemView.findViewById(R.id.messAddressTextView);
            adminPhone = itemView.findViewById(R.id.adminPhone);
            availableSeats = itemView.findViewById(R.id.availableSeatsTextView);
        }

    }

}
