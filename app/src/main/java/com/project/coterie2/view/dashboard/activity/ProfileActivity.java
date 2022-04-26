package com.project.coterie2.view.dashboard.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.project.coterie2.R;
import com.project.coterie2.databinding.ActivityProfileBinding;
import com.project.coterie2.model.Community;
import com.project.coterie2.model.User;
import com.project.coterie2.view.dashboard.adapters.CommunityAdapter;
import com.project.coterie2.view.dashboard.clickListeners.RecyclerViewClickListener;
import com.project.coterie2.viewmodel.CoterieViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private CoterieViewModel viewModel;
    private User currentUser;
    private List<Community> communityArrayList;
    private List<String> communityIDs;
    private int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(CoterieViewModel.class);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        communityArrayList = new ArrayList<>();
        communityIDs = new ArrayList<>();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // getting the current user data from viewmodel
        viewModel.currentUserData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                currentUser = user;

                Glide.with(ProfileActivity.this).load(currentUser.getImage_url()).placeholder(R.drawable.ic_coterie_vector_man1).into(binding.profileIMV);
                binding.profileNameTV.setText(currentUser.getName());

                // getting total number of communities user is in
                showNoOfCommunties();
            }
        });

        // Go to edit profile activity
        binding.profileEditButton.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });

        // getting current user communities
        viewModel.getUserCommunities();
        viewModel.currentUserCommunitiesIDs.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> stringList) {
                communityIDs = stringList;
            }
        });
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        };

        viewModel.currentUserCommunities.observe(this, new Observer<List<Community>>() {
            @Override
            public void onChanged(List<Community> communities) {
                CommunityAdapter adapter = new CommunityAdapter(ProfileActivity.this, communities, communityIDs, listener);
                binding.recylerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                binding.recylerView.setAdapter(adapter);

                adapter.notifyDataSetChanged();

                Log.d("coterie_error", communityArrayList.toString() + "from profile");
            }
        });
    }

    private void showNoOfCommunties() {
        viewModel.repository.database.getReference().child("users").child(currentUser.getUid())
                .child("communities").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                i = 0;

                if(snapshot.exists()){
                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        i++;
                    }
                }

                binding.communityCount.setText("You are in " + i + " communities");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}