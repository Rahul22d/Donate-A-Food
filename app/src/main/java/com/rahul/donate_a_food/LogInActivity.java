package com.rahul.donate_a_food;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rahul.donate_a_food.databinding.ActivityLogInBinding;

import java.util.Arrays;

public class LogInActivity extends AppCompatActivity {
    ActivityLogInBinding binding;
    private Button loginButton;
    private TextView signUp_link, forget_link;

    private EditText emailField, passwordField;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_log_in);

        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signUpLink.setOnClickListener(V->{
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        mAuth = FirebaseAuth.getInstance();
        emailField = findViewById(R.id.email_feild);
        passwordField = findViewById(R.id.pass_feild);

        loginButton = findViewById(R.id.log_button);
        signUp_link = findViewById(R.id.signUp_link);
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(LogInActivity.this, MainActivity.class);
//                startActivity(i);
//            }
//        });
        signUp_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
        forget_link = findViewById(R.id.forget_link);
        forget_link.setOnClickListener(V->{
            Intent i = new Intent(LogInActivity.this, ForgetActivity.class);
            startActivity(i);
        });

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();


        // Handle Login Button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LogInActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase Authentication
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LogInActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, navigate to the main activity
//                                FirebaseUser user = mAuth.getCurrentUser();
                                // On successful login, store login state using SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", true); // Set login status to true
                                editor.putString("username", email); // Optionally, save the username
                                editor.apply();

                                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                intent.putExtra("username", email);
                                startActivity(intent);
                                finish(); // Close the login activity
                            } else {
                                // If sign-in fails, display a message to the user.
                                Toast.makeText(LogInActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}

//package com.rahul.donate_a_food;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.firebase.ui.auth.AuthUI;
//import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.rahul.donate_a_food.databinding.ActivityLogInBinding;
//
//import java.util.Arrays;
//import java.util.List;
//
//import com.google.firebase.appcheck.FirebaseAppCheck;
////import com.google.firebase.appcheck.PlayIntegrityAppCheckProviderFactory;
//
//public class LogInActivity extends AppCompatActivity {
//    private static final int AUTHUI_REQUEST_CODE = 1001; // Unique request code for FirebaseUI login
//
//    ActivityLogInBinding binding;
//    private Button loginButton, firebaseUILoginButton;
//    private TextView signUp_link, forget_link;
//    private EditText emailField, passwordField;
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//
//
//
//        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
//        firebaseAppCheck.installAppCheckProviderFactory(
//                PlayIntegrityAppCheckProviderFactory.getInstance());
//
//        FirebaseAuth.getInstance().getFirebaseAuthSettings().setAppVerificationDisabledForTesting(false);
//
//        binding = ActivityLogInBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        mAuth = FirebaseAuth.getInstance();
//
//        // UI References
////        emailField = findViewById(R.id.email_feild);
////        passwordField = findViewById(R.id.pass_feild);
////        loginButton = findViewById(R.id.log_button);
//////        firebaseUILoginButton = findViewById(R.id.firebase_ui_login_button); // Add a button in XML
////        signUp_link = findViewById(R.id.signUp_link);
////        forget_link = findViewById(R.id.forget_link);
//
//        // Navigate to Sign-Up Activity
////        signUp_link.setOnClickListener(v -> {
////            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
////            startActivity(intent);
////        });
////
////        // Navigate to Forget Password Activity
////        forget_link.setOnClickListener(v -> {
////            Intent i = new Intent(LogInActivity.this, ForgetActivity.class);
////            startActivity(i);
////        });
//
//        // **Email & Password Login**
////        binding.button.setOnClickListener(v -> {
////            String email = emailField.getText().toString();
////            String password = passwordField.getText().toString();
////
////            if (email.isEmpty() || password.isEmpty()) {
////                Toast.makeText(LogInActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
////                return;
////            }
////
////            mAuth.signInWithEmailAndPassword(email, password)
////                    .addOnCompleteListener(LogInActivity.this, task -> {
////                        if (task.isSuccessful()) {
////                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
////                            SharedPreferences.Editor editor = sharedPreferences.edit();
////                            editor.putBoolean("isLoggedIn", true);
////                            editor.putString("username", email);
////                            editor.apply();
////
////                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
////                            intent.putExtra("username", email);
////                            startActivity(intent);
////                            finish(); // Close LoginActivity
////                        } else {
////                            Toast.makeText(LogInActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
////                        }
////                    });
////        });
////
////        // **FirebaseUI Authentication (Google, Email, Phone)**
//        binding.button.setOnClickListener(this::handleLoginRegister);
//    }
//
//    // FirebaseUI Login Method
//    public void handleLoginRegister(View view) {
//        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
////                new AuthUI.IdpConfig.GoogleBuilder().build(),
//                new AuthUI.IdpConfig.PhoneBuilder().build()
//        );
//
//        Intent intent = AuthUI.getInstance()
//                .createSignInIntentBuilder()
//                .setAvailableProviders(providers)
//                .setTosAndPrivacyPolicyUrls("https://example.com", "https://example.com")
//                .setLogo(R.drawable.logo) // Optional: Set app logo
//                .setAlwaysShowSignInMethodScreen(true)
//                .setIsSmartLockEnabled(false) // Disable Smart Lock
//                .build();
//
//        startActivityForResult(intent, AUTHUI_REQUEST_CODE);
//    }
//
//    // Handle FirebaseUI Login Result
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == AUTHUI_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if (user != null) {
//                    String email = user.getEmail();
//                    Toast.makeText(this, "Welcome " + email, Toast.LENGTH_SHORT).show();
//
//                    // Store login state
//                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putBoolean("isLoggedIn", true);
//                    editor.putString("username", email);
//                    editor.apply();
//
//                    // Navigate to Main Activity
//                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
//                    intent.putExtra("username", email);
//                    startActivity(intent);
//                    finish();
//                }
//            } else {
//                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//}
