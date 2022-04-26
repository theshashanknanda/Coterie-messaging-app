package com.project.coterie2.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class Repository {
    public FirebaseAuth auth;
    public FirebaseUser user;
    public FirebaseDatabase database;
    public FirebaseStorage storage;

    public MutableLiveData<User> currentUserData;
    public MutableLiveData<String> currentUserID;
    public MutableLiveData<List<Community>> currentUserCommunities;
    public MutableLiveData<List<String>> currentUserCommunitiesIDs;
    public MutableLiveData<List<Community>> allCommunities;
    public MutableLiveData<List<String>> allCommunitiesIDs;
    public MutableLiveData<Boolean> isCurrentUserInCommunity;
    public MutableLiveData<String> isThereNoCommunity;
    public MutableLiveData<List<MessageModel>> currentCommunityChat;
    public MutableLiveData<String> isThereNoChat;
    public MutableLiveData<List<String>> currentCommunityUsersIds;
    public MutableLiveData<List<User>> currentCommunityUsers;

    public ValueEventListener listener2;
    public ValueEventListener listener1;
    public ValueEventListener listener3;

    private boolean verifyFlag;
    boolean isJoined;
    private final Context context;

    public Repository(Context context) {
        this.context = context;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        currentUserData = new MutableLiveData<>();
        currentUserID = new MutableLiveData<>();
        storage = FirebaseStorage.getInstance();
        currentUserCommunities = new MutableLiveData<>();
        currentUserCommunitiesIDs = new MutableLiveData<>();
        allCommunities = new MutableLiveData<>();
        allCommunitiesIDs = new MutableLiveData<>();
        isCurrentUserInCommunity = new MutableLiveData<>();
        isThereNoCommunity = new MutableLiveData<>();
        currentCommunityChat = new MutableLiveData<>();
        isThereNoChat = new MutableLiveData<>();
        currentCommunityUsers = new MutableLiveData<>();
        currentCommunityUsersIds = new MutableLiveData<>();

        if (user != null) {
            // getting current user data in mutable live data
            database.getReference().child("users").child(user.getUid()).child("details").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    currentUserData.postValue(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("coterie_error", "not getting data");
                }
            });
            currentUserID.postValue(user.getUid());

            getUserCommunities();
        }
    }

    public void saveUserInRealTimeDatabase(String name, String email, String password) {
        user = auth.getCurrentUser();

        User userToBeSaved = new User();
        userToBeSaved.setName(name);
        userToBeSaved.setEmail(email);
        userToBeSaved.setPassword(password);
        userToBeSaved.setImage_url("");
        userToBeSaved.setUid(user.getUid());
        database.getReference().child("users").child(user.getUid()).child("details").setValue(userToBeSaved)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Thank you for registering", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("coterie_error", e.getLocalizedMessage() + " saving user");
                        Toast.makeText(context, "There was a error during saving data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public boolean sendVerificationEmail() {
        if (!user.isEmailVerified()) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                verifyFlag = true;
                            } else {
                                Log.d("coterie_error", task.getException().getLocalizedMessage() + " from send email");
                                verifyFlag = false;
                            }
                        }
                    });
        } else {
            verifyFlag = true;
        }

        return verifyFlag;
    }

    public boolean checkForVerification() {
        user = auth.getCurrentUser();
        database.getReference().child("users").child(user.getUid()).child("details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return user.isEmailVerified();
    }

    public void signOut() {
        auth.signOut();
    }

    public void updateUsername(String str) {
        currentUserData.getValue().setName(str);
        database.getReference().child("users").child(auth.getUid()).child("details").setValue(currentUserData.getValue());
    }

    public void createCommunity(Community community) {
        String communityKey = database.getReference().child("communities").child("list")
                .push().getKey();

        // create a community entry using community key
        database.getReference().child("communities").child("list").child(communityKey).setValue(community)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // now add user in the community
                            database.getReference().child("communities").child("members").child(communityKey).push().setValue(auth.getUid())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // now add community in user communities
                                                database.getReference().child("users").child(auth.getUid()).child("communities").push().setValue(communityKey)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(context, "Community created successfully", Toast.LENGTH_SHORT).show();
                                                                getUserCommunities();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public void getUserCommunities() {
        // getting current user communities
        ArrayList<String> userIDList = new ArrayList<>();
        database.getReference().child("users").child(user.getUid()).child("communities").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userIDList.clear();
                        ArrayList arrayList = new ArrayList();
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                String communityID = snapshot1.getValue(String.class);
                                userIDList.add(communityID);
                                currentUserCommunitiesIDs.setValue(userIDList);

                                // get community info from communityID
                                database.getReference().child("communities").child("list").child(communityID)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Community community = snapshot.getValue(Community.class);
                                                arrayList.add(community);
                                                currentUserCommunities.setValue(arrayList);

                                                Log.d("coterie_error", currentUserCommunities.getValue().toString());
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }else{
                            // TODO
                            isThereNoCommunity.setValue("yes");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void getAllCommunities() {
        List<Community> allCommunitiesList = new ArrayList<>();
        List<String> allCommunityIDs = new ArrayList<>();
        database.getReference().child("communities").child("list").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    // getting community
                    Community community = snapshot1.getValue(Community.class);
                    allCommunitiesList.add(community);
                    allCommunities.setValue(allCommunitiesList);

                    // getting community ID
                    allCommunityIDs.add(snapshot1.getKey());
                    allCommunitiesIDs.setValue(allCommunityIDs);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void currentUserInCommunity(String communityID){
        database.getReference().child("users").child(user.getUid()).child("communities").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        String id = snapshot1.getValue(String.class);

                        Log.d("coterie_error", id + " " + communityID);
                        if(id.equals(communityID)){
                            isJoined = true;
                            Log.d("coterie_error", communityID);
                            Log.d("coterie_error", "In Community");
                        }
                    }
                    isCurrentUserInCommunity.setValue(isJoined);
                }else{
                    isCurrentUserInCommunity.setValue(false);
                    Log.d("coterie_error", "Not In Community");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void joinUser(String communityID){
        // add community in user
        database.getReference().child("users").child(user.getUid()).child("communities").push().setValue(communityID)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // add user in community
                        database.getReference().child("communities").child("members")
                                .child(communityID).push().setValue(user.getUid())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("cerror", "User joined community- " + communityID);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("coterie_error", e.getLocalizedMessage());
                    }
                });
    }

    public void sendMessage(MessageModel messageModel, String communityID) {
        database.getReference().child("communities").child("chats").child(communityID).push().setValue(messageModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("coterie_error", "Chat sent");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("coterie_error", "Chat not sent");
                    }
                });
    }

    public void getAllMessages(String communityID){
        Log.d("coterie_error", "current_com " + communityID);
        currentCommunityChat.setValue(new ArrayList<>());
        database.getReference().child("communities").child("chats").child(communityID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            // has previous messages
                            List<MessageModel> newList = new ArrayList<>();
                            for(DataSnapshot msg: snapshot.getChildren()) {
                                MessageModel messageModel = msg.getValue(MessageModel.class);
                                newList.add(messageModel);
                            }

                            currentCommunityChat.setValue(newList);
                        }else {
                            // no previous message
                            isThereNoChat.setValue("positive");
                            List<MessageModel> models = currentCommunityChat.getValue();
                            models.clear();
                            currentCommunityChat.setValue(models);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void communityUsers(String communityID){
        List<User> newList = new ArrayList<>();
        List<String> newListIds = new ArrayList<>();

        currentCommunityUsers.setValue(newList);
        currentUserCommunitiesIDs.setValue(newListIds);

        database.getReference().child("communities").child("members").child(communityID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    newList.clear();
                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        String uid = snapshot1.getValue(String.class);
                        newListIds.add(uid);
                        currentCommunityUsersIds.setValue(newListIds);
                        Log.d("coterie_error", "User Id- " + uid);

                        Log.d("coterie_error", uid);

                        database.getReference().child("users").child(uid).child("details").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);

                                Log.d("coterie_error", "from user- " + user.toString());
                                // add user in list
                                newList.add(user);
                                currentCommunityUsers.setValue(newList);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void leaveCommunity(String communityID){
        // remove user from community

        listener2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    String comId = snapshot1.getValue(String.class);
                    if(comId.equals(communityID)){
                        snapshot1.getRef().removeValue();

                        Log.d("coterie_error", "User left community- " + communityID);

                        removeListeners(communityID);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        listener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        String uid = snapshot1.getValue(String.class);

                        if(uid.equals(user.getUid())){
                            snapshot1.getRef().removeValue();
                        }
                    }

                    database.getReference().child("users").child(user.getUid()).child("communities")
                            .addValueEventListener(listener2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        listener3 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Community community = snapshot.getValue(Community.class);
                String adminId = community.getAdminID();
                if(!adminId.equals(user.getUid())){
                    database.getReference().child("communities").child("members").child(communityID).addValueEventListener(listener1);
                }else{
                    Toast.makeText(context, "Admin cannot leave the community", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        database.getReference().child("communities").child("list").child(communityID).addValueEventListener(listener3);
    }

    private void removeListeners(String communityID) {
        database.getReference().child("communities").child("members").child(communityID).removeEventListener(listener1);
        database.getReference().child("users").child(user.getUid()).child("communities").removeEventListener(listener2);
        database.getReference().child("communities").child("list").child(communityID).removeEventListener(listener3);
    }
}
