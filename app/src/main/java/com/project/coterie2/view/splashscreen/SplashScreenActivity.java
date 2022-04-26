package com.project.coterie2.view.splashscreen;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.coterie2.databinding.ActivitySplashScreenBinding;
import com.project.coterie2.view.dashboard.activity.DashboardActivity;
import com.project.coterie2.view.loginandsignup.MainActivity;
import com.project.coterie2.view.walkthrough.activity.WalkthroughActivity;
import com.project.coterie2.databinding.ActivitySplashScreenBinding;

import java.net.InetAddress;

public class SplashScreenActivity extends AppCompatActivity {
    private ActivitySplashScreenBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        getSupportActionBar().hide();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // check if user is not connected to a network
        if(!isInternetAvailable()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert");
            builder.setMessage("You are not connected to any network, kindly restart after connecting");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                    System.exit(0);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }else{
            // if logged in go directly to dashboard
            // if not then go to walk through screen
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(user != null){
                        startActivity(new Intent(SplashScreenActivity.this, DashboardActivity.class));
                        finish();
                    }else{
                        startActivity(new Intent(SplashScreenActivity.this, WalkthroughActivity.class));
                        finish();
                    }
                }
            }, 2000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}