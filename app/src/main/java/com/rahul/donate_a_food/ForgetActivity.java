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

package com.rahul.donate_a_food;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ForgetActivity extends AppCompatActivity {

    private EditText emailField, passwordField, confirmPasswordField;
    private Button forgetButton, confirmPasswordBtn;
    private FirebaseAuth mAuth;
    private boolean isEmailVerified = false;
    private String userEmail = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.email_feild);
        passwordField = findViewById(R.id.pass_feild);
        confirmPasswordField = findViewById(R.id.confirm_password);
        forgetButton = findViewById(R.id.foget_button);
        confirmPasswordBtn = findViewById(R.id.confirm_password_btn);

        passwordField.setVisibility(View.GONE);
        confirmPasswordField.setVisibility(View.GONE);

        forgetButton.setOnClickListener(v -> {
            if (!isEmailVerified) {
                verifyEmail();
            }
        });


    }

    private void verifyEmail() {
        userEmail = emailField.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email exists in Firebase Authentication
        mAuth.fetchSignInMethodsForEmail(userEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> signInMethods = task.getResult().getSignInMethods();

                        if (signInMethods != null && !signInMethods.isEmpty()) {
                            // Email exists, send reset email
                            mAuth.sendPasswordResetEmail(userEmail)
                                    .addOnCompleteListener(resetTask -> {
                                        if (resetTask.isSuccessful()) {
                                            Toast.makeText(this, "Reset link sent! Check your email.", Toast.LENGTH_SHORT).show();

//                                            // Switch to password reset mode
//                                            emailField.setVisibility(View.INVISIBLE);
//                                            forgetButton.setVisibility(View.INVISIBLE);
//                                            passwordField.setVisibility(View.VISIBLE);
//                                            confirmPasswordField.setVisibility(View.VISIBLE);
//                                            confirmPasswordBtn.setVisibility(View.VISIBLE);
//
//                                            // Move bottom_layout below confirm_password_btn
//                                            LinearLayout bottomLayout = findViewById(R.id.bottom_layout);
//                                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
//                                            params.addRule(RelativeLayout.BELOW, R.id.confirm_password_btn);
//                                            bottomLayout.setLayoutParams(params);

                                            isEmailVerified = true;
                                        } else {
                                            Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Email not found in Firebase Authentication
                            Toast.makeText(this, "Email not registered. Please check again.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error checking email. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


//    private void verifyEmail() {
//        userEmail = emailField.getText().toString().trim();
//        if (TextUtils.isEmpty(userEmail)) {
//            Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Send password reset email
//        mAuth.sendPasswordResetEmail(userEmail)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(this, "Reset link sent! Check your email.", Toast.LENGTH_SHORT).show();
//
//
//
//
//                        isEmailVerified = true;
//                    } else {
//                        Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }





}
