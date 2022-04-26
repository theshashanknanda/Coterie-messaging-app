package com.project.coterie2.view.loginandsignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.project.coterie2.databinding.ActivityRegisterBinding;
import com.project.coterie2.databinding.MultipleErrorLayoutBinding;
import com.project.coterie2.viewmodel.CoterieViewModel;
import com.project.coterie2.view.dashboard.activity.DashboardActivity;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private CoterieViewModel viewModel;
    private MultipleErrorLayoutBinding errorPopupBinding;
    private int errorCount = 0;
    private AlertDialog.Builder errorDialogBuilder;
    private AlertDialog errorDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        errorPopupBinding = MultipleErrorLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(CoterieViewModel.class);
        errorDialogBuilder = new AlertDialog.Builder(this);
        errorDialogBuilder.setView(errorPopupBinding.getRoot());
        errorDialog = errorDialogBuilder.create();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // go to login
        binding.gotoLoginButtonId.setOnClickListener(v -> {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        });

        // create account
        binding.registerButtonId.setOnClickListener(v -> {
            createAccount();
        });
    }

    private void createAccount() {
        // start progress dialogue
        ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
        dialog.setTitle("Creating account");
        dialog.setMessage("Just a second...");
        dialog.show();

        // get input data
        String name = binding.nameED.getText().toString().trim();
        String email = binding.emailED.getText().toString().trim();
        String password = binding.passwordED.getText().toString().trim();

        // check for empty fields
        if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){

            // Register with firebase
            viewModel.repository.auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            // on success save user data in realtime database (success/failure toast in repository)
                            viewModel.saveUserData(name, email, password);
                            dialog.dismiss();

                            // after saving go to dashboard
                            startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // if failed to create account show toast
                            dialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "There was a problem creating your account", Toast.LENGTH_SHORT).show();

                            // if failed 3 times in consequently then show detailed dialogue
                            errorCount++;
                            if(errorCount%3 == 0){
                                if(errorCount > 10){
                                    // if failed 10 times in consequently then go back to main activity
                                    finish();
                                }else{
                                    errorDialog.show();
                                    errorPopupBinding.seeDetailsButton.setOnClickListener(view -> {
                                        binding.nameED.setError(e.getLocalizedMessage());
                                        binding.emailED.setError(e.getLocalizedMessage());
                                        binding.passwordED.setError(e.getLocalizedMessage());
                                        errorDialog.dismiss();
                                    });
                                    errorPopupBinding.hideButton.setOnClickListener(view -> {
                                        errorDialog.dismiss();
                                    });
                                }
                            }
                        }
                    });
        }else{
            // show fields are empty
            Toast.makeText(RegisterActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }
}