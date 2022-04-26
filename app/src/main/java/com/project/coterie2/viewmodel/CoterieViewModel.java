package com.project.coterie2.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.coterie2.model.Community;
import com.project.coterie2.model.MessageModel;
import com.project.coterie2.model.Repository;
import com.project.coterie2.model.User;

import java.util.List;

public class CoterieViewModel extends AndroidViewModel {
    private Context context;
    public Repository repository;
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
    public MutableLiveData<List<User>> currentCommunityUsers;
    public MutableLiveData<List<String>> currentCommunityUsersIds;

    public CoterieViewModel(@NonNull Application application) {
        super(application);
        this.context = application;
        repository = new Repository(context);
        currentUserData = repository.currentUserData;
        currentUserID = repository.currentUserID;
        currentUserCommunities = repository.currentUserCommunities;
        currentUserCommunitiesIDs = repository.currentUserCommunitiesIDs;
        allCommunities = repository.allCommunities;
        allCommunitiesIDs = repository.allCommunitiesIDs;
        isCurrentUserInCommunity = repository.isCurrentUserInCommunity;
        isThereNoCommunity = repository.isThereNoCommunity;
        currentCommunityChat = repository.currentCommunityChat;
        isThereNoChat = repository.isThereNoChat;
        currentCommunityUsers = repository.currentCommunityUsers;
        currentCommunityUsersIds = repository.currentCommunityUsersIds;
    }

    public boolean sendEmailVerification(){
        return repository.sendVerificationEmail();
    }

    public boolean checkForVerification(){
        return repository.checkForVerification();
    }

    public void saveUserData(String name, String email, String password){
        repository.saveUserInRealTimeDatabase(name, email, password);
    }

    public void signOut(){
        repository.signOut();
    }

    public void updateUsername(String str){
        repository.updateUsername(str);
    }

    public void createCommunity(Community community){
        repository.createCommunity(community);
    }

    public void getUserCommunities(){
        repository.getUserCommunities();
    }

    public void getALlCommunities(){
        repository.getAllCommunities();
    }

    public void joinUser(String communityID){
        repository.joinUser(communityID);
    }

    public void currentUserInCommunity(String communityID){
        repository.currentUserInCommunity(communityID);
    }

    public void sendMessage(MessageModel messageModel, String communityID){
        repository.sendMessage(messageModel, communityID);
    }

    public void getAllMessages(String communityID){
        repository.getAllMessages(communityID);
    }

    public void communityUsers(String communityID){
        repository.communityUsers(communityID);
    }

    public void leaveCommunity(String communityId){
        repository.leaveCommunity(communityId);
    }
}
