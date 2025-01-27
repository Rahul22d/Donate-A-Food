//package com.rahul.donate_a_food.Fragments;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.core.content.FileProvider;
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.material.appbar.MaterialToolbar;
//import com.google.firebase.Timestamp;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.rahul.donate_a_food.MainActivity;
//import com.rahul.donate_a_food.R;
//import com.rahul.donate_a_food.databinding.FragmentProductBinding;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
////import com.firebase.geofire.GeoHash;
//
//public class ProductFragment extends Fragment {
//    FragmentProductBinding binding;
//    private Uri foodImageUri;
//    private double latitude, longitude;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        binding = FragmentProductBinding.inflate(inflater, container, false);
//
////
//        if(getActivity() instanceof MainActivity){
//            ((MainActivity) getActivity()).hideBottomAppBar();
//            ((MainActivity) getActivity()).hideLocation();
//            latitude = ((MainActivity) getActivity()).getLat();
//            longitude = ((MainActivity) getActivity()).getLon();
//
//        }
//        binding.foodUploadBtn.setOnClickListener(V -> uploadFoodDetails());
//
//        return binding.getRoot();
//    }
//    // for image
//
//
//    // For upload food
//    private void uploadFoodDetails() {
//        if (foodImageUri == null || binding.foodName.getText().toString().isEmpty()) {
//            Toast.makeText(getActivity(), "Please provide all details", Toast.LENGTH_SHORT).show();
//            return;
//        }
////
//
//
//        // Generate geohash (using a geohashing library)
////        String geohash = GeoHash.encodeHash(latitude, longitude);
//
//        // Upload image to Firebase Storage
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference().child("food_images/" + UUID.randomUUID().toString());
//        storageRef.putFile(foodImageUri)
//                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                    String foodImageUrl = uri.toString();
//
//                    // Save food details in Firestore
//                    FirebaseFirestore db = FirebaseFirestore.getInstance();
//                    Map<String, Object> foodData = new HashMap<>();
//                    foodData.put("foodName", binding.foodName.getText().toString());
//                    foodData.put("foodDescription", binding.foodDescription.getText().toString());
//                    foodData.put("foodImageUrl", foodImageUrl);
//                    //foodData.put("geohash", geohash); // Store geohash
////                    foodData.put("latitude", latitude);
////                    foodData.put("longitude", longitude);
//                    foodData.put("quantity", Integer.parseInt(binding.foodQuantity.getText().toString()));
//                    foodData.put("timestamp", Timestamp.now());
//
//                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                    db.collection("users").document(userId).collection("user_foods").add(foodData)
//                            .addOnSuccessListener(documentReference -> {
//                                Toast.makeText(getActivity(), "Food uploaded successfully!", Toast.LENGTH_SHORT).show();
//                                binding.foodName.setText("");
//                                binding.foodDescription.setText("");
//                            })
//                            .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to upload food", Toast.LENGTH_SHORT).show());
//                }));
//    }
//
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        // Show the toolbar again when the fragment is destroyed
//        if (getActivity() instanceof MainActivity) {
//            ((MainActivity) getActivity()).showBottomAppBar();
//        }
//    }
//}

package com.rahul.donate_a_food.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rahul.donate_a_food.MainActivity;
import com.rahul.donate_a_food.R;
import com.rahul.donate_a_food.databinding.FragmentProductBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProductFragment extends Fragment {
    private FragmentProductBinding binding;
    private Uri foodImageUri = null;
    private double latitude, longitude;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Display the captured image
                    binding.imageView.setVisibility(View.VISIBLE);
                    binding.imageView.setImageURI(foodImageUri);
                }
            });

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    // Get the image URI from the gallery
                    foodImageUri = result.getData().getData();
                    binding.imageView.setVisibility(View.VISIBLE);
                    binding.imageView.setImageURI(foodImageUri);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductBinding.inflate(inflater, container, false);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideBottomAppBar();
            ((MainActivity) getActivity()).hideLocation();
            latitude = ((MainActivity) getActivity()).getLat();
            longitude = ((MainActivity) getActivity()).getLon();
        }
        binding.foodName.setText(latitude +""+ longitude);
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference("product_images");

        binding.takeImageButton.setOnClickListener(v -> showImageSourceDialog());
        binding.foodUploadBtn.setOnClickListener(v -> {

            String foodName = binding.foodName.getText().toString().trim();
            String foodDescription = binding.foodDescription.getText().toString().trim();
            int foodQuantity = Integer.parseInt(binding.foodQuantity.getText().toString());
            String number = "9998428534";


            if(TextUtils.isEmpty(foodName) || TextUtils.isEmpty(number) || foodQuantity == 0){
                Toast.makeText(getActivity(), "Please provide all details", Toast.LENGTH_SHORT).show();
                return;
            }
            if (foodImageUri != null){
                uploadImageToFirebase(foodImageUri, foodName, number, foodDescription, foodQuantity, latitude, longitude);
//                resetFields();
            } else {
//                saveProductDataToDatabase(foodName, number, foodDescription, foodQuantity, null, latitude, longitude);
//                resetFields();
                Toast.makeText(getActivity(), "Image upload failed so please try again", Toast.LENGTH_SHORT).show();
            }

        });

        return binding.getRoot();
    }

    private void showImageSourceDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Image Source")
                .setItems(new String[]{"Camera", "Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        captureImageFromCamera();
                    } else if (which == 1) {
                        pickImageFromGallery();
                    }
                })
                .show();
    }

    private void captureImageFromCamera() {
        // Create an intent to capture an image
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            // Create a temporary file for the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getContext(), "Error creating image file", Toast.LENGTH_SHORT).show();
            }

            // Proceed only if the file was successfully created
            if (photoFile != null) {
                foodImageUri = FileProvider.getUriForFile(requireContext(),
                        "com.rahul.donate_a_food.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, foodImageUri);
                cameraLauncher.launch(takePictureIntent);
            }
        }
    }

    private void pickImageFromGallery() {
        // Create an intent to pick an image from the gallery
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pickPhotoIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "FOOD_IMAGE_" + UUID.randomUUID().toString();
        File storageDir = requireContext().getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }



    //  Method to upload the image to Firebase Storage
    private void uploadImageToFirebase(Uri imageUri, String foodName, String contactNumber, String foodDescription,
                                       int foodQuantity, double latitude, double longitude) {
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().getContentResolver(), imageUri));
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
            }

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byte[] compressedImageBytes = byteArrayOutputStream.toByteArray();

                StorageReference imageRef = storageReference.child(System.currentTimeMillis() + ".jpg");
                imageRef.putBytes(compressedImageBytes).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            saveProductDataToDatabase(foodName, contactNumber, foodDescription, foodQuantity, imageUrl, latitude, longitude);
//                            Toast.makeText(requireContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                            resetFields();
                        });
                    } else {
                        Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error compressing image", Toast.LENGTH_SHORT).show();
        }
    }

//    private void saveProductDataToDatabase(String foodName, String contactNumber, String foodDescription,
//                                           int foodQuantity, String imageUrl, double latitude, double longitude) {
//        Product product = new Product(foodName, contactNumber, foodDescription, foodQuantity, imageUrl, latitude, longitude);
//        String productId = databaseReference.push().getKey();
//        if (productId != null) {
//            databaseReference.child(productId).setValue(product).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    resetFields();
//                    Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(requireContext(), "Failed to add product", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
private void saveProductDataToDatabase(String foodName, String contactNumber, String foodDescription,
                                       int foodQuantity, String imageUrl, double latitude, double longitude) {
    long currentTimeMillis = System.currentTimeMillis(); // Current time
    long expiryTimeMillis = currentTimeMillis + (4 * 60 * 60 * 1000); // 4 hours in milliseconds

    Product product = new Product(foodName, contactNumber, foodDescription, foodQuantity, imageUrl, latitude, longitude, expiryTimeMillis);
    String productId = databaseReference.push().getKey();
    if (productId != null) {
        databaseReference.child(productId).setValue(product).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Failed to add product", Toast.LENGTH_SHORT).show();
            }
        });
    }
}



    private void resetFields() {
        binding.foodName.setText("");
        binding.foodDescription.setText("");
        binding.foodQuantity.setText("");
        binding.imageView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Show the toolbar again when the fragment is destroyed
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showBottomAppBar();
        }
    }

    // Product class to represent the product data
    public static class Product {
        private String foodName;
        private String contactNumber;
        private int foodQuantity;
        private String foodDescription;
        private String imageUrl;
        private double latitude;
        private double longitude;
        private long expiryTimestamp;

        // No-argument constructor required for Firebase
        public Product() {
        }

        Product(String foodName, String contactNumber, String foodDescription,/*String location,*/ int foodQuantity
                , String imageUrl, double latitude, double longitude, long expiryTimestamp) {
            this.foodName = foodName;
            this.contactNumber = contactNumber;
//            this.location = location;
            this.foodDescription = foodDescription;
            this.foodQuantity = foodQuantity;
            this.imageUrl = imageUrl;
            this.latitude = latitude;
            this.longitude = longitude;
            this.expiryTimestamp = expiryTimestamp;
        }
        // Getters and setters for all fields
        public String getFoodName() {
            return foodName;
        }

        public void setFoodName(String foodName) {
            this.foodName = foodName;
        }

        public String getContactNumber() {
            return contactNumber;
        }

        public void setContactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
        }

        public String getFoodDescription() {
            return foodDescription;
        }

        public void setFoodDescription(String foodDescription) {
            this.foodDescription = foodDescription;
        }

        public int getFoodQuantity() {
            return foodQuantity;
        }

        public void setFoodQuantity(int foodQuantity) {
            this.foodQuantity = foodQuantity;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
        public long getExpiryTimestamp() {
            return expiryTimestamp;
        }

        public void setExpiryTimestamp(long expiryTimestamp) {
            this.expiryTimestamp = expiryTimestamp;
        }
    }
}
