package com.rahul.donate_a_food.Adapter;

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

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private ArrayList<Order> orderList;
    private DatabaseReference ordersRef;
    private String userId;

    public OrderAdapter(ArrayList<Order> orderList) {
        this.orderList = orderList;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        this.userId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : null;
        this.ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
    }

    @Override
    public int getItemViewType(int position) {
        Order order = orderList.get(position);
        if (order.getDonorId() != null && order.getDonorId().equals(userId)) {
            return 0; // Donor Layout
        } else {
            return 1; // Receiver Layout
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_receiver, parent, false);
        }
        return new OrderViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.productName.setText(order.getProductName());
        holder.foodQuantity.setText("Quantity: " + order.getFoodQuantity());
        holder.status.setText("Status: " + order.getStatus());

        Picasso.get().load(order.getImageUrl()).into(holder.imageView);

        if(holder.btnAccept != null && holder.btnReject != null){
            if (order.getStatus().equals("Pending")) {
                holder.btnAccept.setOnClickListener(v -> {
                    acceptOrder(holder.itemView.getContext(), order, holder, position);
                    holder.btnAccept.setEnabled(false);
                    holder.btnReject.setEnabled(false);

                });

                holder.btnReject.setOnClickListener(v -> {
                    rejectOrder(holder.itemView.getContext(), order);
                    holder.btnAccept.setEnabled(false);
                    holder.btnReject.setEnabled(false);
                });
            } else {
                holder.btnAccept.setEnabled(false);
                holder.btnReject.setEnabled(false);
            }
        }
    }

    private void acceptOrder(Context context, Order order, OrderViewHolder holder, int position) {
        if (userId == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        if (order.getDonorId() == null) {
            Log.e("Firebase", "Donor ID is null, cannot accept order.");
            Toast.makeText(context, "Error: Donor ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference acceptedOrdersRef = FirebaseDatabase.getInstance().getReference("AcceptedOrders");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        String newId = acceptedOrdersRef.push().getKey();

        if (newId == null) {
            Log.e("Firebase", "Failed to generate new ID for accepted order.");
            Toast.makeText(context, "Error accepting order, please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Firebase", "New AcceptedOrder ID: " + newId);

        usersRef.child(order.getReceiverId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getName() != null && user.getNumber() != null) {
                        Map<String, Object> acceptedOrder = new HashMap<>();
                        acceptedOrder.put("donorId", order.getDonorId());
                        acceptedOrder.put("receiverId", order.getReceiverId());
                        acceptedOrder.put("productId", order.getProductId());
                        acceptedOrder.put("foodName", order.getProductName());
                        acceptedOrder.put("foodQuantity", order.getFoodQuantity());
                        acceptedOrder.put("donorName", order.getDonorName());
                        acceptedOrder.put("donorNumber", order.getDonorNumber());
                        acceptedOrder.put("location", order.getLocation());
                        acceptedOrder.put("latitude", order.getLatitude());
                        Log.d("Latitude in accept order", "Latitude"+ order.getLatitude());
                        acceptedOrder.put("longitude", order.getLongitude());
                        acceptedOrder.put("imageUrl", order.getImageUrl());
                        acceptedOrder.put("status", "Accepted");
                        acceptedOrder.put("receiverName", user.getName());
                        acceptedOrder.put("receiverNumber", user.getNumber());

                        Log.d("Doner details", "Donor Name: " + order.getDonorName() + ", Donor Number: " + order.getDonorNumber());
                        acceptedOrdersRef.child(newId).setValue(acceptedOrder)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firebase", "Order successfully added to AcceptedOrders");
                                    Toast.makeText(context, "Order Accepted", Toast.LENGTH_SHORT).show();
                                    updateOrderStatus(order, "Accepted", holder);
                                    updateFoodQuantity(order.getProductId(), order.getFoodQuantity());
                                    notifyItemChanged(position);

                                })
                                .addOnFailureListener(e -> {
                                    holder.btnAccept.setEnabled(false);
                                    holder.btnReject.setEnabled(false);
                                    Log.e("Firebase", "Failed to accept order: " + e.getMessage());
                                    Toast.makeText(context, "Failed to accept order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    Log.e("Firebase", "Donor user not found in database.");
                    Toast.makeText(context, "Donor not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
            }
        });
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

    private void updateFoodQuantity(String foodId, int orderedQuantity) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(foodId);

        productRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentQuantity = mutableData.child("foodQuantity").getValue(Integer.class);

                if (currentQuantity == null || currentQuantity < orderedQuantity) {
                    return Transaction.success(mutableData);
                }

                mutableData.child("foodQuantity").setValue(currentQuantity - orderedQuantity);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (error != null) {
                    Log.e("FirebaseUpdate", "Failed to update food quantity: " + error.getMessage());
                }
            }
        });
    }

    private void rejectOrder(Context context, Order order) {
        ordersRef.child(order.getOrderId()).child("status").setValue("Rejected")
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Order Rejected", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to reject order", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView productName, foodQuantity, status;
        ImageView imageView;
        Button btnAccept, btnReject;

        public OrderViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            foodQuantity = itemView.findViewById(R.id.foodQuantity);
            status = itemView.findViewById(R.id.status);
            imageView = itemView.findViewById(R.id.productImage);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
