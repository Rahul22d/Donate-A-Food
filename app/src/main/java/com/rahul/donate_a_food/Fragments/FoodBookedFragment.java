package com.rahul.donate_a_food.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

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
    private String userId;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFoodBookedBinding.inflate(inflater, container, false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        ordersRef = FirebaseDatabase.getInstance().getReference("AcceptedOrders");

        orderList = new ArrayList<>();
        adapter = new FoodBookedAdapter(getContext(), orderList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        loadFoodOrders();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        requestLocationPermissionAndStartUpdates();

        return binding.getRoot();
    }

    private void loadFoodOrders() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    OrderBooked order = orderSnapshot.getValue(OrderBooked.class);
                    if (order != null && "Accepted".equals(order.getStatus()) && userId.equals(order.getReceiverId())) {
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

    private void requestLocationPermissionAndStartUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setInterval(3000)
                .setFastestInterval(2000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    Log.d("LocationUpdate", "Lat: " + lat + ", Lng: " + lng);
                    adapter.updateCurrentLocation(lat, lng);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

        // Also get immediate fresh location once
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lng = location.getLongitude();
                            Log.d("InitialLocation", "Lat: " + lat + ", Lng: " + lng);
                            adapter.updateCurrentLocation(lat, lng);
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    // Handle runtime permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Log.e("PermissionDenied", "Location permission was denied.");
            }
        }
    }
}
