package com.project.coterie2.view.dashboard.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.project.coterie2.databinding.FragmentCommunityBinding;
import com.project.coterie2.model.Community;
import com.project.coterie2.model.User;
import com.project.coterie2.view.chat.activity.ChatActivity;
import com.project.coterie2.view.dashboard.adapters.CommunityAdapter;
import com.project.coterie2.view.dashboard.clickListeners.RecyclerViewClickListener;
import com.project.coterie2.viewmodel.CoterieViewModel;

import java.util.List;

public class CommunityFragment extends Fragment {
    private FragmentCommunityBinding binding;
    private CoterieViewModel viewModel;
    private List<String> communityIDs;
    private User currentUser;
    private String currentUID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCommunityBinding.inflate(getLayoutInflater());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        // start shimmer
        binding.shimmerLayout.startShimmer();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(CoterieViewModel.class);

        viewModel.isThereNoCommunity.observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.hideShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
            }
        });

        // getting current user communities
        viewModel.getUserCommunities();
        viewModel.currentUserCommunitiesIDs.observe(requireActivity(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> stringList) {
                communityIDs = stringList;
            }
        });

        viewModel.currentUserCommunities.observe(requireActivity(), new Observer<List<Community>>() {
            @Override
            public void onChanged(List<Community> communities) {
                // disabling shimmer layout
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.hideShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);

                RecyclerViewClickListener listener = new RecyclerViewClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        // onClick go to chat activity
                        Intent intent = new Intent(getContext(), ChatActivity.class);
                        Community community = communities.get(position);
                        intent.putExtra("name", community.getName());
                        intent.putExtra("image_url", community.getImageURL());
                        intent.putExtra("adminID", community.getAdminID());
                        intent.putExtra("communityID", communityIDs.get(position));
                        startActivity(intent);
                    }
                };

                CommunityAdapter adapter = new CommunityAdapter(getContext(), communities, communityIDs, listener);
                binding.recylerView.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.recylerView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }
        });

        // get current user data
        viewModel.currentUserData.observe(requireActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                currentUser = user;
            }
        });

        // get current user ID
        viewModel.currentUserID.observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentUID = s;
            }
        });
    }
}