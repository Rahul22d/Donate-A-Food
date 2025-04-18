package com.rahul.donate_a_food.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rahul.donate_a_food.LocationViewModel;
import com.rahul.donate_a_food.MainActivity;
import com.rahul.donate_a_food.R;
import com.rahul.donate_a_food.databinding.FragmentLocationBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private FragmentLocationBinding binding;
    private Marker selectedMarker;
    private Marker searchedMarker;
    private Marker currentLocationMarker;
    private LatLng selectedLatLng;
    private boolean userSelectedLocation = false;
    private LocationViewModel locationViewModel;

    private EditText searchEditText;
    private ImageView searchButton;
    private ImageView gpsButton;
    private View mapFragmentView;
    private boolean isGpsClicked = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLocationBinding.inflate(inflater, container, false);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideLocation();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);

        searchEditText = binding.searchEditText;
//        searchButton = binding.searchIcon;
        gpsButton = binding.gpsIcon;

        binding.searchLayout.setVisibility(View.GONE);
        binding.gpsIcon.setVisibility(View.GONE);
        mapFragmentView = binding.getRoot().findViewById(R.id.mapFragment);
        mapFragmentView.setVisibility(View.GONE);

        binding.showMap.setOnClickListener(v -> {
            binding.showMap.setVisibility(View.GONE);
            binding.searchLayout.setVisibility(View.VISIBLE);
            binding.gpsIcon.setVisibility(View.VISIBLE);
            mapFragmentView.setVisibility(View.VISIBLE);
        });

//        searchButton.setOnClickListener(v -> {
//            String placeName = searchEditText.getText().toString();
//            if (!placeName.isEmpty()) {
//                searchPlaceAndMoveMap(placeName);
//            }
//        });
        searchEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableEnd = searchEditText.getCompoundDrawables()[2] != null
                        ? searchEditText.getCompoundDrawables()[2].getBounds().width()
                        : 0;

                if (event.getRawX() >= (searchEditText.getRight() - drawableEnd - searchEditText.getPaddingEnd())) {
                    String placeName = searchEditText.getText().toString().trim();
                    if (!placeName.isEmpty()) {
                        searchPlaceAndMoveMap(placeName);
                    }
                    return true;
                }
            }
            return false;
        });


        gpsButton.setOnClickListener(v -> {
            isGpsClicked = true;  // Set the flag
            requestLocationUpdates();
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return binding.getRoot();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap gMap) {
        this.googleMap = gMap;

        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.setOnMapLongClickListener(this::onMapLongClick);

        LatLng savedLocation = locationViewModel.getLatLng();
        if (savedLocation != null && savedLocation.latitude != 0 && savedLocation.longitude != 0) {
            userSelectedLocation = true;
            updateMapLocation(savedLocation);
            getAddressFromLatLng(savedLocation);
        } else {
            requestLocationUpdates();
        }
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                    Location location = locationResult.getLastLocation();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    showCurrentLocationMarker(latLng);

                    if (!isGpsClicked) {
                        getAddressFromLatLng(latLng);  // ✅ Only run this if GPS button wasn't clicked
                    }

                    isGpsClicked = false; // Reset the flag for next time
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    private void showCurrentLocationMarker(LatLng latLng) {
        if (googleMap == null) return;

        if (currentLocationMarker != null) currentLocationMarker.remove();

        currentLocationMarker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
    }

    private void onMapLongClick(LatLng latLng) {
        userSelectedLocation = true;
        updateMapLocation(latLng);
        getAddressFromLatLng(latLng);
        locationViewModel.setLocation(latLng.latitude, latLng.longitude);
    }

    private void updateMapLocation(LatLng latLng) {
        if (googleMap == null) return;

        selectedLatLng = latLng;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

        if (selectedMarker != null) selectedMarker.remove();

        selectedMarker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Selected Location"));
    }

    private void getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0);

                if (fullAddress != null && !fullAddress.isEmpty()) {
                    fullAddress = fullAddress.replaceAll("^[A-Za-z0-9]+\\+[A-Za-z0-9]+,?", "").trim();
                    binding.textView.setText(fullAddress);
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).setLocation(fullAddress);
                    }
                } else {
                    binding.textView.setText("Address not found");
                }
            } else {
                binding.textView.setText("Address not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Unable to get address", Toast.LENGTH_LONG).show();
        }
    }

    private void searchPlaceAndMoveMap(String placeName) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(placeName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                if (googleMap != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

                    if (searchedMarker != null) {
                        searchedMarker.remove();
                    }

                    searchedMarker = googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Searched Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

//                    getAddressFromLatLng(latLng); // Optional: show address, but don't update ViewModel
                }

            } else {
                Toast.makeText(getActivity(), "Place not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error fetching location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showLocation();
            ((MainActivity) getActivity()).showToolbar();
        }
    }
}
