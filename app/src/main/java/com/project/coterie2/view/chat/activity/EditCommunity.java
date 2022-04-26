package com.project.coterie2.view.chat.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.project.coterie2.databinding.ActivityEditCommunityBinding;
import com.project.coterie2.model.Community;
import com.project.coterie2.view.dashboard.activity.EditProfileActivity;
import com.project.coterie2.viewmodel.CoterieViewModel;

public class EditCommunity extends AppCompatActivity {
    private ActivityEditCommunityBinding binding;
    private ProgressDialog dialog;
    private CoterieViewModel viewModel;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private Community community;
    private String URL, imageURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCommunityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        dialog = new ProgressDialog(this);

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(CoterieViewModel.class);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String name = getIntent().getStringExtra("name");
        imageURL = getIntent().getStringExtra("image_url");
        String adminID = getIntent().getStringExtra("adminID");
        String communityID = getIntent().getStringExtra("communityID");

        community = new Community(name, imageURL, adminID);

        binding.titleED.setText(name);
        Glide.with(this).load(imageURL).into(binding.imageView);

        // activity result launcher for selecting the photo
        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                dialog.setTitle("Uploading photo");
                dialog.setMessage("Please wait");
                dialog.show();

                uploadFile(result, communityID, name);
            }
        });

        // select photo from local storage
        binding.addPhotoButton.setOnClickListener(v -> {
            launcher.launch("image/*");
        });

        binding.updateCommunityBtn.setOnClickListener(v -> {
            String newTitle = binding.titleED.getText().toString();
            Log.d("cerror", "hereeeeeeee");
            if(!newTitle.isEmpty()){
                community.setImageURL(URL);
                community.setName(newTitle);
                database.getReference().child("communities").child("list")
                        .child(communityID).setValue(community);

                Toast.makeText(EditCommunity.this, "User Updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void uploadFile(Uri uri, String communityID, String name) {
        storage.getReference().child("community").child(name).putFile(uri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        storage.getReference().child("community").child(name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                imageURL = url;
                                community.setImageURL(url);

                                database.getReference().child("communities").child("list")
                                        .child(communityID).setValue(community);

                                dialog.dismiss();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("cerror", e.getLocalizedMessage());
                        dialog.dismiss();
                        Toast.makeText(EditCommunity.this, "There was a problem uploading the file", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}