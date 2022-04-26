package com.project.coterie2.view.dashboard.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.project.coterie2.R;
import com.project.coterie2.databinding.ActivityEditProfileBinding;
import com.project.coterie2.model.User;
import com.project.coterie2.viewmodel.CoterieViewModel;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private CoterieViewModel viewModel;
    private User currentUser;
    private ProgressDialog dialog;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(CoterieViewModel.class);
        dialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // current user data
        viewModel.currentUserData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                currentUser = user;
                binding.titleED.setText(currentUser.getName());

                // set previous photo in imageview
                Glide.with(EditProfileActivity.this).load(currentUser.getImage_url()).placeholder(R.drawable.ic_coterie_vector_man1).into(binding.imageView);
            }
        });

        // activity result launcher for selecting the photo
        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                dialog.setTitle("Uploading photo");
                dialog.setMessage("Please wait");
                dialog.show();

                uploadFile(result);
            }
        });

        // select photo from local storage
        binding.addPhotoButton.setOnClickListener(v -> {
            launcher.launch("image/*");
        });

        // updating username
        binding.updateProfileButton.setOnClickListener(v -> {
            String str = binding.titleED.getText().toString();

            if(!str.isEmpty()){
                viewModel.updateUsername(str);

                Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditProfileActivity.this, DashboardActivity.class));
            }else{
                Snackbar.make(binding.getRoot(), "Please type something", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadFile(Uri uri) {
        viewModel.repository.storage.getReference().child("users").child(auth.getUid()).putFile(uri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        storage.getReference().child("users").child(auth.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();

                                currentUser.setImage_url(url);

                                database.getReference().child("users").child(auth.getUid())
                                        .child("details").setValue(currentUser);

                                dialog.dismiss();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "There was a problem uploading the file", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
