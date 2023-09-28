package com.robypomper.smartvan.smart_van.android.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.robypomper.smartvan.smart_van.android.databinding.ActivitySvmainBinding;


public class SVMainActivity extends AppCompatActivity {
    private ActivitySvmainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySvmainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

}