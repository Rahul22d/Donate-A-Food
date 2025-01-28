package com.rahul.donate_a_food;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.rahul.donate_a_food.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements LocationPass{
    private ActivityMainBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawer;
    private FirebaseAuth mAuth;
    private NavigationView navigationView;
    private String username;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    double latitude, longitude;
    private static final int CAMERA_REQUEST = 1888;  // Request code for camera
    private static final int GALLERY_REQUEST = 100; // Request code for gallery
    Uri foodImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
//    }


        drawer = binding.drawerLayout;
        navigationView = binding.navigationView;
        //logIn
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if the user is logged in by reading SharedPreferences
        Intent intent = getIntent();
        String email = intent.getStringExtra("username");
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        username = sharedPreferences.getString("username", email);
        if (!isLoggedIn) {
            navigationView.getMenu().findItem(R.id.action_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.action_logIn).setVisible(true);
        } else {
            // this two line for show login email
            TextView useremail = navigationView.getHeaderView(0).findViewById(R.id.useremail);
            useremail.setText(username);

            navigationView.getMenu().findItem(R.id.action_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.action_logIn).setVisible(false);
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.action_home, R.id.action_history, R.id.action_help, R.id.action_feedback, R.id.action_share)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        setupBottomAppBarNavigation(navController);

        //float button

        binding.floatingActionButton.setOnClickListener(V -> {
//            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_main,new ProductFragment()).commit();
            if (isLoggedIn) {
                navController.navigate(R.id.nav_add);
            } else {
                Intent intent1 = new Intent(MainActivity.this, LogInActivity.class);
                startActivity(intent1);
                finish();
            }
        });


        // for location
        binding.locationIcon.setOnClickListener(V -> {
            navController.navigate(R.id.nav_location);
        });
        // Set up Navigation Drawer item click listener
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // for location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check if both fine and coarse location permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions not granted, request them
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions are granted, proceed to fetch the location
            getLastLocation();
        }

        // Check if the device is connected to the internet
        if (!isConnected()) {
            // If not connected, show a Toast message
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupBottomAppBarNavigation(NavController navController) {
        BottomNavigationView bottomNavigationView = binding.bottomNavigationView;

        // Listener for BottomNavigationView menu item clicks
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_order) {
                    navController.navigate(R.id.nav_bookedFood); // Navigate to LocationFragment
                    return true;
                } else if (item.getItemId() == R.id.nav_account) {
                    navController.navigate(R.id.nav_account); // Navigate to ProfileFragment
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    // Handle the item selection in the navigation drawer
    private boolean onNavigationItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        // Close the drawer after item click
        drawer.closeDrawers();
        // Reload the drawer menu

        if (item.getItemId() == R.id.action_logIn) {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
//            finish();
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            ;
            logOut(username);
            Intent intent = new Intent(this, MainActivity.class);
            // for clean all previous activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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
        } else {
//            return false;
            return super.onOptionsItemSelected(item);
        }

    }

    // log out
    public void logOut(String usename) {
        mAuth.signOut();
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false); // Set login status to true
        editor.remove("username");
        editor.apply();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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

    // for loction hide
    public void hideLocation() {
        binding.location.setVisibility(View.GONE);
    }

    public void showLocation() {
        binding.location.setVisibility(View.VISIBLE);
    }

    // for location using gps
    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);

            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    double latitude, longitude;
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some cases, this can be null.
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
//                            locationTextView.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
                            Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);
                            setLocation(latitude,longitude);
                        } else {
//                            locationTextView.setText("Location not available.");
                            Log.d("Location", "Location not available.");
                        }
                    }


                });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Check if both permissions are granted
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, proceed to fetch location
                getLastLocation();
            } else {
                // Permissions denied, show a message to the user
//                locationTextView.setText("Permission denied. Cannot access location.");
                Toast.makeText(this,"Location permision denie",Toast.LENGTH_SHORT).show();
            }
        }
    }

    // for internet connection check
    private boolean isConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }

        return false;
    }

    @Override
    public void setLocation(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    @Override
    public double getLat() {
        return this.latitude;
    }

    @Override
    public double getLon() {
        return this.longitude;
    }

//    // for food IMAGE
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == GALLERY_REQUEST) {
//                foodImage = data.getData();  // Get URI of the selected image from gallery
////                imageView.setVisibility(View.VISIBLE);  // Show ImageView
////                imageView.setImageURI(imageUri);  // Set the image to the ImageView
//            } else if (requestCode == CAMERA_REQUEST) {
//                foodImage = data.getData();  // Handle camera URI if needed
////                imageView.setVisibility(View.VISIBLE);
////                imageView.setImageURI(imageUri);
//            }
//        } else {
//            Toast.makeText(this, "Image capture failed or canceled", Toast.LENGTH_SHORT).show();
//        }
//    }
    @Override
    protected void onResume() {
        super.onResume(); // Sticky notes received from Android if
        showLocation();
    }
}



