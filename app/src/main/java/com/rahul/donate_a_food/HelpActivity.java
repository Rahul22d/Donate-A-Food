package com.rahul.donate_a_food;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.rahul.donate_a_food.databinding.ActivityMainBinding;

public class HelpActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_help);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        setSupportActionBar(binding.helpT);
//        binding.helpToolbar.setNavigationOnClickListener {
//            onBackPressed();
//        }
    }
}