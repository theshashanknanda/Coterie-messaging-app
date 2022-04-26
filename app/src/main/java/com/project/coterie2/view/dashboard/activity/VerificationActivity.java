package com.project.coterie2.view.dashboard.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Toast;

import com.project.coterie2.databinding.ActivityVerificationBinding;
import com.project.coterie2.viewmodel.CoterieViewModel;

public class VerificationActivity extends AppCompatActivity {
    private ActivityVerificationBinding binding;
    private CoterieViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(CoterieViewModel.class);
        getSupportActionBar().setTitle("Verification");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // disable if already verified
        if(viewModel.checkForVerification()){
            binding.sendEmailButton.setEnabled(false);
        }

        // send verification email
        binding.sendEmailButton.setOnClickListener(v -> {
            viewModel.sendEmailVerification();
            Toast.makeText(this, "Email Sent", Toast.LENGTH_SHORT).show();
        });

        // check for verified
        // if verified then disable 'Send email' button
        // if not show toast not verified
        binding.checkForVerifyButton.setOnClickListener(v -> {
            if(viewModel.checkForVerification()){
                // email verified
                Toast.makeText(this, "Email Verified", Toast.LENGTH_SHORT).show();
                binding.sendEmailButton.setEnabled(false);
            }else{
                // not verified yet
                Toast.makeText(this, "Email is not Verified yet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
