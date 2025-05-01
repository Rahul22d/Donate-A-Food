//package com.rahul.donate_a_food;
//
//import android.os.Bundle;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//public class ForgetActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_forget);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//}

//package com.rahul.donate_a_food;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.appcheck.FirebaseAppCheck;
//import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
//import com.google.firebase.auth.AuthCredential;
//import com.google.firebase.auth.EmailAuthProvider;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.rahul.donate_a_food.databinding.ActivityForgetBinding;
//
//import java.util.List;
//
//public class ForgetActivity extends AppCompatActivity {
//    private ActivityForgetBinding binding;
//    private EditText emailField, passwordField, confirmPasswordField;
//    private Button forgetButton, confirmPasswordBtn;
//    private FirebaseAuth mAuth;
//    private boolean isEmailVerified = false;
//    private String userEmail = "";
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_forget);
//        binding = ActivityForgetBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
//        firebaseAppCheck.installAppCheckProviderFactory(
//                PlayIntegrityAppCheckProviderFactory.getInstance()
//        );
//
//        mAuth = FirebaseAuth.getInstance();
//
////        emailField = findViewById(R.id.email_feild);
////        passwordField = findViewById(R.id.pass_feild);
////        confirmPasswordField = findViewById(R.id.confirm_password);
//        forgetButton = findViewById(R.id.forget_button);
////        confirmPasswordBtn = findViewById(R.id.confirm_password_btn);
//
////        passwordField.setVisibility(View.GONE);
////        confirmPasswordField.setVisibility(View.GONE);
//
//        forgetButton.setOnClickListener(v -> {
//            if (!isEmailVerified) {
////                verifyEmail();
//                forgotPassword();
//            }
//        });
//
//    }
//
//    private void verifyEmail() {
//        userEmail = emailField.getText().toString().trim();
//
//        if (TextUtils.isEmpty(userEmail)) {
//            Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        mAuth.fetchSignInMethodsForEmail(userEmail)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        List<String> signInMethods = task.getResult().getSignInMethods();
//                        if (signInMethods != null && !signInMethods.isEmpty()) {
//                            if (signInMethods.contains("password")) {
//                                // User has email/password account
//                                mAuth.sendPasswordResetEmail(userEmail)
//                                        .addOnCompleteListener(resetTask -> {
//                                            if (resetTask.isSuccessful()) {
//                                                Toast.makeText(this, "Reset link sent! Check your email.", Toast.LENGTH_SHORT).show();
//                                                isEmailVerified = true;
//                                            } else {
//                                                Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                            } else {
//                                // User exists but not with email/password
//                                Toast.makeText(this, "This email is registered via Google/Facebook. Can't reset password.", Toast.LENGTH_LONG).show();
//                            }
//                        } else {
//                            // No user found
//                            Toast.makeText(this, "Email not found. Please check and try again.", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(this, "Failed to check email. Try again later.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//    private void forgotPassword() {
//        String userEmail = emailField.getText().toString().trim();
//        binding.forgetButton.setVisibility(View.GONE);
//        if (TextUtils.isEmpty(userEmail)) {
//            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        // Send password reset email
//        mAuth.sendPasswordResetEmail(userEmail)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(ForgetActivity.this, "Reset email sent successfully", Toast.LENGTH_LONG).show();
////                        finish();
//                    } else {
////                        binding.forgetButton.setEnabled(true);
////                        binding.forgetButton.setEnabled(true);
//                        Toast.makeText(ForgetActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                });
//    }
//
////        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(userEmail)
////                .addOnCompleteListener(task -> {
////                    if (task.isSuccessful()) {
////                        List<String> signInMethods = task.getResult().getSignInMethods();
////
////                        if (signInMethods != null && !signInMethods.isEmpty()) {
////                            // Email is registered with password -> send reset email
////                            FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
////                                    .addOnCompleteListener(resetTask -> {
////                                        if (resetTask.isSuccessful()) {
////                                            Toast.makeText(this, "Password reset email sent. Check your inbox.", Toast.LENGTH_LONG).show();
////                                        } else {
////                                            Toast.makeText(this, "Failed to send reset email. Try again.", Toast.LENGTH_SHORT).show();
////                                        }
////                                    });
////                        } else {
////                            // Email exists but not registered with password
////                            Toast.makeText(this, "This email is registered with Google/Facebook. Password reset not available.", Toast.LENGTH_LONG).show();
////                        }
////                    } else {
////                        // Maybe email is wrong or not found
////                        Toast.makeText(this, "Email address not found. Please check again.", Toast.LENGTH_SHORT).show();
////                    }
////                });
//    }



package com.rahul.donate_a_food;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.rahul.donate_a_food.databinding.ActivityForgetBinding;

public class ForgetActivity extends AppCompatActivity {

    private ActivityForgetBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable view binding
        binding = ActivityForgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase App Check setup
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
        );

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set button click listener
        binding.forgetButton.setOnClickListener(v -> forgotPassword());
    }

    private void forgotPassword() {
        String userEmail = binding.emailFeild.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.forgetButton.setEnabled(false);

        mAuth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Reset email sent successfully", Toast.LENGTH_LONG).show();
                        goBack();
//                        finish(); // 👈 Close the activity after success
                    } else {
                        binding.forgetButton.setEnabled(true);
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void goBack() {
        AlertDialog.Builder builder  = new AlertDialog.Builder(this);
        builder.setTitle("Password Reset")
                .setMessage("A reset link has been sent to your email address. Please check your inbox.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        // Create the dialog and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}





