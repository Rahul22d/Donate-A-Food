package com.rahul.donate_a_food.Adapter;
//
//
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.rahul.donate_a_food.Class.Order;
import com.rahul.donate_a_food.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rahul.donate_a_food.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//
//public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
//
//    private ArrayList<Order> orderList;
//    private DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
//
//    public OrderAdapter(ArrayList<Order> orderList) {
//        this.orderList = orderList;
//    }
//
//    @NonNull
//    @Override
//    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
//        return new OrderViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
//        Order order = orderList.get(position);
//
//        holder.productName.setText(order.getProductName());
//        holder.foodQuantity.setText("Quantity: " + order.getFoodQuantity());
//        holder.status.setText("Status: " + order.getStatus());
//
//        // Load image using Picasso with caching enabled
//        Picasso.get()
//                .load(order.getImageUrl())
////                    .placeholder(R.drawable.placeholder_image)  // Placeholder while loading
////                    .error(R.drawable.error_image)  // Error image if loading fails
//                .into(holder.imageView);
//
//
//        holder.btnAccept.setOnClickListener(v -> updateOrderStatus(order, "Accepted", holder));
//        holder.btnReject.setOnClickListener(v -> updateOrderStatus(order, "Rejected", holder));
//    }
//
//    private void updateOrderStatus(Order order, String status, OrderViewHolder holder) {
//        ordersRef.child(order.getOrderId()).child("status").setValue(status)
//                .addOnSuccessListener(aVoid -> {
//                    holder.status.setText("Status: " + status);
//                    Toast.makeText(holder.itemView.getContext(), "Order " + status, Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e ->
//                        Toast.makeText(holder.itemView.getContext(), "Failed to update order", Toast.LENGTH_SHORT).show()
//                );
//    }
//
//
//
//    @Override
//    public int getItemCount() {
//        return orderList.size();
//    }
//
//    public static class OrderViewHolder extends RecyclerView.ViewHolder {
//        TextView productName, foodQuantity, status;
//        ImageView imageView;
//        Button btnAccept, btnReject;
//
//        public OrderViewHolder(@NonNull View itemView) {
//            super(itemView);
//            productName = itemView.findViewById(R.id.productName);
//            foodQuantity = itemView.findViewById(R.id.foodQuantity);
//            status = itemView.findViewById(R.id.status);
//            imageView = itemView.findViewById(R.id.productImage);
//            btnAccept = itemView.findViewById(R.id.btnAccept);
//            btnReject = itemView.findViewById(R.id.btnReject);
//
//        }
//    }
//
//    public void conFirmOrder(Order order) {
//        // Implement logic to confirm the order
//        // For example, you can update the order status in the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference ordersRef = database.getReference("ConfirmOrders");
//
//        // for check user are login
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        String userId = mAuth.getCurrentUser().getUid();
//        if(userId == null){
//            return;
//        }
//        Map<String, Object> orderData = new HashMap<>();
//        orderData.put("donorId", order.getDonorId());
//        orderData.put("receiverId", order.getReceiverId());
//        orderData.put("productId", order.getProductId());
//        orderData.put("foodName", order.getProductName());
//        orderData.put("foodQuantity", order.getFoodQuantity());
//        orderData.put("imageUrl", order.getImageUrl());
//        orderData.put("status", order.getStatus());
//        ordersRef.push().setValue(orderData);
//
//    }
//
//    public void rejectOrder() {
//        // Implement logic to reject the order
//        // For example, you can update the order status in the database
//    }
//
//    public void ShowDonorAndReceiverDetails(Order order) {
//        // Implement logic to show donor details
//        // For example, you can navigate to a donor profile screen
//
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        String userId = mAuth.getCurrentUser().getUid();
//        if(userId == null){
//            return;
//        }
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        if(order.getDonorId() == userId) {
//            DatabaseReference donorRef = database.getReference("Users").child(order.getDonorId());
//
//        }
//
//    }
//}
//

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private ArrayList<Order> orderList;
    private DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
    private String userId; // Store the current logged-in user ID

    public OrderAdapter(ArrayList<Order> orderList) {
        this.orderList = orderList;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        this.userId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : null;
    }

    @Override
    public int getItemViewType(int position) {
        Order order = orderList.get(position);
        if (order.getDonorId().equals(userId)) {
            return 0; // Donor Layout
        } else {
            return 1; // Receiver Layout
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) { // Donor Layout
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        } else { // Receiver Layout
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_receiver, parent, false);
        }
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.productName.setText(order.getProductName());
        holder.foodQuantity.setText("Quantity: " + order.getFoodQuantity());
        holder.status.setText("Status: " + order.getStatus());

        Picasso.get().load(order.getImageUrl()).into(holder.imageView);

        if (holder.btnAccept != null && holder.btnReject != null && order.getStatus().equals("Pending")) { // Only visible in donor layout
            holder.btnAccept.setOnClickListener(v -> {
                updateOrderStatus(order, "Accepted", holder);
                // Handle accept button click
                acceptOrder(holder.itemView.getContext(), order);
                holder.btnAccept.setEnabled(false);
                holder.btnReject.setEnabled(false);
            });
            holder.btnReject.setOnClickListener(v -> updateOrderStatus(order, "Rejected", holder));
        } else {
            holder.btnAccept.setEnabled(false);
            holder.btnReject.setEnabled(false);
        }
    }

    private void updateOrderStatus(Order order, String status, OrderViewHolder holder) {
        ordersRef.child(order.getOrderId()).child("status").setValue(status)
                .addOnSuccessListener(aVoid -> {
                    holder.status.setText("Status: " + status);
                    Toast.makeText(holder.itemView.getContext(), "Order " + status, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(holder.itemView.getContext(), "Failed to update order", Toast.LENGTH_SHORT).show()
                );
    }


    private void acceptOrder(Context context, Order order) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference acceptedOrdersRef = FirebaseDatabase.getInstance().getReference("AcceptedOrders");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        String newId = acceptedOrdersRef.push().getKey();


//        Log.d("Firebase", "User ID: " + order.getDonorId());
        usersRef.child(order.getDonorId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("FirebaseDebug", "Checking donor ID: " + order.getDonorId());

                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        // Retrieve user details
                        String donorName = user.getName();
                        String donorNumber = user.getNumber();

                        if (donorName != null && donorNumber != null) {
                            Map<String, Object> acceptedOrder = new HashMap<>();
                            acceptedOrder.put("donorId", order.getDonorId());
                            acceptedOrder.put("receiverId", order.getReceiverId());
                            acceptedOrder.put("productId", order.getProductId());
                            acceptedOrder.put("foodName", order.getProductName());
                            acceptedOrder.put("foodQuantity", order.getFoodQuantity());
                            acceptedOrder.put("imageUrl", order.getImageUrl());
                            acceptedOrder.put("status", "Accepted");
                            acceptedOrder.put("donorName", donorName);
                            acceptedOrder.put("number", donorNumber);


                            acceptedOrdersRef.child(newId).setValue(acceptedOrder)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Order Accepted", Toast.LENGTH_SHORT).show();
                                        updateFoodQuantity(order.getProductId(), order.getFoodQuantity());

                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to accept order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });

                            Log.d("FirebaseSuccess", "User Found: " + donorName + ", Contact: " + donorNumber);
                        } else {
                            Log.e("FirebaseError", "User data is incomplete: " + snapshot.getValue());
                        }
                    }
                } else {
                    Log.e("FirebaseError", "User not found for ID: " + order.getDonorId());
                    if (context != null) {
                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Database error: " + error.getMessage());
            }
        });


//        Order acceptedOrder = new Order(
//                newId, order.getDonorId(), order.getReceiverId(), order.getProductId(),
//                order.getProductName(), order.getFoodQuantity(), order.getImageUrl(), "Accepted"
//        );


        //Log.d("Dono" + order.getOrderId() + " Name", donorName.get(0) + " number" + number.get(0) + " food" + order.getProductName());
    }

    private void updateFoodQuantity(String foodId, int orderedQuantity) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(foodId);

        productRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentQuantity = mutableData.child("foodQuantity").getValue(Integer.class);

                if (currentQuantity == null) {
                    return Transaction.success(mutableData); // No update if null
                }

                int newQuantity = currentQuantity - orderedQuantity;
                if (newQuantity < 0) newQuantity = 0; // Prevent negative values

                mutableData.child("foodQuantity").setValue(newQuantity);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (error != null) {
                    Log.e("FirebaseUpdate", "Failed to update food quantity: " + error.getMessage());
                } else {
                    Log.d("FirebaseUpdate", "Food quantity updated successfully");
                }
            }
        });
    }



    private void rejectOrder(Context context, Order order) {
        Toast.makeText(context, "Order Rejected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView productName, foodQuantity, status;
        ImageView imageView;
        Button btnAccept, btnReject;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            foodQuantity = itemView.findViewById(R.id.foodQuantity);
            status = itemView.findViewById(R.id.status);
            imageView = itemView.findViewById(R.id.productImage);
            btnAccept = itemView.findViewById(R.id.btnAccept); // These buttons are only in donor layout
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }


}

