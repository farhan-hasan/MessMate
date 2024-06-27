package com.example.messmate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messmate.R;
import com.example.messmate.models.MessListCardModel;
import com.example.messmate.models.ResidentDetailsModel;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;

public class MessListRecyclerAdapter extends RecyclerView.Adapter<MessListRecyclerAdapter.ViewHolder> {
    Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MessListCardModel item);
    }
    ArrayList<MessListCardModel> messList;
    public MessListRecyclerAdapter(Context context, ArrayList<MessListCardModel> messList, OnItemClickListener listener) {
        this.context = context;
        this.messList = messList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.mess_list_card, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessListCardModel item = messList.get(position);
        holder.messName.setText(item.messName);
        holder.messAddress.setText(item.messAddress);
        holder.adminPhone.setText(item.adminPhone);
        holder.availableSeats.setText(item.availableSeats);

        holder.itemView.setPadding(0, 0, 0, 0);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item);
            }
        });



    }

    @Override
    public int getItemCount() {
        return messList.size();
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
