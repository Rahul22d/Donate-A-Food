package com.rahul.donate_a_food;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.rahul.donate_a_food.databinding.ActivityMainBinding;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LocationPass {
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

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);

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
        }

//        SharedPreferences sharedPreferences1 = getSharedPreferences("LocationPrefs", MODE_PRIVATE);
//        String latitudeString = sharedPreferences1.getString("latitude", null);
//        String longitudeString = sharedPreferences1.getString("longitude", null);
//        if (latitudeString != null && longitudeString != null) {
//            currentLatitude = Double.parseDouble(latitudeString);
//            currentLongitude = Double.parseDouble(longitudeString);
//            setLocation(currentLatitude, currentLongitude); // for product fragment
//            LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class); // for home fragment
//            locationViewModel.setLocation(currentLatitude, currentLongitude);
//            saveLocationToFirestore(currentLatitude, currentLongitude);
//        }
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

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
//                    LOCATION_PERMISSION_REQUEST_CODE);
//        } else {
//            getLastLocation();
//        }

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
                    // Set the cleaned-up address
//                    binding.textView.setText(fullAddress);
//                    if(getActivity() instanceof MainActivity){
//                        ((MainActivity) getActivity()).setLocation(fullAddress);
//                    }
//                } else {
//                    binding.textView.setText("Address not found");
//                }
//                } else {
//                binding.textView.setText("Addrss not found");
                }}
            } catch(IOException e){
                e.printStackTrace();
                Toast.makeText(this, "Unable to get address", Toast.LENGTH_LONG).show();
            }
        }


//    // 🔥 Improved GPS Location Fetching
//    public void getLastLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
//                    LOCATION_PERMISSION_REQUEST_CODE);
//            return;
//        }
//
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, location -> {
//                    if (location != null) {
//                        updateLocation(location);
//                    } else {
//                        requestLocationUpdates();
//                    }
//                });
//    }

//    private void requestLocationUpdates() {
//        LocationRequest locationRequest = new LocationRequest.Builder(10000)
//                .setMinUpdateIntervalMillis(30000) // update after 30 sec
//                .setGranularity(Granularity.GRANULARITY_FINE)
//                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
//                .setMinUpdateDistanceMeters(5)
//                .build();
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult != null) {
//                    for (Location location : locationResult.getLocations()) {
//                        updateLocation(location);
//                    }
//                }
//            }
//        };
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }
//
//    private void updateLocation(Location location) {
//        latitude = location.getLatitude();
//        longitude = location.getLongitude();
//        // Store the location in SharedPreferences
////        SharedPreferences sharedPreferences = getSharedPreferences("LocationPrefs", MODE_PRIVATE);
////        SharedPreferences.Editor editor = sharedPreferences.edit();
////        editor.putString("latitude", String.valueOf(latitude)); // Store as String
////        editor.putString("longitude", String.valueOf(longitude)); // Store as String
////        editor.apply(); // Apply the changes
//        // 🔥 Update ViewModel for Fragment communication
////        LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
////        locationViewModel.setLocation(latitude, longitude);
//        Log.d("Location", "Updated Latitude: " + latitude + ", Longitude: " + longitude);
////        setLocation(latitude, longitude);
//    }

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

