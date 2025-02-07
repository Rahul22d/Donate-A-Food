//package com.rahul.donate_a_food.Fragments;
//
//import android.location.Address;
//import android.location.Geocoder;
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//
//import android.util.Pair;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.material.appbar.MaterialToolbar;
//import com.rahul.donate_a_food.MainActivity;
//import com.rahul.donate_a_food.R;
//import com.rahul.donate_a_food.databinding.FragmentHelpBinding;
//import com.rahul.donate_a_food.databinding.FragmentLocationBinding;
//
//import java.io.IOException;
//import java.util.List;
//
//
//public class LocationFragment extends Fragment {
//    FragmentLocationBinding binding;
//    double latitude, longitude;
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        binding = FragmentLocationBinding.inflate(inflater, container, false);
////        return inflater.inflate(R.layout.fragment_location, container, false);
//        // Hide the toolbar when this fragment is shown
////        if (getActivity() instanceof MainActivity) {
////            ((MainActivity) getActivity()).hideToolbar();
////        }
//
////        MaterialToolbar toolbar = binding.findViewById(R.id.helpT);
////        MaterialToolbar toolbar = binding.helpT;
////        toolbar.setNavigationOnClickListener(v -> {
////            // Handle back navigation when the back arrow is clicked
////            requireActivity().onBackPressed();
////        });
//        if(getActivity() instanceof MainActivity){
//            ((MainActivity) getActivity()).hideLocation();
//            latitude = ((MainActivity) getActivity()).getLat();
//            longitude = ((MainActivity) getActivity()).getLon();
////            binding.textView3.setText("Location : "+latitude+","+longitude);
//        }
//
//
//        getAddressFromCoordinates(latitude, longitude);
//
//        return binding.getRoot();
//
//
//    }
//
//    private void getAddressFromCoordinates(double latitude, double longitude) {
//        // Use Geocoder to convert latitude and longitude to address
//        Geocoder geocoder = new Geocoder(getActivity());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
//
////            if (addresses != null && !addresses.isEmpty()) {
////                Address address = addresses.get(0);
////                String addressText = address.getAddressLine(0);  // Full address
////                binding.textView3.setText(addressText);  // Display the address in the TextView
////            } else {
////                binding.textView3.setText("Address not found");
////            }
//            if (addresses != null && !addresses.isEmpty()) {
//                Address address = addresses.get(0);
//
//                // Extract the full formatted address
//                String fullAddress = address.getAddressLine(0);  // Full address line
//
//                // Check if the full address contains a Plus Code (e.g., "UH3V+5FW")
//                if (fullAddress != null && !fullAddress.isEmpty()) {
//                    // Remove the Plus Code pattern at the beginning (e.g., "UH3V+5FW")
//                    fullAddress = fullAddress.replaceAll("^[A-Za-z0-9]+\\+[A-Za-z0-9]+,?", "").trim();  // Remove Plus Code
//
//                    // Set the cleaned-up address
//                    binding.textView3.setText(fullAddress);  // Display the full address without the Plus Code
//                    binding.textView3.setTextSize(14);
//                } else {
//                    binding.textView3.setText("Address not found");
//                }
//            }else {
//                binding.textView3.setText("Addrss not found");
//                }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(getActivity(), "Unable to get address. Please try again later.", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        // Show the toolbar again when the fragment is destroyed
//        if (getActivity() instanceof MainActivity) {
//            ((MainActivity) getActivity()).showToolbar();
//            ((MainActivity) getActivity()).showLocation();
//
//        }
//    }
//}
//package com.rahul.donate_a_food.Fragments;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.rahul.donate_a_food.MainActivity;
//import com.rahul.donate_a_food.R;
//import com.rahul.donate_a_food.databinding.FragmentLocationBinding;
//
//import org.osmdroid.api.IGeoPoint;
//import org.osmdroid.config.Configuration;
//import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
//import org.osmdroid.util.GeoPoint;
//import org.osmdroid.views.MapView;
//import org.osmdroid.views.overlay.Marker;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Locale;
//
//public class LocationFragment extends Fragment {
//    private MapView mapView;
//    private FusedLocationProviderClient fusedLocationProviderClient;
//    private LocationCallback locationCallback;
//    private FragmentLocationBinding binding;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
////        View view = inflater.inflate(R.layout.fragment_location, container, false);
//        binding = FragmentLocationBinding.inflate(inflater, container,false);
//
//        mapView = binding.osmMapView;
//        mapView.setTileSource(TileSourceFactory.MAPNIK);
//        mapView.setMultiTouchControls(true);
//        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
//        requestLocationUpdates();
//
//        return binding.getRoot();
//    }
//
//    private void requestLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
//            return;
//        }
//
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(5000);
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//                if (locationResult != null && !locationResult.getLocations().isEmpty()) {
//                    Location location = locationResult.getLastLocation();
//                    updateMapLocation(location);
//                    getAddressFromCoordinates(location);
//                }
//            }
//        };
//
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
//    }
//
//    private void updateMapLocation(Location location) {
//        if (mapView == null || location == null) return;
//
//        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
//        mapView.getController().setCenter(geoPoint);
//        mapView.getController().setZoom(15);
//
//        Marker marker = new Marker(mapView);
//        marker.setPosition(geoPoint);
//        marker.setTitle("You are here");
//        mapView.getOverlays().clear();
//        mapView.getOverlays().add(marker);
//        mapView.invalidate();
//    }
//
//    private void getAddressFromCoordinates(Location location) {
//        // Use Geocoder to convert latitude and longitude to address
//        double latitude , longitude;
//        latitude = location.getLatitude();
//        longitude = location.getLongitude();
//        Geocoder geocoder = new Geocoder(getActivity());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
//
////            if (addresses != null && !addresses.isEmpty()) {
////                Address address = addresses.get(0);
////                String addressText = address.getAddressLine(0);  // Full address
////                binding.textView3.setText(addressText);  // Display the address in the TextView
////            } else {
////                binding.textView3.setText("Address not found");
////            }
//            if (addresses != null && !addresses.isEmpty()) {
//                Address address = addresses.get(0);
//
//                // Extract the full formatted address
//                String fullAddress = address.getAddressLine(0);  // Full address line
//
//                // Check if the full address contains a Plus Code (e.g., "UH3V+5FW")
//                if (fullAddress != null && !fullAddress.isEmpty()) {
//                    // Remove the Plus Code pattern at the beginning (e.g., "UH3V+5FW")
//                    fullAddress = fullAddress.replaceAll("^[A-Za-z0-9]+\\+[A-Za-z0-9]+,?", "").trim();  // Remove Plus Code
//
//                    // Set the cleaned-up address
//                    binding.textView.setText(fullAddress);  // Display the full address without the Plus Code
//                    binding.textView.setTextSize(14);
//                } else {
//                    binding.textView.setText("Address not found");
//                }
//            }else {
//                binding.textView.setText("Addrss not found");
//                }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(getActivity(), "Unable to get address. Please try again later.", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (fusedLocationProviderClient != null && locationCallback != null) {
//            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//        }
//        if(getActivity() instanceof MainActivity){
//            ((MainActivity) getActivity()).showLocation();
//            ((MainActivity) getActivity()).showToolbar();
//        }
//    }
//}


// for click location
package com.rahul.donate_a_food.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rahul.donate_a_food.LocationViewModel;
import com.rahul.donate_a_food.MainActivity;
import com.rahul.donate_a_food.databinding.FragmentLocationBinding;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationFragment extends Fragment {
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private FragmentLocationBinding binding;
    private GeoPoint selectedLocation;
    private boolean userSelectedLocation = false; // Flag to stop auto-refreshing after selection
    private FloatingActionButton gpsButton;
    private LocationViewModel locationViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLocationBinding.inflate(inflater, container, false);

        if(getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideLocation();
        }

        mapView = binding.osmMapView;
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        gpsButton = new FloatingActionButton(requireContext());
        gpsButton.setImageResource(android.R.drawable.ic_menu_mylocation);
        binding.getRoot().addView(gpsButton);

        gpsButton.setOnClickListener(v -> {
            userSelectedLocation = false; // Allow GPS updates again
            requestLocationUpdates();
        });
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        requestLocationUpdates();
        setupMapGestureListener(); // Enable long-press location selection

        return binding.getRoot();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult != null && !locationResult.getLocations().isEmpty() && !userSelectedLocation) {
                    Location location = locationResult.getLastLocation();
                    selectedLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                    updateMapLocation(selectedLocation);
                    getAddressFromCoordinates(selectedLocation);

                    // share location data between page
                    locationViewModel.setLocation(location.getLatitude(), location.getLongitude());
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupMapGestureListener() {
        mapView.setOnTouchListener(new View.OnTouchListener() {
            private final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    return false; // Ignore single taps
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    IGeoPoint geoPoint = mapView.getProjection().fromPixels((int) e.getX(), (int) e.getY());
                    if (geoPoint instanceof GeoPoint) {
                        selectedLocation = (GeoPoint) geoPoint;
                        userSelectedLocation = true; // Stop auto-refreshing
                        updateMapLocation(selectedLocation);
                        getAddressFromCoordinates(selectedLocation);
                        // update location data
                        locationViewModel.setLocation(selectedLocation.getLatitude(), selectedLocation.getLongitude());
                    }
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void updateMapLocation(GeoPoint geoPoint) {
        if (mapView == null) return;

        mapView.getController().setZoom(15);
        mapView.getController().animateTo(geoPoint); // Ensure it centers properly

        mapView.getOverlays().clear(); // Remove old markers
        Marker marker = new Marker(mapView);
        marker.setPosition(geoPoint);
        marker.setTitle("Selected Location");
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    private void getAddressFromCoordinates(GeoPoint geoPoint) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);
//            if (addresses != null && !addresses.isEmpty()) {
//                String fullAddress = addresses.get(0).getAddressLine(0);
//                binding.textView.setText(fullAddress);
//                if(getActivity() instanceof MainActivity){
//                    ((MainActivity) getActivity()).setLocation(fullAddress);
//                }
//            } else {
//                binding.textView.setText("Address not found");
//            }

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Extract the full formatted address
                String fullAddress = address.getAddressLine(0);  // Full address line

                // Check if the full address contains a Plus Code (e.g., "UH3V+5FW")
                if (fullAddress != null && !fullAddress.isEmpty()) {
                    // Remove the Plus Code pattern at the beginning (e.g., "UH3V+5FW")
                    fullAddress = fullAddress.replaceAll("^[A-Za-z0-9]+\\+[A-Za-z0-9]+,?", "").trim();  // Remove Plus Code

                    // Set the cleaned-up address
                    binding.textView.setText(fullAddress);
                    if(getActivity() instanceof MainActivity){
                        ((MainActivity) getActivity()).setLocation(fullAddress);
                    }
                } else {
                    binding.textView.setText("Address not found");
                }
            }else {
                binding.textView.setText("Addrss not found");
                }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Unable to get address", Toast.LENGTH_LONG).show();
        }
    }

    private void saveLocation() {
        if (selectedLocation == null) {
            Toast.makeText(getActivity(), "Please select a location first", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserLocation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("latitude", (float) selectedLocation.getLatitude());
        editor.putFloat("longitude", (float) selectedLocation.getLongitude());
        editor.apply();

        Toast.makeText(getActivity(), "Location saved successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showLocation();
            ((MainActivity) getActivity()).showToolbar();
        }
    }
}
