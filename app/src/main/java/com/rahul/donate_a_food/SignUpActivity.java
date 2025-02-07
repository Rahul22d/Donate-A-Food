package com.rahul.donate_a_food;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rahul.donate_a_food.databinding.ActivitySignUpBinding;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private double currentLongitude, currentLatitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_sign_up);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());

        SharedPreferences sharedPreferences1 = getSharedPreferences("LocationPrefs", MODE_PRIVATE);
        String latitudeString = sharedPreferences1.getString("latitude", null);
        String longitudeString = sharedPreferences1.getString("longitude", null);
        if (latitudeString != null && longitudeString != null) {
            currentLatitude = Double.parseDouble(latitudeString);
            currentLongitude = Double.parseDouble(longitudeString);
        }

        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users"); // Reference to "users" node in Firebase

        binding.signUpButton.setOnClickListener(v -> signUpUser());
    }
    // Example: Storing user data when the user signs up
    private void signUpUser() {
        String email = binding.email.getText().toString();
        String name = binding.name.getText().toString();
        String phone = binding.number.getText().toString();
        String username = binding.userName.getText().toString();
        String password = binding.password.getText().toString();
        String confirmPassword = binding.confirmPassword.getText().toString();
        // check if all fields are filled
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

//        // Create a user in Firebase Authentication and Firestore
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnSuccessListener(authResult -> {
//                    FirebaseFirestore db = FirebaseFirestore.getInstance();
//                    Map<String, Object> user = new HashMap<>();
//                    user.put("email", email);
//                    user.put("name", name);
//                    user.put("phone", phone);
//                    user.put("username", username);
//
//                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                    db.collection("users").document(userId).set(user)
//                            .addOnSuccessListener(aVoid -> {
////                                String userId = mAuth.getCurrentUser().getUid();
////                                saveUserDetailsToDatabase(userId, name, username, email);
//                                // Proceed to the next screen (e.g., food donation page)
//                                Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show();
//                            })
//                            .addOnFailureListener(e -> {
//                                Toast.makeText(this, "Error signing up", Toast.LENGTH_SHORT).show();
//                            });
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Error signing up", Toast.LENGTH_SHORT).show();
//                });
//    }
        // Create a new user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User is signed up successfully, save user details to Firebase Realtime Database
                        String userId = mAuth.getCurrentUser().getUid();
                        saveUserDetailsToDatabase(userId, name, username, email, phone, currentLatitude, currentLongitude);

                        // Display success message
                        Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();

                        // Optionally navigate to the next activity
                        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                        startActivity(intent);
                        finish();  // Close the Sign Up Activity
                    } else {
                        // If sign-up fails, display a message to the user
                        Toast.makeText(SignUpActivity.this, "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    // Method to save user details to Firebase Realtime Database
    private void saveUserDetailsToDatabase(String userId, String name, String username, String email, String number, double latitude, double longitude) {
        // Create a User object with the provided details
        User newUser = new User(name, username, email, number, latitude, longitude);

        // Save the user data under the userId in the "users" node
        mDatabase.child(userId).setValue(newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // User data saved successfully
                        Toast.makeText(SignUpActivity.this, "User details saved", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failure
                        Toast.makeText(SignUpActivity.this, "Failed to save user details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}