package com.rahul.donate_a_food.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rahul.donate_a_food.CleanupWorker;
import com.rahul.donate_a_food.LocationViewModel;
import com.rahul.donate_a_food.MainActivity;
import com.rahul.donate_a_food.R;
import com.rahul.donate_a_food.databinding.FragmentHomeBinding;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private Query databaseQuery;
    private double currentLatitude, currentLongitude;

    private MainActivity mainActivity;

    private static final int PAGE_SIZE = 10;
    private LocationViewModel locationViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
// Number of items per page

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // for clean up expire item
        scheduleCleanupWorker();

        // Initialize RecyclerView
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);
        swipeRefreshLayout = binding.swipeRefreshLayout;

        // Set up Firebase query for pagination
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        databaseQuery = databaseReference.limitToFirst(PAGE_SIZE);  // Fetch first PAGE_SIZE products



        // Load initial data
        retrieveData();

        // Implement pagination when user scrolls
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (dy < 0 && layoutManager.findFirstVisibleItemPosition() == 0) {
                    // Refresh data when user scrolls to top
                    Log.d("HomeFragment", "Refreshing data on scroll up...");
                    refreshData();
                } else if (!recyclerView.canScrollVertically(1)) {  // Check if user reached the bottom
                    if (!isLoading && !isLastPage) {
                        // Load more data
                        retrieveData();
                    }
                }

            }
        });
        // Pull-to-refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d("HomeFragment", "Pull to refresh triggered...");
            refreshData();
        });
//        getLocationFromPreferences();
        // 🔥 Observe Location Changes
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        locationViewModel.getLatitude().observe(getViewLifecycleOwner(), lat -> currentLatitude = lat);
        locationViewModel.getLongitude().observe(getViewLifecycleOwner(), lon -> currentLongitude = lon);

        return root;
    }
    private void refreshData() {
        isLastPage = false;
        isLoading = false;
        productList.clear();
        productAdapter.notifyDataSetChanged();
        databaseQuery = databaseReference.limitToFirst(PAGE_SIZE);
        retrieveData();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void retrieveData() {
        isLoading = true;

        databaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    isLastPage = true;  // No more data to load
                    return;
                }

                // If the productList is empty, avoid using last index to get the last item.
                if (!productList.isEmpty()) {
                    // Fetch more data based on the last item's fullName
                    String lastProductFullName = productList.get(productList.size() - 1).getFullName();
                    databaseQuery = databaseReference.orderByKey().startAfter(lastProductFullName).limitToFirst(PAGE_SIZE);
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String fullName = snapshot.child("foodName").getValue(String.class);
                    String ownerName = snapshot.child("userName").getValue(String.class);
                    Integer foodQuantity = snapshot.child("foodQuantity").getValue(Integer.class);
                    String foodDescription = snapshot.child("foodDescription").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    String contactNumber = snapshot.child("contactNumber").getValue(String.class);
                    double latitude = snapshot.child("latitude").getValue(Double.class);
                    double longitude = snapshot.child("longitude").getValue(Double.class);
                    String foodType = snapshot.child("foodType").getValue(String.class);
                    String location = getAddressFromCoordinates(latitude, longitude);


                    // Calculate distance between current user and product location
                    float[] results = new float[1];
                    android.location.Location.distanceBetween(currentLatitude, currentLongitude, latitude, longitude, results);
                    float distanceInKm = results[0] / 1000; // Convert to kilometers
                    distanceInKm = (Math.round(distanceInKm * 100.0f) / 100.0f);  // Round to one decimal place
                    Log.d("Distance", "Distance: " + distanceInKm);
                    if (distanceInKm <= 15) { // Only show products within 15 km radius

                        if (fullName != null && foodQuantity != null && foodDescription != null && location != null && imageUrl != null && contactNumber != null) {
                            productList.add(new Product(fullName, ownerName, foodQuantity, foodDescription, location, distanceInKm, imageUrl, contactNumber, foodType));
                        } else if (location == null) {
                            productList.add(new Product(fullName,ownerName, foodQuantity, foodDescription, "Location not found", distanceInKm, imageUrl, contactNumber, foodType));
                        }
                    } else {
                        Log.d("coordinate",currentLatitude + " lat, lon "+currentLongitude + " current lat "+latitude + " lon");
                    }
                }

                // Notify adapter about the new data
                productAdapter.notifyDataSetChanged();
                isLoading = false;

//                // Update query to load the next page
//                if (!isLastPage) {
//                    databaseQuery = databaseReference.orderByKey().startAfter(productList.get(productList.size() - 1).getFullName()).limitToFirst(PAGE_SIZE);
//                }
                // If no data was loaded, mark it as the last page
                if (dataSnapshot.getChildrenCount() < PAGE_SIZE) {
                    isLastPage = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isLoading = false;
                Toast.makeText(getContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getAddressFromCoordinates(double latitude, double longitude) {
        // Use Geocoder to convert latitude and longitude to address
        Geocoder geocoder = new Geocoder(getActivity());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Extract the full formatted address
                String fullAddress = address.getAddressLine(0);  // Full address line

                // Check if the full address contains a Plus Code (e.g., "UH3V+5FW")
                if (fullAddress != null && !fullAddress.isEmpty()) {
                    // Remove the Plus Code pattern at the beginning (e.g., "UH3V+5FW")
                    fullAddress = fullAddress.replaceAll("^[A-Za-z0-9]+\\+[A-Za-z0-9]+,?", "").trim();  // Remove Plus Code

                    return fullAddress;
                } else {
                    return null;
                }
            } else {
//                binding.textView3.setText("Addrss not found");
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Unable to get address. Please try again later.", Toast.LENGTH_LONG).show();
        }
        return null;
    }
//    // Method to retrieve location from SharedPreferences
//    private void getLocationFromPreferences() {
//        // Retrieve the latitude and longitude from SharedPreferences as Strings
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE);
//        String latString = sharedPreferences.getString("latitude", "0.0"); // Default to "0.0"
//        String lonString = sharedPreferences.getString("longitude", "0.0"); // Default to "0.0"
//
//        // Convert the Strings back to doubles
//        currentLatitude = Double.parseDouble(latString);
//        currentLongitude = Double.parseDouble(lonString);
//    }


    // Product class to represent product data
    public static class Product {
        private String fullName;
        private String ownerName;
        private int foodQuantity;
        private String foodDescription;
        private String imageUrl;
        private String contactNumber;
        private String location;
        public float distance;
        private String foodType;

        public Product(String fullName, String ownerName, int foodQuantity, String foodDescription, String location, float distance, String imageUrl, String contactNumber, String foodType) {
            this.fullName = fullName;
            this.ownerName = ownerName;
            this.foodQuantity = foodQuantity;
            this.foodDescription = foodDescription;
            this.location = location;
            this.imageUrl = imageUrl;
            this.contactNumber = contactNumber;
            this.distance = distance;
            this.foodType = foodType;

        }

        public String getFullName() {
            return fullName;
        }

        public int getFoodQuantity() {
            return foodQuantity;
        }
        public  float getDistance(){ return distance;}
        public String getFoodDescription() { return foodDescription; }

        public String getLocation() { return location; }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getContactNumber() {
            return contactNumber;
        }
        public String getOwnerName() {return ownerName;}
        public String getFoodType() {return foodType;}
    }

    // Adapter for RecyclerView
    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        private List<Product> productList;

        public ProductAdapter(List<Product> productList) {
            this.productList = productList;
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            Product product = productList.get(position);
            holder.fullNameTextView.setText( product.getFullName());
            holder.foodQuantityTextView.setText("Quantity: " + product.getFoodQuantity());
            holder.distanceTextView.setText("Distance : "+product.getDistance() + " Km");
            if("Non-Veg".equals(product.getFoodType())){
                holder.foodType.setImageResource(R.drawable.non_veg);
            }else{
                holder.foodType.setImageResource(R.drawable.veg);
            }
//            holder.foodDescriptionTextView.setText("Food Description: " + product.getFoodDescription());
//            holder.contactTextView.setText("Contact: " + product.getContactNumber());
//            holder.locationTextView.setText("Location: " + product.getLocation());

            // Load image using Picasso with caching enabled
            Picasso.get()
                    .load(product.getImageUrl())
//                    .placeholder(R.drawable.placeholder_image)  // Placeholder while loading
//                    .error(R.drawable.error_image)  // Error image if loading fails
                    .into(holder.productImageView);

            // Set item click listener to open dialog
            holder.itemView.setOnClickListener(v -> showProductDetailsDialog(product));
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        public class ProductViewHolder extends RecyclerView.ViewHolder {
            public TextView fullNameTextView;
            public TextView foodQuantityTextView;
            public TextView foodDescriptionTextView;
            public ImageView productImageView;
            public TextView contactTextView;
            public TextView locationTextView;
            public TextView distanceTextView;
            public ImageView foodType;

            public ProductViewHolder(View itemView) {
                super(itemView);
                fullNameTextView = itemView.findViewById(R.id.fullNameTextView);
                foodQuantityTextView = itemView.findViewById(R.id.foodQuantityTextView);
                distanceTextView = itemView.findViewById(R.id.distanceTextView);
//                locationTextView = itemView.findViewById(R.id.locationTextView);
                productImageView = itemView.findViewById(R.id.productImageView);
//                contactTextView = itemView.findViewById(R.id.contactTextView);
//                locationTextView = itemView.findViewById(R.id.locationTextView);
                foodType = itemView.findViewById(R.id.foodType);
            }
        }
    }

    // Show Product Details in a Dialog
    private void showProductDetailsDialog(Product product) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customDialogView = inflater.inflate(R.layout.dialog_product_details, null);

        // Get references to the views
        TextView productNameTextView = customDialogView.findViewById(R.id.productName);
        TextView ownerNameTextView = customDialogView.findViewById(R.id.ownerName);
        TextView locationTextView = customDialogView.findViewById(R.id.location);
        TextView foodQuantityTextView = customDialogView.findViewById(R.id.foodQuantity);
        TextView foodDescriptionTextView = customDialogView.findViewById(R.id.foodDescription);
        TextView contactNumberTextView = customDialogView.findViewById(R.id.contactNumber);
        Button submit = customDialogView.findViewById(R.id.btnOk);

        // Set data from the product object to the views
        productNameTextView.setText(product.getFullName());
        ownerNameTextView.setText("Donor: " + product.getOwnerName());
        locationTextView.setText("Location: " + product.getLocation());
        foodQuantityTextView.setText("Quantity: " + product.getFoodQuantity());
        foodDescriptionTextView.setText("Description: " + product.getFoodDescription());
        contactNumberTextView.setText("Contact: " + product.getContactNumber());

        // Create the custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(customDialogView);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Get the "OK" button and set its listener

        submit.setOnClickListener(v -> dialog.dismiss());
    }
    private void scheduleCleanupWorker() {
        PeriodicWorkRequest cleanupWorkRequest =
                new PeriodicWorkRequest.Builder(CleanupWorker.class, 1, TimeUnit.HOURS)
                        .build();

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                "ProductCleanupWorker",
                ExistingPeriodicWorkPolicy.REPLACE,
                cleanupWorkRequest
        );
    }

}
