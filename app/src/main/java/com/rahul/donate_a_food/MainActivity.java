package com.rahul.donate_a_food;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rahul.donate_a_food.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationPass {
    private static final int NOTIFICATION_PERMISSION_CODE = 1;
    private ActivityMainBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private NavigationView navigationView;
    private String username;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private double latitude, longitude;
    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 100;
    private Uri foodImage;
    private LocationCallback locationCallback;
    private Double currentLatitude, currentLongitude;

    private LocationViewModel locationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{"android.permission.RECEIVE_SMS", "android.permission.READ_SMS"}, 101);
        }


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);

        // load profile image
        loadProfileImage();
        drawer = binding.drawerLayout;
        navigationView = binding.navigationView;
        db = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String email = intent.getStringExtra("username");
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        username = sharedPreferences.getString("username", email);

        if (!isLoggedIn) {
            navigationView.getMenu().findItem(R.id.action_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.action_logIn).setVisible(true);
        } else {
            TextView useremail = navigationView.getHeaderView(0).findViewById(R.id.useremail);
            useremail.setText(username);
            navigationView.getMenu().findItem(R.id.action_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.action_logIn).setVisible(false);
            // for notification
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.d("FCMService", "Fetching FCM token failed", task.getException());
                            return;
                        }

                        // Get token
                        String token = task.getResult();
                        Log.d("FCMService", "FCM Token: " + token);

                        // Save token to Firestore
//                        MyFirebaseService myFirebaseService = new MyFirebaseService();
//                        myFirebaseService.saveTokenToFirestore(token);
                    });

        }


        currentLatitude = intent.getDoubleExtra("latitude",0.0);
        currentLongitude = intent.getDoubleExtra("longitude", 0.0);
//        setLocation(currentLatitude, currentLongitude);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        locationViewModel.setLocation(currentLatitude, currentLongitude);
        locationViewModel.getLocation().observe(this, location -> {
           currentLatitude = location[0];
           currentLongitude = location[1];
           saveLocationToFirestore(currentLatitude, currentLongitude);
           Toast.makeText(this, "Location: "+currentLatitude+" ,"+currentLongitude, Toast.LENGTH_SHORT).show();
        });
        getAddess();


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.action_home, R.id.action_history, R.id.action_help, R.id.action_feedback, R.id.action_share)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        setupBottomAppBarNavigation(navController);

        binding.floatingActionButton.setOnClickListener(V -> {
            if (isLoggedIn) {
                navController.navigate(R.id.nav_add);
            } else {
                startActivity(new Intent(MainActivity.this, LogInActivity.class));
                finish();
            }
        });

        binding.locationIcon.setOnClickListener(V -> navController.navigate(R.id.nav_location));

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        if (!isConnected()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }


    // get address
    public void getAddess() {
        Toast.makeText(this, "getAddess", Toast.LENGTH_SHORT).show();
        if(currentLatitude != null && currentLongitude != null) {
            getAddressFromCoordinates(currentLatitude, currentLongitude);
        }
    }

    // for save location on data base
    private void saveLocationToFirestore(double latitude, double longitude) {
        String userId = mAuth.getUid();
        Map<String, Object> map = new HashMap<>();
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        if(userId != null){
                    db.child(userId) // Replace with actual user ID logic
                    .updateChildren(map)
                    .addOnSuccessListener(e ->  Log.e("SplashActivity", "save location: " ))
                    .addOnFailureListener(e -> {
                        Log.e("SplashActivity", "Failed to save location: " );

                    });
        }
    }



    private void setupBottomAppBarNavigation(NavController navController) {
        BottomNavigationView bottomNavigationView = binding.bottomNavigationView;
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_order) {
                navController.navigate(R.id.nav_bookedFood);
                return true;
            } else if (item.getItemId() == R.id.nav_account) {
                navController.navigate(R.id.nav_account);
                return true;
            }
            return false;
        });
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        drawer.closeDrawers();

        if (item.getItemId() == R.id.action_logIn) {
            startActivity(new Intent(MainActivity.this, LogInActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            logOut(username);
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        } else if (item.getItemId() == R.id.action_home) {
            navController.navigate(R.id.action_home);
            return true;
        } else if (item.getItemId() == R.id.action_history) {
            navController.navigate(R.id.action_history);
            return true;
        } else if (item.getItemId() == R.id.action_help) {
            navController.navigate(R.id.action_help);
            return true;
        } else if (item.getItemId() == R.id.action_feedback) {
            navController.navigate(R.id.action_feedback);
            return true;
        } else if (item.getItemId() == R.id.action_share) {
            navController.navigate(R.id.action_share);
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void logOut(String username) {
        mAuth.signOut();
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("username");
        editor.apply();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    private void getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Extract the full formatted address
                String fullAddress = address.getAddressLine(0);  // Full address line

                // Check if the full address contains a Plus Code (e.g., "UH3V+5FW")
                if (fullAddress != null && !fullAddress.isEmpty()) {
                    // Remove the Plus Code pattern at the beginning (e.g., "UH3V+5FW")
                    fullAddress = fullAddress.replaceAll("^[A-Za-z0-9]+\\+[A-Za-z0-9]+,?", "").trim();  // Remove Plus Code
                    setLocation(fullAddress);
                    Log.d("MainActivity", "Address: "+fullAddress);
                }
            }
            } catch(IOException e){
                e.printStackTrace();
                Toast.makeText(this, "Unable to get address", Toast.LENGTH_LONG).show();
            }
        }

        // for change nav image
        private void loadProfileImage() {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) return;

            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            NavigationView navigationView = findViewById(R.id.navigationView);
            View headerView = navigationView.getHeaderView(0);
            ImageView profileImageView = headerView.findViewById(R.id.imageView);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.hasChild("imageUrl")) {
                        String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            // Load image manually without Glide (using AsyncTask)
                            new ImageLoaderTask(profileImageView).execute(imageUrl);
                        } else {
                            profileImageView.setImageResource(R.drawable.logo); // default image
                        }
                    } else {
                        profileImageView.setImageResource(R.drawable.logo); // default image
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    profileImageView.setImageResource(R.drawable.logo); // default image on error
                }
            });
        }




    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public void setLocation(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    @Override
    public double getLat() {
        return latitude;
    }

    @Override
    public double getLon() {
        return longitude;
    }
        // for loction hide
    public void hideLocation() {
        binding.location.setVisibility(View.GONE);
    }

    public void showLocation() {
        binding.location.setVisibility(View.VISIBLE);
    }
        //  this two method for hide actionbar for help fragment
    public void hideToolbar() {
//        getActionBar().hide();
        getSupportActionBar().hide();
    }

    public void showToolbar() {
//        getActionBar().show();
        getSupportActionBar().show();
    }

    //this method for hide bottomActionBar for product fragment
    public void hideBottomAppBar() {
//        binding.bottomAppBar.setVisibility(View.GONE);
        binding.bottomNavigationView.setVisibility(View.GONE);
    }

    public void showBottomAppBar() {
//        binding.bottomAppBar.setVisibility(View.VISIBLE);
        binding.bottomNavigationView.setVisibility(View.VISIBLE);
    }
    public void setLocation(String location) {
        binding.locationView.setText(location);
    }
    @Override
    protected void onResume() {
        super.onResume(); // Sticky notes received from Android if
        showLocation();
//        requestLocationUpdates();
    }
}

