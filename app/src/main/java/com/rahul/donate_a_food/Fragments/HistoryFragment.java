//package com.rahul.donate_a_food.Fragments;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.rahul.donate_a_food.R;
//import com.rahul.donate_a_food.databinding.FragmentHistoryBinding;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//
//public class HistoryFragment extends Fragment {
//    private FragmentHistoryBinding binding;
//    private DatabaseReference databaseReference;
//    private StorageReference storageReference;
//    private FirebaseAuth mAuth;
//    private RecyclerView recyclerView;
//    private HistoryFragment.ProductAdapter productAdapter;
//    private List<HistoryFragment.Product> productList;
//    private boolean isLoading = false;
//    private boolean isLastPage = false;
//    private Query databaseQuery;
//    private static final int PAGE_SIZE = 10;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
////        return inflater.inflate(R.layout.fragment_history, container, false);
//        binding = FragmentHistoryBinding.inflate(inflater, container, false);
//
//
//        mAuth = FirebaseAuth.getInstance();
//        String userId = mAuth.getCurrentUser().getUid();
//        if(userId == null){
//            return binding.getRoot();
//        }
//        // for database reference
//        databaseReference = FirebaseDatabase.getInstance().getReference("users")
//                .child(userId)
//                .child("uploadHistory");
//        // for storage reference
//        storageReference = FirebaseStorage.getInstance().getReference("productImages");
//        // Initialize RecyclerView
//        recyclerView = binding.recyclerView;
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        productList = new ArrayList<>();
//        productAdapter = new HistoryFragment.ProductAdapter(productList);
//        recyclerView.setAdapter(productAdapter);
//
//        // Load initial data
//        retrieveData();
//
//        // Implement pagination when user scrolls
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                if (!recyclerView.canScrollVertically(1)) {  // Check if user reached the bottom
//                    if (!isLoading && !isLastPage) {
//                        // Load more data
//                        retrieveData();
//                    }
//                }
//            }
//        });
//
//        return binding.getRoot();
//    }
//
//    private void retrieveData() {
//        isLoading = true;
//
//        databaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists()) {
//                    isLastPage = true;  // No more data to load
//                    return;
//                }
//
//                // If the productList is empty, avoid using last index to get the last item.
//                if (!productList.isEmpty()) {
//                    // Fetch more data based on the last item's fullName
//                    String lastProductFullName = productList.get(productList.size() - 1).getFullName();
//                    databaseQuery = databaseReference.orderByKey().startAfter(lastProductFullName).limitToFirst(PAGE_SIZE);
//                }
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String fullName = snapshot.child("foodName").getValue(String.class);
//                    Integer foodQuantity = snapshot.child("foodQuantity").getValue(Integer.class);
//                    String location = snapshot.child("foodDescription").getValue(String.class);
//                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
//                    String contactNumber = snapshot.child("contactNumber").getValue(String.class);
//
//                    if (fullName != null && foodQuantity != null && location != null && imageUrl != null && contactNumber != null) {
//                        productList.add(new HistoryFragment.Product(fullName, foodQuantity, location, imageUrl, contactNumber));
//                    }
//                }
//
//                // Notify adapter about the new data
//                productAdapter.notifyDataSetChanged();
//                isLoading = false;
//
////                // Update query to load the next page
////                if (!isLastPage) {
////                    databaseQuery = databaseReference.orderByKey().startAfter(productList.get(productList.size() - 1).getFullName()).limitToFirst(PAGE_SIZE);
////                }
//                // If no data was loaded, mark it as the last page
//                if (dataSnapshot.getChildrenCount() < PAGE_SIZE) {
//                    isLastPage = true;
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                isLoading = false;
//                Toast.makeText(getContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Product class to represent product data
//    public static class Product {
//        private String fullName;
//        private int foodQuantity;
//        private String location;
//        private String imageUrl;
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
//
//        public String getContactNumber() {
//            return contactNumber;
//        }
//    }
//
//    // Adapter for RecyclerView
//    public class ProductAdapter extends RecyclerView.Adapter<HistoryFragment.ProductAdapter.ProductViewHolder> {
//        private List<HistoryFragment.Product> productList;
//
//        public ProductAdapter(List<HistoryFragment.Product> productList) {
//            this.productList = productList;
//        }
//
//        @NonNull
//        @Override
//        public HistoryFragment.ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
//            return new HistoryFragment.ProductAdapter.ProductViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull HistoryFragment.ProductAdapter.ProductViewHolder holder, int position) {
//            HistoryFragment.Product product = productList.get(position);
//            holder.fullNameTextView.setText("Name: " + product.getFullName());
//            holder.foodQuantityTextView.setText("Quantity: " + product.getFoodQuantity());
//            holder.locationTextView.setText("Location: " + product.getLocation());
//            holder.contactTextView.setText("Contact: " + product.getContactNumber());
//
//            // Load image using Picasso with caching enabled
//            Picasso.get()
//                    .load(product.getImageUrl())
////                    .placeholder(R.drawable.placeholder_image)  // Placeholder while loading
////                    .error(R.drawable.error_image)  // Error image if loading fails
//                    .into(holder.productImageView);
//
//            // Set item click listener to open dialog
//            holder.itemView.setOnClickListener(v -> showProductDetailsDialog(product));
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
//
//    // Show Product Details in a Dialog
//    private void showProductDetailsDialog(HistoryFragment.Product product) {
////        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
////        builder.setTitle(product.getFullName());
////
////        String details = "Food Quantity: " + product.getFoodQuantity() + "\n" +
////                "Location: " + product.getLocation() + "\n" +
////                "Contact: " + product.getContactNumber();
////
////        builder.setMessage(details)
////                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
////                .show();
//    }
//
//
//}

package com.rahul.donate_a_food.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rahul.donate_a_food.R;
import com.rahul.donate_a_food.databinding.FragmentHistoryBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private FragmentHistoryBinding binding;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private Query databaseQuery;
    private static final int PAGE_SIZE = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Initialize Firebase Database and Storage references
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("uploadHistory");
        storageReference = FirebaseStorage.getInstance().getReference("productImages");

        // Initialize RecyclerView
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);

        // Set initial query
        databaseQuery = databaseReference.limitToFirst(PAGE_SIZE);

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

        return view;
    }

    private void retrieveData() {
        isLoading = true;

        // Add listener to database query to fetch data
        databaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    isLastPage = true;  // No more data to load
                    return;
                }

                // If the productList is not empty, fetch more data based on the last item
                if (!productList.isEmpty()) {
                    // Fetch more data based on the last item's fullName (pagination)
                    String lastProductFullName = productList.get(productList.size() - 1).getFullName();
                    databaseQuery = databaseReference.orderByKey().startAfter(lastProductFullName).limitToFirst(PAGE_SIZE);
                }

                // Iterate through the fetched data
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
                    .into(holder.productImageView);
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
}
