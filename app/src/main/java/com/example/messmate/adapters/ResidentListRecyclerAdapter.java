package com.example.messmate.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messmate.R;
import com.example.messmate.models.MessDetailsModel;
import com.example.messmate.models.UserDetailsModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;
import java.util.List;

public class ResidentListRecyclerAdapter extends RecyclerView.Adapter<ResidentListRecyclerAdapter.ViewHolder> {
    Context context;
    List<UserDetailsModel> residentList;
    private final ResidentListRecyclerAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onPaidButtonClick(UserDetailsModel item);
        void onRemoveButtonClick(UserDetailsModel item);
    }

    public ResidentListRecyclerAdapter(Context context,
                                       ResidentListRecyclerAdapter.OnItemClickListener listener,
                                       List<UserDetailsModel> residentList) {
        this.context = context;
        this.listener = listener;
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
        UserDetailsModel resident = residentList.get(position);
        holder.residentName.setText(resident.username);
        holder.residentEmail.setText("Email: " + resident.email);
        holder.residentPhone.setText("Phone: " + resident.phone);
        holder.itemView.setPadding(0, 0, 0, 0);
    }

    @Override
    public int getItemCount() {
        return residentList.size();
    }
    public void updateList(UserDetailsModel newUser) {
        residentList.add(newUser);
        notifyDataSetChanged();
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
