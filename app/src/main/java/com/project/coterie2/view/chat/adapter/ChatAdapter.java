package com.project.coterie2.view.chat.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;

import com.project.coterie2.databinding.ChatRowBinding;
import com.project.coterie2.model.MessageModel;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<MessageModel> modelList;
    private Context context;

    private int SENDER_VIEWTYPE = 1;
    private int RECIEBER_VIEWTYPE = 1;

    public ChatAdapter(List<MessageModel> modelList, Context context){
        this.modelList = modelList;
        this.context = context;
    }
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ChatRowBinding binding = ChatRowBinding.inflate(inflater, parent, false);

        return new ChatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        MessageModel model = modelList.get(position);
        holder.binding.senderTV.setText(model.getSenderName());
        holder.binding.messageTV.setText(model.getMessage());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public ChatRowBinding binding;
        public ChatViewHolder(ChatRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
