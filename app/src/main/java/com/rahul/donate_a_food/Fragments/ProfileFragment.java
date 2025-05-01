package com.rahul.donate_a_food.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
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
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference userRef;
    String name, email, number;
    String newPassword, confirmPassword;

    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int UCROP_REQUEST_CODE = 69;
//    private static final int GALLERY_REQUEST_CODE = 1001;
    private Uri selectedImageUri, croppedImageUri;
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

//            resetPassword();
        });
        binding.confirmPasswordBtn.setOnClickListener(v -> {
            newPassword = binding.passFeild.getText().toString().trim();
            confirmPassword = binding.confirmPassword.getText().toString().trim();
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            showReAuthDialogAndChangePassword(newPassword);
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
            Uri sourceUri = data.getData();
            Uri destinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), "cropped_image.jpg"));

            UCrop.of(sourceUri, destinationUri)
                    .withAspectRatio(1, 1) // Square crop
                    .withMaxResultSize(500, 500)
                    .start(requireContext(), this); // Use activity or fragment as needed
        }

        if (requestCode == UCROP_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            croppedImageUri = UCrop.getOutput(data);
            if (croppedImageUri != null) {
                binding.imageView2.setImageURI(croppedImageUri);
                uploadImageToFirebase(); // Pass the cropped image
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            Toast.makeText(getContext(), "Crop error: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
//            selectedImageUri = data.getData();
//            binding.imageView2.setImageURI(selectedImageUri); // Preview image
//
//            // Ask user to confirm upload
//            new AlertDialog.Builder(getContext())
//                    .setTitle("Upload Image")
//                    .setMessage("Do you want to set this image as your profile picture?")
//                    .setPositiveButton("Yes", (dialog, which) -> uploadImageToFirebase())
//                    .setNegativeButton("Cancel", null)
//                    .show();
//        }
//    }

    private void uploadImageToFirebase() {
        if (croppedImageUri != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) return;

            String userId = user.getUid();
            StorageReference imageRef = storageRef.child("profile_images/" + userId + ".jpg");

            imageRef.putFile(croppedImageUri)
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

    // this method for change email id
    private void showPasswordDialogAndUpdateEmail(String currentEmail, String newEmail, Runnable onSuccessCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Re-authenticate");

        final EditText passwordInput = new EditText(getContext());
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setHint("Enter your password");
        builder.setView(passwordInput);

        // this code for confirm password for change email id
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String password = passwordInput.getText().toString().trim();

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Password required", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.equals(currentEmail, newEmail)) {
                Toast.makeText(getContext(), "New email must be different", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(getContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }
            // Re-authenticate user using email and password
            AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, password);
            user.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("EMAIL_UPDATE", "Re-authentication successful");

                        // Send verification email to new email
                        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(newEmail).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                List<String> signInMethods = task.getResult().getSignInMethods();
                                if (signInMethods != null && signInMethods.size() > 0) {
                                    Toast.makeText(getContext(), "Email already in use", Toast.LENGTH_SHORT).show();
                                } else {
                                    FirebaseUser tempUser = FirebaseAuth.getInstance().getCurrentUser();
                                    if (tempUser != null) {
                                        tempUser.verifyBeforeUpdateEmail(newEmail)
                                                .addOnSuccessListener(unused -> {
                                                    Toast.makeText(getContext(), "Verification link sent to new email. Verify before updating.", Toast.LENGTH_LONG).show();
                                                    Log.d("EMAIL_UPDATE", "Verification link sent to: " + newEmail);
                                                    SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                                    prefs.edit()
                                                            .putBoolean("emailNeedsUpdate", true)
                                                            .putString("pendingNewEmail", newEmail)
                                                            .apply();

                                                    if(onSuccessCallback != null){
                                                        onSuccessCallback.run();
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("EMAIL_UPDATE", "Failed to send verification link", e);
                                                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                }
                            } else {
                                Toast.makeText(getContext(), "Failed to check email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("EMAIL_UPDATE", "Re-authentication failed", e);
                        Toast.makeText(getContext(), "Authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        });

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
    private void showReAuthDialogAndChangePassword(String newPassword) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Re-authenticate");

        // Set up input field
        final EditText input = new EditText(requireContext());
        input.setHint("Enter current password");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String currentPassword = input.getText().toString().trim();
            if (!currentPassword.isEmpty()) {
                changeUserPassword(currentPassword, newPassword);
            } else {
                Toast.makeText(getContext(), "Current password is required", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void changeUserPassword(String currentPassword, String newPassword) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null || user.getEmail() == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        // Re-authenticate user
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Now update the password
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                        binding.name.setVisibility(View.VISIBLE);
                                        binding.email.setVisibility(View.VISIBLE);
                                        binding.number.setVisibility(View.VISIBLE);
                                        binding.updateProfileBtn.setVisibility(View.VISIBLE);
                                        binding.changePassword.setVisibility(View.VISIBLE);

                                        binding.passFeild.setVisibility(View.INVISIBLE);
                                        binding.confirmPassword.setVisibility(View.INVISIBLE);
                                        binding.confirmPasswordBtn.setVisibility(View.INVISIBLE);
                                    } else {
                                        Toast.makeText(getContext(), "Password update failed: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getContext(), "Re-authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);

        boolean emailNeedsUpdate = prefs.getBoolean("emailNeedsUpdate", false);
        String pendingNewEmail = prefs.getString("pendingNewEmail", null);

        if (user != null && emailNeedsUpdate && pendingNewEmail != null) {
            user.reload().addOnSuccessListener(aVoid -> {
                String currentEmail = user.getEmail();
                boolean isVerified = user.isEmailVerified();

                Log.d("EMAIL_CHECK", "Current Email: " + currentEmail + ", Verified: " + isVerified);

                if (isVerified && currentEmail.equals(pendingNewEmail)) {
                    updateEmailInUserTable(currentEmail);
                    // Clear flags
                    prefs.edit().remove("emailNeedsUpdate").remove("pendingNewEmail").apply();

                    Toast.makeText(getContext(), "Email updated in database", Toast.LENGTH_SHORT).show();
                }

            }).addOnFailureListener(e -> {
                Log.e("EMAIL_CHECK", "User reload failed", e);
            });
        }
    }

    private void updateEmailInUserTable(String newEmail) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(userId)
                    .child("email");

            userRef.setValue(newEmail)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("EMAIL_UPDATE", "Email updated in Realtime DB");
                    })
                    .addOnFailureListener(e -> Log.e("EMAIL_UPDATE", "Failed to update email", e));
        }
    }

}
