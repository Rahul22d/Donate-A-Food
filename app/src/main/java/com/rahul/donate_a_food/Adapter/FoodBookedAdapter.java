//package com.rahul.donate_a_food.Adapter;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//
//import com.rahul.donate_a_food.Class.OrderBooked;
//
//import com.rahul.donate_a_food.R;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//public class FoodBookedAdapter extends RecyclerView.Adapter<FoodBookedAdapter.ViewHolder> {
//
//    private Context context;
//    private List<OrderBooked> orderList;
//
//    public FoodBookedAdapter(Context context, List<OrderBooked> orderList) {
//        this.context = context;
//        this.orderList = orderList;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_food_booked, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        OrderBooked order = orderList.get(position);
//
//        holder.foodName.setText(order.getFoodName());
//        holder.donorName.setText("Donor: " + order.getDonorName());
//        holder.foodQuantity.setText("Quantity: " + order.getFoodQuantity());
//        holder.location.setText("Location: " + order.getLocation());
//        holder.number.setText("Contact: " + order.getDonorNumber());
//        holder.status.setText("Status: " + order.getStatus());
//        Log.d("Donor number", "Number : " + order.getDonorNumber());
//        // Load image using Glide
////        Glide.with(context).load(order.getImageUrl()).into(holder.foodImage);
//        Picasso.get().load(order.getImageUrl()).into(holder.foodImage);
//    }
//
//    @Override
//    public int getItemCount() {
//        return orderList.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView foodName, donorName, foodQuantity, location, number, status;
//        ImageView foodImage;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            foodName = itemView.findViewById(R.id.foodName);
//            donorName = itemView.findViewById(R.id.donorName);
//            foodQuantity = itemView.findViewById(R.id.foodQuantity);
//            location = itemView.findViewById(R.id.location);
//            number = itemView.findViewById(R.id.number);
//            status = itemView.findViewById(R.id.status);
//            foodImage = itemView.findViewById(R.id.foodImage);
//        }
//    }
//}

package com.rahul.donate_a_food.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
    private double currentLat = 0.0;
    private double currentLng = 0.0;

    public FoodBookedAdapter(Context context, List<OrderBooked> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void updateCurrentLocation(double lat, double lng) {
        this.currentLat = lat;
        this.currentLng = lng;
        notifyDataSetChanged(); // Update map click functionality
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
        holder.location.setText("Location: " + order.getLocation());
        holder.number.setText("Contact: " + order.getDonorNumber());
        holder.status.setText("Status: " + order.getStatus());

        Picasso.get().load(order.getImageUrl()).into(holder.foodImage);

        holder.map.setOnClickListener(v -> {
            double foodLat = order.getLatitude();
            double foodLng = order.getLongitude();

            String uri = "https://www.google.com/maps/dir/?api=1" +
                    "&origin=" + currentLat + "," + currentLng +
                    "&destination=" + foodLat + "," + foodLng +
                    "&travelmode=driving";

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, donorName, foodQuantity, location, number, status;
        ImageView foodImage, map;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            donorName = itemView.findViewById(R.id.donorName);
            foodQuantity = itemView.findViewById(R.id.foodQuantity);
            location = itemView.findViewById(R.id.location);
            number = itemView.findViewById(R.id.number);
            status = itemView.findViewById(R.id.status);
            foodImage = itemView.findViewById(R.id.foodImage);
            map = itemView.findViewById(R.id.map);
        }
    }
}
