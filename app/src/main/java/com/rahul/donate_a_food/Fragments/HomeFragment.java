////package com.rahul.project.ui.home;
////
////import android.os.Bundle;
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.TextView;
////import android.widget.Toast;
////
////import androidx.annotation.NonNull;
////import androidx.fragment.app.Fragment;
////import androidx.lifecycle.ViewModelProvider;
////
////import com.google.firebase.database.DataSnapshot;
////import com.google.firebase.database.DatabaseError;
////import com.google.firebase.database.DatabaseReference;
////import com.google.firebase.database.FirebaseDatabase;
////import com.google.firebase.database.ValueEventListener;
////import com.rahul.project.databinding.FragmentHomeBinding;
////
////public class HomeFragment extends Fragment {
////
////    private FragmentHomeBinding binding;
////    private DatabaseReference databaseReference;
////
////    public View onCreateView(@NonNull LayoutInflater inflater,
////                             ViewGroup container, Bundle savedInstanceState) {
////        HomeViewModel homeViewModel =
////                new ViewModelProvider(this).get(HomeViewModel.class);
////
////        binding = FragmentHomeBinding.inflate(inflater, container, false);
////        View root = binding.getRoot();
////
////        // Get reference to Firebase Realtime Database
////        databaseReference = FirebaseDatabase.getInstance().getReference("products"); // assuming you are fetching data from the "products" node
////
////        // Retrieve data from Firebase
////        retrieveData();
////
////        return root;
////    }
////
////    private void retrieveData() {
////        // Create a listener to get the data
////        databaseReference.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                StringBuilder dataBuilder = new StringBuilder();
////
////                // Iterate through the dataSnapshot and build the data string
////                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
////                    // Assuming each child node represents a product
////                    String productName = snapshot.child("name").getValue(String.class);
////                    Integer quantity = snapshot.child("quantity").getValue(Integer.class);
////                    String location = snapshot.child("location").getValue(String.class);
////
////                    // Here change and make card for product view
////                    // Append the product information to the StringBuilder
////                    if (productName != null && quantity != null && location != null) {
////                        dataBuilder.append("Product: ").append(productName)
////                                .append("\nQuantity: ").append(quantity)
////                                .append("\nLocation: ").append(location)
////                                .append("\n\n");
////                    }
////                }
////
////                // Update the TextView with the retrieved data
////                TextView textView = binding.textHome;
////                textView.setText(dataBuilder.toString());
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////                // Handle errors here
////                Toast.makeText(getContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
////            }
////        });
////    }
////
////    @Override
////    public void onDestroyView() {
////        super.onDestroyView();
////        binding = null;
////    }
////}
//
//package com.rahul.donate_a_food.Fragments;
//
////import static android.os.Build.VERSION_CODES.R;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.rahul.donate_a_food.databinding.FragmentHomeBinding;
//import com.rahul.donate_a_food.R;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class HomeFragment extends Fragment {
//
//    private FragmentHomeBinding binding;
//    private DatabaseReference databaseReference;
//    private RecyclerView recyclerView;
//    private ProductAdapter productAdapter;
//    private List<Product> productList;
//
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        // Initialize RecyclerView
//        recyclerView = binding.recyclerView;
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        productList = new ArrayList<>();
//        productAdapter = new ProductAdapter(productList);
//        recyclerView.setAdapter(productAdapter);
//
//        // Get reference to Firebase Realtime Database
//        databaseReference = FirebaseDatabase.getInstance().getReference("products");
//
//        // Retrieve data from Firebase
//        retrieveData();
////
//        return root;
//    }
//
//    private void retrieveData() {
//        // Create a listener to get the data
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                productList.clear();
//
//                // Iterate through the dataSnapshot and add products to the list
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String fullName = snapshot.child("fullName").getValue(String.class);
//                    Integer foodQuantity = snapshot.child("foodQuantity").getValue(Integer.class);
//                    String location = snapshot.child("location").getValue(String.class);
//                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
//                    String contactNumber = snapshot.child("contactNumber").getValue(String.class);
//
//                    if (fullName != null && foodQuantity != null && location != null && imageUrl != null && contactNumber != null) {
//                        productList.add(new Product(fullName, foodQuantity, location, imageUrl, contactNumber));
//                    }
//                }
//
//                // Notify the adapter that the data has been updated
//                productAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle errors here
//                Toast.makeText(getContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//
//    // Product class to represent product data
//    public static class Product {
//        private String fullName;
//        private int foodQuantity;
//        private String location;
//        private String imageUrl;
//
//        private String contactNumber;
//
//        public Product(String fullName, int foodQuantity, String location, String imageUrl, String contactNumer) {
//            this.fullName = fullName;
//            this.foodQuantity = foodQuantity;
//            this.location = location;
//            this.imageUrl = imageUrl;
//            this.contactNumber = contactNumer;
//        }
//
//        public String getFullName() {
//            return fullName;
//        }
//
//        public int getFoodQuantity() {
//            return foodQuantity;
//        }
//
//        public String getLocation() {
//            return location;
//        }
//
//        public String getImageUrl() {
//            return imageUrl;
//        }
//        public String getContactNumber() {
//            return contactNumber;
//        }
//    }
//
//    // Adapter for RecyclerView
//    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
//        private List<Product> productList;
//
//        public ProductAdapter(List<Product> productList) {
//            this.productList = productList;
//        }
//
//        @NonNull
//        @Override
//        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
//            return new ProductViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
//            Product product = productList.get(position);
//            holder.fullNameTextView.setText("Name: " + product.getFullName());
//            holder.foodQuantityTextView.setText("Quantity: " + product.getFoodQuantity());
//            holder.locationTextView.setText("Location: " + product.getLocation());
//            holder.contactTextView.setText("Contact: " + product.getContactNumber());
//            // Load the image using Picasso library
//            Picasso.get().load(product.getImageUrl()).into(holder.productImageView);
//        }
//
//        @Override
//        public int getItemCount() {
//            return productList.size();
//        }
//
//        public class ProductViewHolder extends RecyclerView.ViewHolder {
//            public TextView fullNameTextView;
//            public TextView foodQuantityTextView;
//            public TextView locationTextView;
//            public ImageView productImageView;
//
//            public TextView contactTextView;
//
//            public ProductViewHolder(View itemView) {
//                super(itemView);
//                fullNameTextView = itemView.findViewById(R.id.fullNameTextView);
//                foodQuantityTextView = itemView.findViewById(R.id.foodQuantityTextView);
//                locationTextView = itemView.findViewById(R.id.locationTextView);
//                productImageView = itemView.findViewById(R.id.productImageView);
//                contactTextView = itemView.findViewById(R.id.contactTextView);
//            }
//        }
//    }
//}
//
package com.rahul.donate_a_food.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.rahul.donate_a_food.R;
import com.rahul.donate_a_food.databinding.FragmentHomeBinding;
import com.squareup.picasso.Picasso;

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

    private static final int PAGE_SIZE = 10;  // Number of items per page

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

        // Set up Firebase query for pagination
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        databaseQuery = databaseReference.limitToFirst(PAGE_SIZE);  // Fetch first PAGE_SIZE products

        // Load initial data
        retrieveData();

        // Implement pagination when user scrolls
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1)) {  // Check if user reached the bottom
                    if (!isLoading && !isLastPage) {
                        // Load more data
                        retrieveData();
                    }
                }
            }
        });

        return root;
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
                    Integer foodQuantity = snapshot.child("foodQuantity").getValue(Integer.class);
                    String location = snapshot.child("foodDescription").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    String contactNumber = snapshot.child("contactNumber").getValue(String.class);

                    if (fullName != null && foodQuantity != null && location != null && imageUrl != null && contactNumber != null) {
                        productList.add(new Product(fullName, foodQuantity, location, imageUrl, contactNumber));
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

    // Product class to represent product data
    public static class Product {
        private String fullName;
        private int foodQuantity;
        private String location;
        private String imageUrl;
        private String contactNumber;

        public Product(String fullName, int foodQuantity, String location, String imageUrl, String contactNumer) {
            this.fullName = fullName;
            this.foodQuantity = foodQuantity;
            this.location = location;
            this.imageUrl = imageUrl;
            this.contactNumber = contactNumer;
        }

        public String getFullName() {
            return fullName;
        }

        public int getFoodQuantity() {
            return foodQuantity;
        }

        public String getLocation() {
            return location;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getContactNumber() {
            return contactNumber;
        }
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
            holder.fullNameTextView.setText("Name: " + product.getFullName());
            holder.foodQuantityTextView.setText("Quantity: " + product.getFoodQuantity());
            holder.locationTextView.setText("Location: " + product.getLocation());
            holder.contactTextView.setText("Contact: " + product.getContactNumber());

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
            public TextView locationTextView;
            public ImageView productImageView;
            public TextView contactTextView;

            public ProductViewHolder(View itemView) {
                super(itemView);
                fullNameTextView = itemView.findViewById(R.id.fullNameTextView);
                foodQuantityTextView = itemView.findViewById(R.id.foodQuantityTextView);
                locationTextView = itemView.findViewById(R.id.locationTextView);
                productImageView = itemView.findViewById(R.id.productImageView);
                contactTextView = itemView.findViewById(R.id.contactTextView);
            }
        }
    }

    // Show Product Details in a Dialog
    private void showProductDetailsDialog(Product product) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle(product.getFullName());
//
//        String details = "Food Quantity: " + product.getFoodQuantity() + "\n" +
//                "Location: " + product.getLocation() + "\n" +
//                "Contact: " + product.getContactNumber();
//
//        builder.setMessage(details)
//                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
//                .show();
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

