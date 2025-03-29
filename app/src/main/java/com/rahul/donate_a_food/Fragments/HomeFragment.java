package com.rahul.donate_a_food.Fragments;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.annotation.SuppressLint;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rahul.donate_a_food.CleanupWorker;
import com.rahul.donate_a_food.LocationViewModel;
import com.rahul.donate_a_food.MainActivity;
import com.rahul.donate_a_food.R;
import com.rahul.donate_a_food.databinding.FragmentHomeBinding;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private FirebaseAuth mAuth;
    DatabaseReference ordersRef;

    private static final int PAGE_SIZE = 10;
    private LocationViewModel locationViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
// Number of items per page

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        //String userId = mAuth.getCurrentUser().getUid();
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");


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

        if(mAuth.getCurrentUser() != null) {
            // for chack if any order place or not
            checkForOrdersAndShowPopup(); // Check for orders when fragment loads
        }

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
//        locationViewModel.getLatitude().observe(getViewLifecycleOwner(), lat -> currentLatitude = lat);
//        locationViewModel.getLongitude().observe(getViewLifecycleOwner(), lon -> currentLongitude = lon);
        locationViewModel.getLocation().observe(getViewLifecycleOwner(), location -> {
            currentLatitude = location[0];
            currentLongitude = location[1];
        });

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
                    String donorId = snapshot.child("donorId").getValue(String.class);
                    String productId = snapshot.child("productId").getValue(String.class);


                    // Calculate distance between current user and product location
                    float[] results = new float[1];
                    android.location.Location.distanceBetween(currentLatitude, currentLongitude, latitude, longitude, results);
                    float distanceInKm = results[0] / 1000; // Convert to kilometers
                    distanceInKm = (Math.round(distanceInKm * 100.0f) / 100.0f);  // Round to one decimal place
                    Log.d("Distance", "Distance: " + distanceInKm);
                    if (distanceInKm <= 15) { // Only show products within 15 km radius

                        if (fullName != null && foodQuantity != null && foodDescription != null && location != null && imageUrl != null && contactNumber != null) {
                            productList.add(new Product(fullName, ownerName, foodQuantity, foodDescription, location, distanceInKm, imageUrl, contactNumber, foodType, donorId, productId));
                        } else if (location == null) {
                            productList.add(new Product(fullName,ownerName, foodQuantity, foodDescription, "Location not found", distanceInKm, imageUrl, contactNumber, foodType, donorId, productId));
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
        private String donorId;
        private String productId;

        public Product(String fullName, String ownerName, int foodQuantity, String foodDescription, String location, float distance, String imageUrl, String contactNumber, String foodType, String donorId, String productId) {
            this.fullName = fullName;
            this.ownerName = ownerName;
            this.foodQuantity = foodQuantity;
            this.foodDescription = foodDescription;
            this.location = location;
            this.imageUrl = imageUrl;
            this.contactNumber = contactNumber;
            this.distance = distance;
            this.foodType = foodType;
            this.donorId = donorId;
            this.productId = productId;

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

        public String getDonorId() {
            return donorId;
        }
        public String getProductId() {return productId;}
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
            //this line add on 26 march-- start
//            if (productList == null || productList.isEmpty() || position >= productList.size()) {
//                return; // Prevent crash if list is empty or invalid index
//            }
            // end
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

            // this line add on 26 march --
            // Prevent crash when clicking on an empty product list
//            holder.itemView.setOnClickListener(v -> {
//                if (productList != null && position < productList.size()) {
//                    showProductDetailsDialog(productList.get(position)); // Safe access
//                }
//            });
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
//    @SuppressLint("RestrictedApi")
    private void showProductDetailsDialog(Product product) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customDialogView = inflater.inflate(R.layout.dialog_product_details, null);

        // for value change
        TextView tvValue = customDialogView.findViewById(R.id.tv_value);
        Button btnIncrease = customDialogView.findViewById(R.id.btn_increase);
        Button btnDecrease = customDialogView.findViewById(R.id.btn_decrease);

        //int[] quantity = {1}; // Using an array to modify within lambda expressions
        ArrayList<Integer> quantity = new ArrayList<>();
        quantity.add(1);
        tvValue.setText(String.valueOf(quantity.get(0)));

        btnIncrease.setOnClickListener(v -> {
            if(quantity.get(0) < product.getFoodQuantity()) {
                quantity.set(0, quantity.get(0) + 1);
                tvValue.setText(String.valueOf(quantity.get(0)));
            }
        });

        btnDecrease.setOnClickListener(v -> {
            if (quantity.get(0) > 1) {
                quantity.set(0, quantity.get(0) - 1);
                tvValue.setText(String.valueOf(quantity.get(0)));
            }
        });

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

//        submit.setOnClickListener(v -> dialog.dismiss());
        submit.setOnClickListener(v -> {
            // Dismiss the dialog
            dialog.dismiss();

            // Place the order
            placeOrder(product, quantity);
        });
    }

    // Method to place an order and store it in Firebase Realtime Database
    private void placeOrder(Product product, ArrayList quantity) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        String currentUserId = auth.getCurrentUser().getUid();

        // Generate a unique order ID using push()
        String orderId = ordersRef.push().getKey();

        if (orderId == null ) {
            Toast.makeText(getActivity(), "Failed to place order!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("donorId", product.getDonorId());
        orderData.put("receiverId", currentUserId);
        orderData.put("productId", product.getProductId());
        orderData.put("productName", product.getFullName());
        orderData.put("foodQuantity", quantity.get(0));
//        orderData.put("foodDescription", product.getFoodDescription());
        orderData.put("imageUrl", product.getImageUrl());
        orderData.put("status", "Pending");

//        orderData.put("timestamp", ServerValue.TIMESTAMP);

        ordersRef.child(orderId).setValue(orderData)
                .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Order Placed!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Order Failed!", Toast.LENGTH_SHORT).show());
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

    // for oorder booking
    private void checkForOrdersAndShowPopup() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("orders");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseRef.orderByChild("donorId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {  // If an order exists for the user
                    showOrderPopup();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching data", databaseError.toException());
            }
        });
    }

    // Show Popup Dialog
    private void showOrderPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Order Found");
        builder.setMessage("You have an active order. Click OK to proceed.");

        builder.setPositiveButton("OK", (dialog, which) -> {
            Fragment newFragment = new FoodBookedFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_content_main, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });


        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
