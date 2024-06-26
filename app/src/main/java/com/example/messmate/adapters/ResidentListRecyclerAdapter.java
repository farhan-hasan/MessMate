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
import com.example.messmate.models.ResidentDetailsModel;

import java.util.ArrayList;

public class ResidentListRecyclerAdapter extends RecyclerView.Adapter<ResidentListRecyclerAdapter.ViewHolder> {
    Context context;

    ArrayList<ResidentDetailsModel> residentList;
    public ResidentListRecyclerAdapter(Context context, ArrayList<ResidentDetailsModel> residentList) {
        this.context = context;
        this.residentList = residentList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.resident_card_view, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResidentDetailsModel item = residentList.get(position);
        holder.residentName.setText(item.name);
        holder.residentEmail.setText(item.email);
        holder.residentPhone.setText(item.phone);

        holder.itemView.setPadding(0, 0, 0, 0);
    }

    @Override
    public int getItemCount() {
        return residentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView residentName, residentEmail, residentPhone;
        public CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            residentName = itemView.findViewById(R.id.residentNameTextView);
            residentEmail = itemView.findViewById(R.id.residentEmailTextView);
            residentPhone = itemView.findViewById(R.id.residentPhoneTextView);
            cardView = itemView.findViewById(R.id.resident_card_view);
        }
    }

}
