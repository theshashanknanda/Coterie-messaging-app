package com.project.coterie2.view.dashboard.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.coterie2.R;
import com.project.coterie2.databinding.ActivityDashboardBinding;
import com.project.coterie2.model.User;
import com.project.coterie2.view.dashboard.fragment.CommunityFragment;
import com.project.coterie2.view.dashboard.fragment.JoinNewFragment;
import com.project.coterie2.viewmodel.CoterieViewModel;
import com.project.coterie2.view.loginandsignup.MainActivity;

public class DashboardActivity extends AppCompatActivity {
    private ActivityDashboardBinding binding;
    private CoterieViewModel viewModel;
    private User currentUser;
    private String currentUID;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(CoterieViewModel.class);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportFragmentManager().beginTransaction().replace(R.id.dashboardContainer, new CommunityFragment()).commit();

        // Adview
        try {
            AdView adView = new AdView(this);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId("ca-app-pub-2317393289250954/4317953107");

            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });

            adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }catch (Exception e){
            Log.d("coterie_ad", e.getLocalizedMessage());
        }

        // disabling middle dummy item in bottom navigation view
        binding.bottomNavView.getMenu().getItem(1).setEnabled(false);

        // getting current user data
        viewModel.repository.currentUserData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                currentUser = user;
            }
        });

        // getting current user ID
        viewModel.currentUserID.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentUID = s;
            }
        });

        // go to create community screen when clicked on fab
        binding.createCommunityButton.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, CreateCommunity.class));
        });

        binding.bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.community_menu_id:
                        getSupportFragmentManager().beginTransaction().replace(R.id.dashboardContainer, new CommunityFragment()).commit();
                        binding.bottomNavView.getMenu().getItem(0).setChecked(true);
                        break;
                    case R.id.joinnew_menu_id:
                        getSupportFragmentManager().beginTransaction().replace(R.id.dashboardContainer, new JoinNewFragment()).commit();
                        binding.bottomNavView.getMenu().getItem(2).setChecked(true);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile_menu_id:
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                break;
            case R.id.verification_menu_id:
                verify();
                break;
            case R.id.logout_menu_id:
                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                builder.setTitle("Alert");
                builder.setMessage("Do you really want to signout from your account?");
                builder.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        signOut();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut(){
        viewModel.signOut();
        startActivity(new Intent(DashboardActivity.this, MainActivity.class));
        finish();
    }

    private void verify(){
        startActivity(new Intent(DashboardActivity.this, VerificationActivity.class));
    }
}
