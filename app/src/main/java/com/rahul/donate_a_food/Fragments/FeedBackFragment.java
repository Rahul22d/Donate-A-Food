package com.rahul.donate_a_food.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rahul.donate_a_food.Adapter.OrderAdapter;
import com.rahul.donate_a_food.Class.Order;
import com.rahul.donate_a_food.R;

import java.util.ArrayList;

public class FeedBackFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private ArrayList<Order> orderList;
    private DatabaseReference ordersRef;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_back, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(orderAdapter);

        auth = FirebaseAuth.getInstance();
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        if (auth.getCurrentUser() != null) {
            loadOrders();
        }
        return view;
    }

    private void loadOrders() {
        String currentUserId = auth.getCurrentUser().getUid();

        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null) {
                        order.setOrderId(dataSnapshot.getKey());

                        // Add only if the user is a donor or a receiver
                        if (order.getReceiverId().equals(currentUserId) || order.getDonorId().equals(currentUserId)) {
                            orderList.add(order);
                        }
                    }
                }

                if (orderList.isEmpty()) {
                    Toast.makeText(getContext(), "No booked or donated food found!", Toast.LENGTH_SHORT).show();
                }

                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
