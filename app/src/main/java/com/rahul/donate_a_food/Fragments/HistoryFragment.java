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
    private List<ProductHistory> productList;
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
                    String date = snapshot.child("date").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    if (fullName != null && foodQuantity != null && imageUrl != null && date != null) {
                        productList.add(new ProductHistory(fullName, foodQuantity, imageUrl, date));
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
    public static class ProductHistory {
        private String fullName;
        private int foodQuantity;
        private String imageUrl;
        private String date;

        public ProductHistory(String fullName, int foodQuantity, String imageUrl, String date) {
            this.fullName = fullName;
            this.foodQuantity = foodQuantity;
            this.imageUrl = imageUrl;
            this.date = date;
        }

        public String getFullName() {
            return fullName;
        }

        public int getFoodQuantity() {
            return foodQuantity;
        }

        public String getdate() {
            return date;
        }

        public String getImageUrl() {
            return imageUrl;
        }

//        public String getContactNumber() {
//            return contactNumber;
//        }
    }

    // Adapter for RecyclerView
    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        private List<ProductHistory> productList;

        public ProductAdapter(List<ProductHistory> productList) {
            this.productList = productList;
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_history, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            ProductHistory product = productList.get(position);
            holder.fullNameTextView.setText(product.getFullName());
            holder.foodQuantityTextView.setText("Quantity: " + product.getFoodQuantity());
            holder.locationTextView.setText("Date: " + product.getdate());
//            holder.contactTextView.setText("Contact: " + product.getContactNumber());

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
//            public TextView contactTextView;

            public ProductViewHolder(View itemView) {
                super(itemView);
                fullNameTextView = itemView.findViewById(R.id.foodNameH);
                foodQuantityTextView = itemView.findViewById(R.id.foodQuantityH);
                locationTextView = itemView.findViewById(R.id.foodDate);
                productImageView = itemView.findViewById(R.id.foodImageH);
//                contactTextView = itemView.findViewById(R.id.contactTextView);
            }
        }
    }
}
