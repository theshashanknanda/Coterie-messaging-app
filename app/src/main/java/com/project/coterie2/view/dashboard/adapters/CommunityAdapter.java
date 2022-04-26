package com.project.coterie2.view.dashboard.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.coterie2.R;
import com.project.coterie2.databinding.CommunityResourceBinding;
import com.project.coterie2.model.Community;
import com.project.coterie2.view.dashboard.clickListeners.RecyclerViewClickListener;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {
    public List<Community> communities;
    public List<String> communityIDs;
    public Context context;
    public RecyclerViewClickListener listener;

    public CommunityAdapter(Context context, List<Community> communities, List<String> communityIDs, RecyclerViewClickListener listener){
        this.context = context;
        this.communities = communities;
        this.communityIDs = communityIDs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommunityAdapter.CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        CommunityResourceBinding binding = CommunityResourceBinding.inflate(inflater, parent, false);
        return new CommunityViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityAdapter.CommunityViewHolder holder, int position) {
        Community community = communities.get(position);
        holder.binding.communityTitleTV.setText(community.getName());
        Glide.with(context).load(community.getImageURL()).placeholder(R.drawable.ic_choosephoto).into(holder.binding.imageView);

        // call custom adapter
        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        Log.d("coterie_error", String.valueOf(communities.size()));
        return communities.size();
    }

    public class CommunityViewHolder extends RecyclerView.ViewHolder {
        public CommunityResourceBinding binding;
        public CommunityViewHolder(CommunityResourceBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
