package com.rahul.donate_a_food;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rahul.donate_a_food.Fragments.ProductFragment;


public class CleanupWorker extends Worker {
    private final DatabaseReference databaseReference;
    private final StorageReference storageReference;

    public CleanupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public Result doWork() {
        long currentTimeMillis = System.currentTimeMillis();

        // Query the database for expired products
        databaseReference.orderByChild("expiryTimestamp").endAt(currentTimeMillis)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            ProductFragment.Product product = productSnapshot.getValue(ProductFragment.Product.class);
                            if (product != null && product.getImageUrl() != null) {
                                String productId = productSnapshot.getKey();
                                String imageUrl = product.getImageUrl();

                                // Delete the image from Firebase Storage
                                StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                imageRef.delete().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Delete the product from the database
                                        if (productId != null) {
                                            databaseReference.child(productId).removeValue()
                                                    .addOnCompleteListener(dbTask -> {
                                                        if (!dbTask.isSuccessful()) {
                                                            Log.e("CleanupWorker", "Failed to delete product: " + productId);
                                                        }
                                                    });
                                        }
                                    } else {
                                        Log.e("CleanupWorker", "Failed to delete image: " + imageUrl);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("CleanupWorker", "Database query cancelled", error.toException());
                    }
                });

        return Result.success();
    }
}

