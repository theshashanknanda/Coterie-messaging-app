package com.project.coterie2.view.chat.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.project.coterie2.R;
import com.project.coterie2.databinding.ActivityChatDetailsBinding;
import com.project.coterie2.model.User;
import com.project.coterie2.view.chat.adapter.UserAdapter;
import com.project.coterie2.view.dashboard.activity.DashboardActivity;
import com.project.coterie2.viewmodel.CoterieViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatDetails extends AppCompatActivity {
    private ActivityChatDetailsBinding binding;
    private CoterieViewModel viewModel;
    private List<String> userIds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        userIds = new ArrayList<>();

        getSupportActionBar().hide();

        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(CoterieViewModel.class);

        String name = getIntent().getStringExtra("name");
        String imageURL = getIntent().getStringExtra("image_url");
        String adminID = getIntent().getStringExtra("adminID");
        String communityID = getIntent().getStringExtra("communityID");

        binding.collapsingtoolbarlayout.setTitle(name);
        Glide.with(this).load(imageURL).placeholder(R.drawable.ic_choosephoto).into(binding.imageView);

        Log.d("coterie_error", "Com ID " + communityID);
        viewModel.communityUsers(communityID);

        viewModel.currentCommunityUsers.observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                Log.d("cerror", users.toString());
                List<String> userRole = new ArrayList<>();

                binding.recylerView.setLayoutManager(new LinearLayoutManager(ChatDetails.this));
                UserAdapter adapter = new UserAdapter(ChatDetails.this, users, adminID);
                binding.recylerView.setAdapter(adapter);
            }
        });

        binding.leaveBtn.setOnClickListener(v -> {
            if(!viewModel.currentUserID.getValue().equals(adminID)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Do you really want to leave this community");
                builder.setMessage("You will no longer be a member of the community");

                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        viewModel.leaveCommunity(communityID);
                        finish();
                        startActivity(new Intent(ChatDetails.this, DashboardActivity.class));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                Toast.makeText(this, "Admins cannot leave the community", Toast.LENGTH_SHORT).show();
            }
        });

        binding.editBtn.setOnClickListener(v -> {
            if(viewModel.currentUserID.getValue().equals(adminID)){
                Log.d("cerror", viewModel.currentUserID.getValue() + " - " + adminID);
                Intent intent = new Intent(this, EditCommunity.class);
                intent.putExtra("name", name);
                intent.putExtra("image_url", imageURL);
                intent.putExtra("adminID", adminID);
                intent.putExtra("communityID", communityID);

                startActivity(intent);
            }else{
                Toast.makeText(this, "This feature is restrcited to admins", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
