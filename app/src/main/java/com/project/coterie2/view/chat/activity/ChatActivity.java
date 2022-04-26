package com.project.coterie2.view.chat.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.project.coterie2.R;
import com.project.coterie2.databinding.ActivityChatBinding;
import com.project.coterie2.model.MessageModel;
import com.project.coterie2.view.chat.adapter.ChatAdapter;
import com.project.coterie2.view.dashboard.activity.VerificationActivity;
import com.project.coterie2.viewmodel.CoterieViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private CoterieViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(CoterieViewModel.class);
        getSupportActionBar().hide();

        String name = getIntent().getStringExtra("name");
        String imageURL = getIntent().getStringExtra("image_url");
        String adminID = getIntent().getStringExtra("adminID");
        String communityID = getIntent().getStringExtra("communityID");
        setToolBar(name, imageURL);

        Log.d("coterie_error", communityID);

        viewModel.currentCommunityChat.setValue(new ArrayList<>());
        viewModel.currentCommunityUsers.setValue(new ArrayList<>());

        binding.backArrow.setOnClickListener(v -> {
            finish();
        });

        binding.toolbarDetails.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatDetails.class);
            intent.putExtra("name", name);
            intent.putExtra("image_url", imageURL);
            intent.putExtra("adminID", adminID);
            intent.putExtra("communityID", communityID);
            startActivity(intent);
        });

        binding.dropButton.setOnClickListener(v -> {
            viewModel.checkForVerification();
            if(viewModel.checkForVerification()){
                // user is verified
                String msg = binding.chatED.getText().toString();

                if(!msg.isEmpty()){
                    String uid = viewModel.currentUserID.getValue();
                    String username = viewModel.currentUserData.getValue().getName();

                    MessageModel model = new MessageModel(uid, username, msg);

                    viewModel.sendMessage(model, communityID);
                }

//                binding.recyclerView.smoothScrollToPosition(binding.recyclerView.getAdapter().getItemCount());
            }else{
                // user is not verified
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Your account is not verified yet");
                builder.setMessage("Verify your account to start sending messages in the community");
                builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(ChatActivity.this, VerificationActivity.class));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        
        getAllMessages(communityID);
    }

    private void getAllMessages(String communityID) {
        viewModel.getAllMessages(communityID);
        viewModel.currentCommunityChat.observe(this, new Observer<List<MessageModel>>() {
            @Override
            public void onChanged(List<MessageModel> messageModels) {
                List<MessageModel> modelList = new ArrayList<>();
                modelList = messageModels;
                modelList = reverseArrayList(modelList);

                ChatAdapter adapter = new ChatAdapter(modelList, ChatActivity.this);
                binding.recyclerView.setAdapter(adapter);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this, RecyclerView.VERTICAL, true));
//                binding.recyclerView.smoothScrollToPosition(adapter.getItemCount());

                binding.chatED.setText("");
            }
        });
    }

    public List<MessageModel> reverseArrayList(List<MessageModel> alist)
    {
        // Arraylist for storing reversed elements
        List<MessageModel> revArrayList = new ArrayList<>();
        for (int i = alist.size() - 1; i >= 0; i--) {

            // Append the elements in reverse order
            revArrayList.add(alist.get(i));
        }

        // Return the reversed arraylist
        return revArrayList;
    }

    // Iterate through all the elements and print
    public void printElements(ArrayList<Integer> alist)
    {
        for (int i = 0; i < alist.size(); i++) {
            System.out.print(alist.get(i) + " ");
        }
    }

    private void setToolBar(String name, String imageURL) {
        binding.communityTV.setText(name);
        Glide.with(this).load(imageURL).placeholder(R.drawable.ic_choosephoto).into(binding.communityImageView);
        Log.d("coterie_error",  "From chat " + imageURL);
    }
}
