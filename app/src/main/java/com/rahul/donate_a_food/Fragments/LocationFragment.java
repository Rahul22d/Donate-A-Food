package com.rahul.donate_a_food.Fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.rahul.donate_a_food.MainActivity;
import com.rahul.donate_a_food.R;
import com.rahul.donate_a_food.databinding.FragmentHelpBinding;
import com.rahul.donate_a_food.databinding.FragmentLocationBinding;

import java.io.IOException;
import java.util.List;


public class LocationFragment extends Fragment {
    FragmentLocationBinding binding;
    double latitude, longitude;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLocationBinding.inflate(inflater, container, false);
//        return inflater.inflate(R.layout.fragment_location, container, false);
        // Hide the toolbar when this fragment is shown
//        if (getActivity() instanceof MainActivity) {
//            ((MainActivity) getActivity()).hideToolbar();
//        }

//        MaterialToolbar toolbar = binding.findViewById(R.id.helpT);
//        MaterialToolbar toolbar = binding.helpT;
//        toolbar.setNavigationOnClickListener(v -> {
//            // Handle back navigation when the back arrow is clicked
//            requireActivity().onBackPressed();
//        });
        if(getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).hideLocation();
            latitude = ((MainActivity) getActivity()).getLat();
            longitude = ((MainActivity) getActivity()).getLon();
//            binding.textView3.setText("Location : "+latitude+","+longitude);
        }


        getAddressFromCoordinates(latitude, longitude);

        return binding.getRoot();


    }

    private void getAddressFromCoordinates(double latitude, double longitude) {
        // Use Geocoder to convert latitude and longitude to address
        Geocoder geocoder = new Geocoder(getActivity());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

//            if (addresses != null && !addresses.isEmpty()) {
//                Address address = addresses.get(0);
//                String addressText = address.getAddressLine(0);  // Full address
//                binding.textView3.setText(addressText);  // Display the address in the TextView
//            } else {
//                binding.textView3.setText("Address not found");
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
                    binding.textView3.setText(fullAddress);  // Display the full address without the Plus Code
                    binding.textView3.setTextSize(14);
                } else {
                    binding.textView3.setText("Address not found");
                }
            }else {
                binding.textView3.setText("Addrss not found");
                }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Unable to get address. Please try again later.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Show the toolbar again when the fragment is destroyed
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showToolbar();
            ((MainActivity) getActivity()).showLocation();
 
        }
    }
}