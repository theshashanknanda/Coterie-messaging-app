package com.project.coterie2.view.dashboard.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.project.coterie2.R;
import com.project.coterie2.databinding.FragmentJoinNewBinding;
import com.project.coterie2.model.Community;
import com.project.coterie2.view.dashboard.adapters.CommunityAdapter;
import com.project.coterie2.view.dashboard.adapters.SearchAdapter;
import com.project.coterie2.view.dashboard.clickListeners.RecyclerViewClickListener;
import com.project.coterie2.viewmodel.CoterieViewModel;

import java.util.ArrayList;
import java.util.List;

public class JoinNewFragment extends Fragment {
    private FragmentJoinNewBinding binding;
    private CoterieViewModel viewModel;
    private List<Community> communitiesList;
    private List<String> communityIDsList;
    private SearchAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(CoterieViewModel.class);
        communitiesList = new ArrayList<>();
        communityIDsList = new ArrayList<>();
        // Inflate the layout for this fragment
        binding = FragmentJoinNewBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // getting all the communities in repository
        viewModel.getALlCommunities();
        viewModel.allCommunities.observe(requireActivity(), new Observer<List<Community>>() {
            @Override
            public void onChanged(List<Community> communities) {
                communitiesList = communities;
                Log.d("coterie_error", communitiesList.toString());
            }
        });
        viewModel.allCommunitiesIDs.observe(requireActivity(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> stringList) {
                communityIDsList = stringList;
            }
        });

        // search by text entered in edittext
        binding.titleED.addTextChangedListener(new AbsListView(getContext()) {
            @Override
            public ListAdapter getAdapter() {
                return null;
            }

            @Override
            public void setSelection(int i) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);

                List<Community> newList = new ArrayList<>();
                List<String> newIDs = new ArrayList<>();

                // recyclerview click
                RecyclerViewClickListener listener = new RecyclerViewClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        // check for user already joined
                        Log.d("coterie_error", "Item clicked");
                        viewModel.currentUserInCommunity(newIDs.get(position));
                        viewModel.isCurrentUserInCommunity.observe(requireActivity(), new Observer<Boolean>() {
                            @Override
                            public void onChanged(Boolean aBoolean) {
                                if(!aBoolean){
                                    // user is not in the community
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Confirm join this community");
                                    builder.setMessage("You are about to join the community");
                                    builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            viewModel.joinUser(newIDs.get(position));
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }else {
                                    // user is in the community
                                    Toast.makeText(getContext(), "You are in the community", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                };

                adapter = new SearchAdapter(getContext(), newList, communityIDsList, listener);

                for(int i = 0; i < communitiesList.size(); i++){
                    if(communitiesList.get(i).getName().toLowerCase().contains(s)){
                        newList.add(communitiesList.get(i));
                        newIDs.add(communityIDsList.get(i));
                    }
                }
                adapter.update(newList, communityIDsList);
                binding.recyclerView.setAdapter(adapter);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                if(!newList.isEmpty()){
                    binding.emptyImageView.setVisibility(INVISIBLE);
                }
            }
        });
    }
}