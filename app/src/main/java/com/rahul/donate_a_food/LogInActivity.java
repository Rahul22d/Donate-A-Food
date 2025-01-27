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