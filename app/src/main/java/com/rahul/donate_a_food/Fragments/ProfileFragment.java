package com.rahul.donate_a_food.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rahul.donate_a_food.R;
import com.rahul.donate_a_food.databinding.FragmentHelpBinding;
import com.rahul.donate_a_food.databinding.FragmentProductBinding;
import com.rahul.donate_a_food.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }
}