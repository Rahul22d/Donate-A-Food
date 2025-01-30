package com.rahul.donate_a_food.Fragments;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.UUID;

public class ProductFragment extends Fragment {
    private FragmentProductBinding binding;
    private Uri foodImageUri = null;
    private double latitude, longitude;
    private DatabaseReference databaseReference;
    private DatabaseReference userdatabaseReference;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private Spinner spinner;

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
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference("product_images");
        userdatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // for spinner food type select
        spinner = binding.foodCategory;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity()
                ,R.array.food_type
                , android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        binding.takeImageButton.setOnClickListener(v -> showImageSourceDialog());
        binding.foodUploadBtn.setOnClickListener(v -> {

            String foodName = binding.foodName.getText().toString().trim();
            String foodDescription = binding.foodDescription.getText().toString().trim();
            int foodQuantity = Integer.parseInt(binding.foodQuantity.getText().toString());

            String foodType = spinner.getSelectedItem().toString();



            if(TextUtils.isEmpty(foodName)  || foodQuantity == 0){
                Toast.makeText(getActivity(), "Please provide all details", Toast.LENGTH_SHORT).show();
                return;
            }
            if (foodImageUri != null){
                uploadImageToFirebase(foodImageUri, foodName,  foodDescription, foodQuantity, latitude, longitude, foodType);
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
    private void uploadImageToFirebase(Uri imageUri, String foodName, String foodDescription,
                                       int foodQuantity, double latitude, double longitude, String foodType) {
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
                            saveProductDataToDatabase(foodName,  foodDescription, foodQuantity, imageUrl, latitude, longitude, foodType);
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

    private void saveProductDataToDatabase(String foodName,  String foodDescription,
                                           int foodQuantity, String imageUrl, double latitude, double longitude, String foodType) {
        long currentTimeMillis = System.currentTimeMillis(); // Current time
        long expiryTimeMillis = currentTimeMillis + (4 * 60 * 60 * 1000); // 4 hours in milliseconds

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        // Fetch donor details from Firebase Database
        userdatabaseReference.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String donorName = task.getResult().child("name").getValue(String.class);
                String donorNumber = task.getResult().child("number").getValue(String.class);

                if (donorName == null || donorNumber == null) {
                    Toast.makeText(getActivity(), "Failed to fetch donor details", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create Product object with new donor details
                Product product = new Product(foodName,donorNumber, foodDescription, foodQuantity, imageUrl, latitude, longitude, expiryTimeMillis, foodType, donorName);

                // Save product to Firebase
                String productId = databaseReference.push().getKey();
                if (productId != null) {
                    databaseReference.child(productId).setValue(product).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(getActivity(), "Product added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to add product", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Save to user's upload history
                    HistoryProduct historyProduct = new HistoryProduct(foodName, foodQuantity, imageUrl, new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                    userdatabaseReference.child(userId).child("uploadHistory").child(productId).setValue(historyProduct);
                }
            } else {
                Toast.makeText(getActivity(), "Failed to retrieve donor details", Toast.LENGTH_SHORT).show();
            }
        });
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
        private String foodType;
        private String userName;

        // No-argument constructor required for Firebase
        public Product() {
        }

        Product(String foodName, String contactNumber, String foodDescription,/*String location,*/ int foodQuantity
                , String imageUrl, double latitude, double longitude, long expiryTimestamp, String foodType, String userName) {
            this.foodName = foodName;
            this.contactNumber = contactNumber;
//            this.location = location;
            this.foodDescription = foodDescription;
            this.foodQuantity = foodQuantity;
            this.imageUrl = imageUrl;
            this.latitude = latitude;
            this.longitude = longitude;
            this.expiryTimestamp = expiryTimestamp;
            this.foodType = foodType;
            this.userName = userName;
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
        public void setFoodType(){this.foodType = foodType;}
        public String getFoodType() {return foodType;}

        public void setUserName() {this.userName = userName;}
        public String getUserName() {return userName;}
    }
    // for track user history
    class HistoryProduct{
        private String foodName;
        private int foodQuantity;
        private String imageUrl;
        private String date;

        public HistoryProduct(String foodName, int foodQuantity, String imageUrl, String date) {
            this.foodName = foodName;
            this.foodQuantity = foodQuantity;
            this.imageUrl = imageUrl;
            this.date = date;
        }
        public String getFoodName() {
            return foodName;
        }
        public int getFoodQuantity() {
            return foodQuantity;
        }

        public String getImageUrl() {
            return imageUrl;
        }
        public String getDate() {
            return date;
        }

    }
}
