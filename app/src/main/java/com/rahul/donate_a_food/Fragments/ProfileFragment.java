//package com.rahul.donate_a_food.Fragments;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.fragment.app.Fragment;
//
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.google.firebase.auth.AuthCredential;
//import com.google.firebase.auth.EmailAuthProvider;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.rahul.donate_a_food.R;
//import com.rahul.donate_a_food.databinding.FragmentHelpBinding;
//import com.rahul.donate_a_food.databinding.FragmentProductBinding;
//import com.rahul.donate_a_food.databinding.FragmentProfileBinding;
//
//import java.util.List;
//
//
//public class ProfileFragment extends Fragment {
//
//    FragmentProfileBinding binding;
//    FirebaseAuth mAuth;
//    DatabaseReference userRef;
//    String name, email, number;
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
////        return inflater.inflate(R.layout.fragment_profile, container, false);
//        binding = FragmentProfileBinding.inflate(inflater, container, false);
//        mAuth = FirebaseAuth.getInstance();
//        userRef = FirebaseDatabase.getInstance().getReference("users");
//
//        showProfile();
//        setupEditTextListeners();
//        binding.changePassword.setOnClickListener(v -> {
//            // Switch to password reset mode
//            binding.name.setVisibility(View.INVISIBLE);
//            binding.email.setVisibility(View.INVISIBLE);
//            binding.number.setVisibility(View.INVISIBLE);
//            binding.updateProfileBtn.setVisibility(View.INVISIBLE);
//            binding.changePassword.setVisibility(View.INVISIBLE);
//
//            binding.passFeild.setVisibility(View.VISIBLE);;
//            binding.confirmPassword.setVisibility(View.VISIBLE);
//            binding.confirmPasswordBtn.setVisibility(View.VISIBLE);
//
//                        // Move bottom_layout below confirm_password_btn
////                        LinearLayout bottomLayout = findViewById(R.id.bottom_layout);
////                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
////                        params.addRule(RelativeLayout.BELOW, R.id.confirm_password_btn);
////                        bottomLayout.setLayoutParams(params);//
//            resetPassword();
//        });
//
//        return binding.getRoot();
//
//    }
//
//    public void showProfile() {
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            String userId = user.getUid();
//            Log.d("ProfileFragment", "Fetching data for user ID: " + userId);
//
//            userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.exists()) {
//                        Log.d("ProfileFragment", "Snapshot exists!");
//
//                        // Retrieve values correctly
//                        name = snapshot.child("name").getValue(String.class);
//                        email = snapshot.child("email").getValue(String.class);
//                       // String number = snapshot.child("number").getValue(String.class); // Now directly as a String
//
//                        // Handle number safely (Firebase may store it as Long)
//                        Long numberLong = snapshot.child("number").getValue(Long.class);
//                        number = (numberLong != null) ? String.valueOf(numberLong) : "N/A";
//                        // Debugging logs
//                        Log.d("ProfileFragment", "Name: " + name);
//                        Log.d("ProfileFragment", "Username: " + email);
//                        Log.d("ProfileFragment", "Number: " + number);
//
//                        // Set data to TextViews
//                        binding.name.setText(name != null ? name : "N/A");
//                        binding.number.setText(number != null ? number : "N/A");
//                        binding.email.setText(email != null ? email : "N/A");
//
//                        // Now check if values are unchanged
//                        checkIfProfileChanged();
//
//                    } else {
//                        Log.e("ProfileFragment", "User data not found!");
//                        Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Log.e("ProfileFragment", "Database error: " + error.getMessage());
//                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//        } else {
//            Log.e("ProfileFragment", "User not logged in");
//            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
//        }
//    }
//
////    private void checkIfProfileChanged(String originalName, String originalNumber, String originalEmail) {
////        String newName = binding.name.getText().toString();
////        String newNumber = binding.number.getText().toString();
////        String newEmail = binding.email.getText().toString();
////
////        if (newName.equals(originalName) && newEmail.equals(originalEmail) && newNumber.equals(originalNumber)) {
////            binding.updateProfileBtn.setEnabled(false); // Disable button if nothing changed
////        } else {
////            binding.updateProfileBtn.setEnabled(true); // Enable button if values changed
////        }
////    }
//private void checkIfProfileChanged() {
//    String originalName = binding.name.getText().toString();
//    String originalNumber = binding.number.getText().toString();
//    String originalEmail = binding.email.getText().toString();
//
////    String newName = binding.name.getText().toString();
////    String newNumber = binding.number.getText().toString();
////    String newEmail = binding.email.getText().toString();
//
//    // Check if the values have changed
//    if (name.equals(originalName) && email.equals(originalEmail) && number.equals(originalNumber)) {
//        binding.updateProfileBtn.setVisibility(View.GONE); // Disable the button if values are the same
//        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.changePassword.getLayoutParams();
//        params.topMargin = 50; // Set top margin in pixels
//        binding.changePassword.setLayoutParams(params);
//    } else {
//        binding.updateProfileBtn.setVisibility(View.VISIBLE); // Enable the button if any value has changed
//        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.changePassword.getLayoutParams();
//        params.topMargin = 250; // Set top margin in pixels
//        binding.changePassword.setLayoutParams(params);
//    }
//}
//
//
//    private void setupEditTextListeners() {
//        // Listen for changes in EditText fields
//        binding.name.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                checkIfProfileChanged(); // Check if the profile values changed
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {}
//        });
//
//        binding.number.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                checkIfProfileChanged(); // Check if the profile values changed
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {}
//        });
//
//        binding.email.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                checkIfProfileChanged(); // Check if the profile values changed
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {}
//        });
//    }
//
//    // this code for change password not forget password
//    private void resetPassword() {
////        String email = emailField.getText().toString().trim();
//        String newPassword = binding.passFeild.getText().toString().trim();
//        String confirmPassword = binding.confirmPassword.getText().toString().trim();
//
//
//
//        if (!newPassword.equals(confirmPassword)) {
//            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//
//        // Sign in anonymously (only for password reset)
//        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
//            if (task.isSuccessful() && task.getResult() != null) {
//                List<String> signInMethods = task.getResult().getSignInMethods();
//                if (signInMethods != null && ((List<?>) signInMethods).contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
//
//                    // Get user reference
//                    FirebaseUser user = auth.getCurrentUser();
//                    if (user != null) {
//                        // Re-authenticate using an email link or token
//                        AuthCredential credential = EmailAuthProvider.getCredential(email, newPassword);
//                        user.reauthenticate(credential).addOnCompleteListener(authTask -> {
//                            if (authTask.isSuccessful()) {
//                                // Update password
//                                user.updatePassword(newPassword)
//                                        .addOnCompleteListener(updateTask -> {
//                                            if (updateTask.isSuccessful()) {
//                                                Toast.makeText(getContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show();
////                                                finish();
//                                            } else {
//                                                Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                            } else {
//                                Toast.makeText(getContext(), "Re-authentication failed. Please log in again.", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    } else {
//                        Toast.makeText(getContext(), "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show();
//                    }
//
//                } else {
//                    Toast.makeText(getContext(), "User not found. Please check the email.", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(getContext(), "Error retrieving user data.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        binding.name.setVisibility(View.VISIBLE);
//        binding.email.setVisibility(View.VISIBLE);
//        binding.number.setVisibility(View.VISIBLE);
//        binding.updateProfileBtn.setVisibility(View.VISIBLE);
//        binding.changePassword.setVisibility(View.VISIBLE);
//
//        binding.passFeild.setVisibility(View.INVISIBLE);;
//        binding.confirmPassword.setVisibility(View.INVISIBLE);
//        binding.confirmPasswordBtn.setVisibility(View.INVISIBLE);
//    }
//}

package com.rahul.donate_a_food.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rahul.donate_a_food.ImageLoaderTask;
import com.rahul.donate_a_food.R;
import com.rahul.donate_a_food.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference userRef;
    String name, email, number;

    private static final int GALLERY_REQUEST_CODE = 1001;
    private Uri selectedImageUri;
    private FirebaseStorage storage;
    private StorageReference storageRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        showProfile();
        loadProfileImage();
        setupEditTextListeners();
        binding.changePassword.setOnClickListener(v -> {
            // Switch to password reset mode
            binding.name.setVisibility(View.INVISIBLE);
            binding.email.setVisibility(View.INVISIBLE);
            binding.number.setVisibility(View.INVISIBLE);
            binding.updateProfileBtn.setVisibility(View.INVISIBLE);
            binding.changePassword.setVisibility(View.INVISIBLE);

            binding.passFeild.setVisibility(View.VISIBLE);
            binding.confirmPassword.setVisibility(View.VISIBLE);
            binding.confirmPasswordBtn.setVisibility(View.VISIBLE);

            resetPassword();
        });

        // When the update button is clicked
        binding.updateProfileBtn.setOnClickListener(v -> updateUserProfile());
        binding.ivEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        });


        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            binding.imageView2.setImageURI(selectedImageUri); // Preview image

            // Ask user to confirm upload
            new AlertDialog.Builder(getContext())
                    .setTitle("Upload Image")
                    .setMessage("Do you want to set this image as your profile picture?")
                    .setPositiveButton("Yes", (dialog, which) -> uploadImageToFirebase())
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void uploadImageToFirebase() {
        if (selectedImageUri != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) return;

            String userId = user.getUid();
            StorageReference imageRef = storageRef.child("profile_images/" + userId + ".jpg");

            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();

                        // Only update imageUrl field, keep others intact
                        DatabaseReference userRef = FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(userId);

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("imageUrl", downloadUrl);

                        userRef.updateChildren(updates)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(getContext(), "Profile image updated!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(), "Failed to update image URL", Toast.LENGTH_SHORT).show());

                    })).addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void loadProfileImage() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("imageUrl")) {
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    if (imageUrl != null && !imageUrl.isEmpty()) {

                        // Load image manually without Glide (using AsyncTask)
                        Toast.makeText(getContext(), "imageUrl: " + imageUrl, Toast.LENGTH_SHORT).show();
//                        new ImageLoaderTask(binding.imageView2).execute(imageUrl);

                        // load imgae using picasso
                        Picasso.get().load(imageUrl).into(binding.imageView2);
                    } else {
                        binding.imageView2.setImageResource(R.drawable.logo); // default image
                    }
                } else {
                    binding.imageView2.setImageResource(R.drawable.logo); // default image
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.imageView2.setImageResource(R.drawable.logo); // default image on error
            }
        });
    }



    private void showPasswordDialogAndUpdateEmail(String currentEmail, String newEmail, Runnable onSuccessCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Re-authenticate");

        final EditText passwordInput = new EditText(getContext());
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setHint("Enter your password");
        builder.setView(passwordInput);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String password = passwordInput.getText().toString().trim();

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Password required", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && !TextUtils.equals(currentEmail, newEmail)) {
                if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    Toast.makeText(getContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("EMAIL_UPDATE", "Current user email: " + user.getEmail());

                AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, password);

                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("EMAIL_UPDATE", "Re-authentication successful");

                                user.updateEmail(newEmail)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                Toast.makeText(getContext(), "Email updated successfully", Toast.LENGTH_SHORT).show();
                                                if (onSuccessCallback != null)
                                                    onSuccessCallback.run();
                                            } else {
                                                Exception e = updateTask.getException();
                                                Log.e("EMAIL_UPDATE", "Email update failed", e);
                                                Toast.makeText(getContext(), "Failed to update email: " +
                                                        (e != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                                            }
                                        });

                            } else {
                                Exception e = task.getException();
                                Log.e("EMAIL_UPDATE", "Re-authentication failed", e);
                                Toast.makeText(getContext(), "Authentication failed: " +
                                        (e != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                            }
                        });
            }});


            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void updateUserProfile() {
        String name1 = binding.name.getText().toString().trim();
        String number1 = binding.number.getText().toString().trim();
        String email1 = binding.email.getText().toString().trim();

        if (TextUtils.isEmpty(name1) || TextUtils.isEmpty(number1) || TextUtils.isEmpty(email1)) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // If email changed, ask for password, and only then update the profile
            if (!email.equals(email1)) {
                showPasswordDialogAndUpdateEmail(email, email1, () -> {
                    // Callback runs only if email was updated successfully
                    updateRemainingProfile(userId, name1, number1, email1);
                });
            } else {
                // If email hasn't changed, update directly
                updateRemainingProfile(userId, name1, number1, email1);
            }
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRemainingProfile(String userId, String name1, String number1, String email1) {
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", name1);
        userUpdates.put("number", number1);
        userUpdates.put("email", email1);

        userRef.child(userId).updateChildren(userUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        binding.updateProfileBtn.setVisibility(View.INVISIBLE);
                        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.changePassword.getLayoutParams();
                        params.topMargin = 50;
                        binding.changePassword.setLayoutParams(params);
                        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

public void showProfile() {
    FirebaseUser user = mAuth.getCurrentUser();
    if (user != null) {
        String userId = user.getUid();
        Log.d("ProfileFragment", "Fetching data for user ID: " + userId);

        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("ProfileFragment", "Snapshot exists!");

                    // Retrieve values correctly
                    name = snapshot.child("name").getValue(String.class);
                    email = snapshot.child("email").getValue(String.class);

                    // Safely handle number as a String first
                    String numberString = snapshot.child("number").getValue(String.class);
                    // If the number is stored as a string, try to parse it into a Long
                    number = numberString != null ? numberString : "N/A";

                    // Debugging logs
                    Log.d("ProfileFragment", "Name: " + name);
                    Log.d("ProfileFragment", "Username: " + email);
                    Log.d("ProfileFragment", "Number: " + number);

                    // Set data to TextViews
                    binding.name.setText(name != null ? name : "N/A");
                    binding.number.setText(number != null ? number : "N/A");
                    binding.email.setText(email != null ? email : "N/A");

                    // Now check if values are unchanged
                    checkIfProfileChanged();
                } else {
                    Log.e("ProfileFragment", "User data not found!");
                    Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileFragment", "Database error: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

    } else {
        Log.e("ProfileFragment", "User not logged in");
        Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
    }
}


    private void checkIfProfileChanged() {
        String originalName = binding.name.getText().toString();
        String originalNumber = binding.number.getText().toString();
        String originalEmail = binding.email.getText().toString();

        // Check if the values have changed
        if (TextUtils.equals(name, originalName) && TextUtils.equals(email, originalEmail) && TextUtils.equals(number, originalNumber)) {
            binding.updateProfileBtn.setVisibility(View.INVISIBLE); // Disable button if nothing changed
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.changePassword.getLayoutParams();
            params.topMargin = 50; // Set top margin in pixels
            binding.changePassword.setLayoutParams(params);
        } else {
            binding.updateProfileBtn.setVisibility(View.VISIBLE); // Enable button if values changed
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.changePassword.getLayoutParams();
            params.topMargin = 250; // Set top margin in pixels
            binding.changePassword.setLayoutParams(params);
        }
    }

    private void setupEditTextListeners() {
        // Listen for changes in EditText fields
        binding.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                checkIfProfileChanged(); // Check if the profile values changed
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                checkIfProfileChanged(); // Check if the profile values changed
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                checkIfProfileChanged(); // Check if the profile values changed
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void resetPassword() {
        String newPassword = binding.passFeild.getText().toString().trim();
        String confirmPassword = binding.confirmPassword.getText().toString().trim();

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Sign in anonymously (only for password reset)
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> signInMethods = task.getResult().getSignInMethods();
                if (signInMethods != null && ((List<?>) signInMethods).contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {

                    // Get user reference
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        // Re-authenticate using an email link or token
                        AuthCredential credential = EmailAuthProvider.getCredential(email, newPassword);
                        user.reauthenticate(credential).addOnCompleteListener(authTask -> {
                            if (authTask.isSuccessful()) {
                                // Update password
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                Toast.makeText(getContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(getContext(), "Re-authentication failed. Please log in again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "User not found. Please check the email.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error retrieving user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Reset UI to default state
        binding.name.setVisibility(View.VISIBLE);
        binding.email.setVisibility(View.VISIBLE);
        binding.number.setVisibility(View.VISIBLE);
        binding.updateProfileBtn.setVisibility(View.VISIBLE);
        binding.changePassword.setVisibility(View.VISIBLE);

        binding.passFeild.setVisibility(View.INVISIBLE);
        binding.confirmPassword.setVisibility(View.INVISIBLE);
        binding.confirmPasswordBtn.setVisibility(View.INVISIBLE);
    }
}
