package com.project.coterie2.view.dashboard.adapters;

import android.content.Context;
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

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{
    public List<Community> communities;
    public List<String> communityIDs;
    public Context context;
    public RecyclerViewClickListener listener;

    public SearchAdapter(Context context, List<Community> communities, List<String> communityIDs, RecyclerViewClickListener listener){
        this.context = context;
        this.communities = communities;
        this.communityIDs = communityIDs;
        this.listener = listener;
    }

    // change list for searching
    public void update(List<Community> communities, List<String> communityIDs){
        this.communities = communities;
        this.communityIDs = communityIDs;

        notifyDataSetChanged();
        Log.d("coterie_error", communities.toString());
    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        CommunityResourceBinding binding = CommunityResourceBinding.inflate(inflater, parent, false);
        return new SearchAdapter.SearchViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchViewHolder holder, int position) {
        Community community = communities.get(position);
        holder.binding.communityTitleTV.setText(community.getName());
        Glide.with(context).load(community.getImageURL()).placeholder(R.drawable.ic_choosephoto).into(holder.binding.imageView);

        holder.binding.lastSenderNameTV.setVisibility(View.INVISIBLE);
        holder.binding.lastSenderMessageTV.setVisibility(View.INVISIBLE);


        // onClick
        holder.itemView.setOnClickListener(v -> {
            if(listener != null){
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("coterie_error", String.valueOf(communities.size()));
        return communities.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        public CommunityResourceBinding binding;
        public SearchViewHolder(CommunityResourceBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
