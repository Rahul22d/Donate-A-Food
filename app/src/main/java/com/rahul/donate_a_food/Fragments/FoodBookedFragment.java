package com.rahul.donate_a_food.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rahul.donate_a_food.Adapter.FoodBookedAdapter;
import com.rahul.donate_a_food.Class.OrderBooked;

import com.rahul.donate_a_food.databinding.FragmentFoodBookedBinding;

import java.util.ArrayList;
import java.util.List;

public class FoodBookedFragment extends Fragment {

    private FragmentFoodBookedBinding binding;
    private DatabaseReference ordersRef;
    private List<OrderBooked> orderList;
    private FoodBookedAdapter adapter;
    private String userId; // Store the current user's ID

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFoodBookedBinding.inflate(inflater, container, false);

        // Get the current user's ID from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid(); // Logged-in user ID
        }

        // Initialize Firebase reference
        ordersRef = FirebaseDatabase.getInstance().getReference("AcceptedOrders");

        // Initialize RecyclerView
        orderList = new ArrayList<OrderBooked>();
        adapter = new FoodBookedAdapter(getContext(), orderList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        // Load booked food data for the receiver
        loadFoodOrders();

        return binding.getRoot();
    }

    private void loadFoodOrders() {
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    OrderBooked order = orderSnapshot.getValue(OrderBooked.class);
                    if (order != null && order.getStatus().equals("Accepted") && order.getReceiverId().equals(userId)) {
                        orderList.add(order);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to load orders: " + error.getMessage());
            }
        });
    }
}
