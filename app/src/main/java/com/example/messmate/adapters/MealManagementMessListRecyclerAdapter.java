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
import com.example.messmate.models.SearchMessDetailsModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MealManagementMessListRecyclerAdapter extends FirebaseRecyclerAdapter<MessDetailsModel, MealManagementMessListRecyclerAdapter.ViewHolder> {
    Context context;
    private final MealManagementMessListRecyclerAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MessDetailsModel item);
    }

    public MealManagementMessListRecyclerAdapter(Context context,
                                                 MealManagementMessListRecyclerAdapter.OnItemClickListener listener,
                                                 @NonNull FirebaseRecyclerOptions<MessDetailsModel> options) {
        super(options);
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.search_mess_list_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull MessDetailsModel model) {
        holder.messName.setText(model.mess_name);
        holder.messAddress.setText("Address: " + model.mess_address);
        holder.adminPhone.setText("Admin phone: " + model.admin_phone);
        holder.availableSeats.setText("Available seats: " + String.valueOf(model.available_seats));
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
