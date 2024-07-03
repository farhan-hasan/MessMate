package com.example.messmate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messmate.R;
import com.example.messmate.models.MessDetailsModel;
import com.example.messmate.models.SearchMessDetailsModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

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
        holder.messAddress.setText(model.mess_address);
        holder.adminPhone.setText(model.admin_phone);
        holder.availableSeats.setText(String.valueOf(model.available_seats));
        holder.itemView.setPadding(0, 0, 0, 0);

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

        }
    }
}
