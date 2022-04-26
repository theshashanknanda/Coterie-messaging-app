package com.project.coterie2.view.chat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.project.coterie2.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.project.coterie2.databinding.UserLayoutBinding;
import com.project.coterie2.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private Context context;
    private String adminId;

    public UserAdapter(Context context, List<User> userList, String adminId){
        this.context = context;
        this.userList = userList;
        this.adminId = adminId;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        UserLayoutBinding binding = UserLayoutBinding.inflate(inflater, parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        Glide.with(context).load(user.getImage_url()).placeholder(R.drawable.ic_coterie_vector_man1).into(holder.binding.imageView);
        holder.binding.usernameTV.setText(user.getName());

        if(user.getUid().equals(adminId)){
            holder.binding.userRoleTV.setText("Admin");
        }else{
            holder.binding.userRoleTV.setText("Member");
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public UserLayoutBinding binding;
        public UserViewHolder(@NonNull UserLayoutBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
