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
import com.project.coterie2.databinding.ActivityLoginBinding;
import com.project.coterie2.databinding.MultipleErrorLayoutBinding;
import com.project.coterie2.viewmodel.CoterieViewModel;
import com.project.coterie2.view.dashboard.activity.DashboardActivity;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private MultipleErrorLayoutBinding errorPopupBinding;
    private CoterieViewModel viewModel;
    private int errorCount = 0;
    private AlertDialog.Builder errorDialogBuilder;
    private AlertDialog errorDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        errorPopupBinding = MultipleErrorLayoutBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(CoterieViewModel.class);
        errorDialogBuilder = new AlertDialog.Builder(this);
        errorDialogBuilder.setView(errorPopupBinding.getRoot());
        errorDialog = errorDialogBuilder.create();
        getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // go to register
        binding.gotoRegisterButtonId.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
            finish();
        });

        binding.loginButtonId.setOnClickListener(v -> {
            login();
        });
    }

    private void login() {
        // start progress dialogue
        ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setTitle("Logging you in");
        dialog.setMessage("Just a second...");
        dialog.show();

        // get input data
        String email = binding.emailED.getText().toString();
        String password = binding.passwordED.getText().toString();

        // check for empty fields
        if(!email.isEmpty() && !password.isEmpty()){
            // login with firebase
            viewModel.repository.auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            // on success go to dashboard
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // if failed to create account show toast
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

                            // if failed 3 times in consequently then show detailed dialogue
                            errorCount++;
                            if(errorCount%3 == 0){
                                // if failed 10 times in consequently then go back to main activity
                                if(errorCount > 10){
                                    finish();
                                }else{
                                    errorDialog.show();
                                    errorPopupBinding.seeDetailsButton.setOnClickListener(view -> {
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
            Toast.makeText(LoginActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }
}