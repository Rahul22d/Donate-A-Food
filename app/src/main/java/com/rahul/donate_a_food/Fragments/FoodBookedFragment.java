package com.rahul.donate_a_food.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rahul.donate_a_food.MainActivity;
import com.rahul.donate_a_food.R;
import com.rahul.donate_a_food.databinding.FragmentFoodBookedBinding;

public class FoodBookedFragment extends Fragment {
    FragmentFoodBookedBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_food_booked, container, false);
        binding = FragmentFoodBookedBinding.inflate(inflater, container, false);

        // Hide the toolbar when this fragment is shown
        if (getActivity() instanceof MainActivity) {
//            ((MainActivity) getActivity()).hideLocation();
        }

        return binding.getRoot();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Show the toolbar again when the fragment is destroyed
        if (getActivity() instanceof MainActivity) {
//            ((MainActivity) getActivity()).showLocation();
        }

        }
}