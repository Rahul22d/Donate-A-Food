package com.rahul.donate_a_food.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.rahul.donate_a_food.Class.OrderBooked;

import com.rahul.donate_a_food.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodBookedAdapter extends RecyclerView.Adapter<FoodBookedAdapter.ViewHolder> {

    private Context context;
    private List<OrderBooked> orderList;

    public FoodBookedAdapter(Context context, List<OrderBooked> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_booked, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderBooked order = orderList.get(position);

        holder.foodName.setText(order.getFoodName());
        holder.donorName.setText("Donor: " + order.getDonorName());
        holder.foodQuantity.setText("Quantity: " + order.getFoodQuantity());
        holder.number.setText("Contact: " + order.getNumber());
        holder.status.setText("Status: " + order.getStatus());

        // Load image using Glide
//        Glide.with(context).load(order.getImageUrl()).into(holder.foodImage);
        Picasso.get().load(order.getImageUrl()).into(holder.foodImage);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, donorName, foodQuantity, number, status;
        ImageView foodImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            donorName = itemView.findViewById(R.id.donorName);
            foodQuantity = itemView.findViewById(R.id.foodQuantity);
            number = itemView.findViewById(R.id.number);
            status = itemView.findViewById(R.id.status);
            foodImage = itemView.findViewById(R.id.foodImage);
        }
    }
}
