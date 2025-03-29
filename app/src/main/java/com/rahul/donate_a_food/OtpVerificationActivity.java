package com.rahul.donate_a_food;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText phoneInput, otpInput;
    private Button sendOtpBtn, verifyOtpBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private String verificationId; // Stores the verification ID sent by Firebase
    private PhoneAuthProvider.ForceResendingToken resendingToken; // Used to resend OTP

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        phoneInput = findViewById(R.id.phoneInput);
        otpInput = findViewById(R.id.otpInput);
        sendOtpBtn = findViewById(R.id.sendOtpBtn);
        verifyOtpBtn = findViewById(R.id.verifyOtpBtn);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneInput.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 10) {
                    phoneInput.setError("Enter a valid phone number");
                    return;
                }
                sendOtp("+91" + phoneNumber); // Change country code as needed
            }
        });

        verifyOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = otpInput.getText().toString().trim();
                if (TextUtils.isEmpty(otp)) {
                    otpInput.setError("Enter OTP");
                    return;
                }
                verifyOtp(otp);
            }
        });
    }

    // Method to send OTP
    private void sendOtp(String phoneNumber) {
        progressBar.setVisibility(View.VISIBLE);

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(120L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                progressBar.setVisibility(View.GONE);
                                signInWithCredential(credential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(OtpVerificationActivity.this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                progressBar.setVisibility(View.GONE);
                                OtpVerificationActivity.this.verificationId = verificationId;
                                resendingToken = token;
                                Toast.makeText(OtpVerificationActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Method to verify OTP
    private void verifyOtp(String otp) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithCredential(credential);
    }

    // Method to sign in using the OTP credential
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(OtpVerificationActivity.this, "OTP Verified", Toast.LENGTH_SHORT).show();
//                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                            if (user != null) {
//                                // Immediately delete the user to prevent Firebase signup
//                                user.delete().addOnCompleteListener(task1 -> {
//                                    if (task1.isSuccessful()) {
//                                        Log.d("Firebase", "User deleted successfully to prevent signup");
//                                    }
//                                });
//                            }
                            startActivity(new Intent(OtpVerificationActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(OtpVerificationActivity.this, "OTP Verification Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
