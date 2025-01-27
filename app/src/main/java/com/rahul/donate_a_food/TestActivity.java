package com.rahul.donate_a_food;
//
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.widget.Toolbar;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//import androidx.navigation.ui.AppBarConfiguration;
//import androidx.navigation.ui.NavigationUI;
//
//import com.google.android.material.appbar.MaterialToolbar;
//import com.google.android.material.bottomappbar.BottomAppBar;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.navigation.NavigationView;
//import com.google.android.material.snackbar.Snackbar;
//import com.rahul.donate_a_food.Fragments.HistoryFragment;
//import com.rahul.donate_a_food.Fragments.HomeFragment;
//import com.rahul.donate_a_food.Fragments.LocationFragment;
//import com.rahul.donate_a_food.databinding.ActivityMainBinding;
//
//public class TestActivity extends AppCompatActivity {
//    private ActivityMainBinding binding;
//
//    private AppBarConfiguration mAppBarConfiguration;
//    private DrawerLayout drawerLayout;
//    private BottomAppBar bottomAppBar;
//    private MaterialToolbar toolbar;
//    private FloatingActionButton fab;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//
////        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        // Find views
//        toolbar = binding.toolbar;
//        drawerLayout = findViewById(R.id.drawerLayout);
//        bottomAppBar = findViewById(R.id.bottomAppBar);
//        fab = findViewById(R.id.floatingActionButton);
//
//        // Set up the toolbar as the action bar
//        setSupportActionBar(toolbar);
//
//        // Set up Navigation Drawer icon (Navigation icon in the toolbar)
//        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
//
//        // Set up BottomAppBar click listener
//        bottomAppBar.setOnClickListener(v -> {
//            // Handle BottomAppBar click (For example, change the layout)
//            changeLayout();
//        });
//
//        // Set up Navigation Drawer item click listener
//        NavigationView navigationView = findViewById(R.id.navigationView);
//        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
//
//        // Handle FloatingActionButton click
//        fab.setOnClickListener(v -> {
//            Snackbar.make(v, "FAB clicked", Snackbar.LENGTH_SHORT).show();
//        });
//
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.action_home, R.id.action_history, R.id.action_help, R.id.action_feedback, R.id.action_share)
//
//                .setOpenableLayout(drawerLayout )
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
//    }
//
//
//
//    private void setSupportActionBar(Toolbar toolbar) {
//
//    }
//
//    // Method to change layout when BottomAppBar is clicked
//    private void changeLayout() {
//        // For example, replace a Fragment
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.nav_host_fragment_content_main, new HomeFragment()) // Replace with your fragment
//                .commit();
//    }
//
//    // Handle Navigation Drawer item clicks
//    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
////        switch (item.getItemId()) {
//            if(item.getItemId() == R.id.action_home) {
//                // Handle first menu item
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.nav_host_fragment_content_main, new HomeFragment()) // Replace with your fragment
//                        .commit();
//            }
//            else if(item.getItemId() == R.id.action_history) {
//                // Handle second menu item
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.nav_host_fragment_content_main, new HistoryFragment()) // Replace with your fragment
//                        .commit();
//            } else if (item.getItemId() == R.id.nav_location) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.nav_host_fragment_content_main, new LocationFragment())
//                        .commit();
//            }
//            // Add more cases for other items
////        }
//
//        // Close the drawer after item selection
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return true;
//    }
//
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
//}
//



import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.rahul.donate_a_food.Fragments.HomeFragment;
import com.rahul.donate_a_food.Fragments.LocationFragment;
import com.rahul.donate_a_food.Fragments.ProductFragment;
import com.rahul.donate_a_food.Fragments.ProfileFragment;

public class TestActivity extends AppCompatActivity {
    private NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        // Find the NavHostFragment and set up the NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        navController = navHostFragment.getNavController();

        // Initialize the BottomAppBar
        BottomNavigationView bottomAppBar = findViewById(R.id.bottomNavigationView2);
//        setSupportActionBar(bottomAppBar);


        FloatingActionButton fab = findViewById(R.id.fab);
        // Set the listener for BottomAppBar menu items
        fab.setOnClickListener(v -> Toast.makeText(this, "Navigation icon clicked", Toast.LENGTH_SHORT).show());

        // Handle menu item clicks from the BottomAppBar
//        bottomAppBar.setOnNavigationItemSelectedListener( item -> {
////            switch (item.getItemId()) {
//                if(R.id.action_home == item.getItemId()) {
//                    switchFragment(new HomeFragment());
//                    return true;
//                }
//                else if (R.id.nav_order == item.getItemId()) {
//                    switchFragment(new LocationFragment());
////                    Toast.makeText(this, "Navigation icon clicked location", Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//                else if (R.id.nav_account == item.getItemId()) {
//                    switchFragment(new ProfileFragment());
//                    return true;
//                }
//                else {
//                    return false;
//            }
//        });
        if(savedInstanceState == null) {
            switchFragment(new HomeFragment());
        }
        bottomAppBar.setOnItemSelectedListener(item -> {
            if(R.id.action_home == item.getItemId()) {
                switchFragment(new HomeFragment());
                return true;
            }
            else if (R.id.nav_order == item.getItemId()) {
                switchFragment(new LocationFragment());
//                    Toast.makeText(this, "Navigation icon clicked location", Toast.LENGTH_SHORT).show();
                return true;
            }
            else if (R.id.nav_account == item.getItemId()) {
                switchFragment(new ProfileFragment());
                return true;
            }
            else {
                return false;
            }
        });

        // Set the BottomNavigationView's selected item to the start destination
//        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
//            if (destination.getId() == R.id.nav_order) {
//                bottomAppBar.setSelectedItemId(R.id.action_);
//            }
//            // Add more cases to handle other fragments' selection if needed
//        });
    }

    // Switch between fragments dynamically
    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
        //transaction.addToBackStack(null); // Optional: to allow fragment navigation stack

    }
    // Inflate the menu for the BottomAppBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        // Check if there's any fragment in the back stack
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            // If there's more than one fragment in the stack, pop the current fragment
            getSupportFragmentManager().popBackStack();
        } else {
            // If there's only one fragment (the HomeFragment), let the system handle the back button
            super.onBackPressed();
        }
    }


}

