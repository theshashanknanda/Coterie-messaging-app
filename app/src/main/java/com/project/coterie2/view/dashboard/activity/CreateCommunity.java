package com.project.coterie2.view.dashboard.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.project.coterie2.databinding.ActivityCreateCommunityBinding;
import com.project.coterie2.model.Community;
import com.project.coterie2.viewmodel.CoterieViewModel;

public class CreateCommunity extends AppCompatActivity {
    private ActivityCreateCommunityBinding binding;
    private String title;
    private Uri uri;
    private CoterieViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCommunityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Community");
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(CoterieViewModel.class);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // getting images chosen from gallery
        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                uri = result;
                binding.imageView.setImageURI(uri);
            }
        });

        binding.choosePhotoButton.setOnClickListener(v -> {
            launcher.launch("image/*");
        });

        binding.createCommunityButton.setOnClickListener(view -> {
            // create a entry in communities list
            createRecord();
        });
    }

    private void createRecord() {
        title = binding.titleED.getText().toString();

        ProgressDialog dialog = new ProgressDialog(CreateCommunity.this);
        dialog.setTitle("Creating community");
        dialog.setMessage("please wait...");
        dialog.show();

        if((!title.isEmpty()) && (uri != null)){
            // uploading photo
            viewModel.repository.storage.getReference().child("community").child(title).putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // getting the image URL
                            viewModel.repository.storage.getReference().child("community").child(title).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imgURL = uri.toString();

                                            // now create an entry, add user in community, add community in user
                                            Community community = new Community();
                                            community.setName(title);
                                            community.setImageURL(imgURL);
                                            community.setAdminID(viewModel.currentUserID.getValue());
                                            viewModel.createCommunity(community);

                                            dialog.dismiss();
                                            startActivity(new Intent(CreateCommunity.this, DashboardActivity.class));
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateCommunity.this, "Something went wrong while uploading the image", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
        }else{
            Toast.makeText(CreateCommunity.this, "Fields empty", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }
}
