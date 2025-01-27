package com.rahul.donate_a_food.Fragments;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;
import com.rahul.donate_a_food.MainActivity;
import com.rahul.donate_a_food.R;
import com.rahul.donate_a_food.databinding.FragmentHelpBinding;

public class HelpFragment extends Fragment {
    FragmentHelpBinding binding;
    ConstraintLayout constraintLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout using ViewBinding
        binding = FragmentHelpBinding.inflate(inflater, container, false);


        // Hide the toolbar when this fragment is shown
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).hideToolbar();
        }

//        MaterialToolbar toolbar = binding.findViewById(R.id.helpT);
        MaterialToolbar toolbar = binding.helpT;
        toolbar.setNavigationOnClickListener(v -> {
            // Handle back navigation when the back arrow is clicked
            requireActivity().onBackPressed();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Show the toolbar again when the fragment is destroyed
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showToolbar();
        }
    }

}