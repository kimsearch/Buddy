package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupSearchAdapter extends RecyclerView.Adapter<GroupSearchAdapter.ViewHolder> {
    private List<GroupSearchResponse> groupList;
    private OnGroupClickListener listener;

    public interface OnGroupClickListener {
        void onClick(GroupSearchResponse group);
    }

    public GroupSearchAdapter(List<GroupSearchResponse> groupList, OnGroupClickListener listener) {
        this.groupList = groupList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupSearchResponse group = groupList.get(position);
        holder.groupName.setText(group.getName());
        holder.memberCount.setText("인원수: " + group.getMemberCount());
        holder.itemView.setOnClickListener(v -> listener.onClick(group));
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupName, memberCount;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.search_group_name_1);
            memberCount = itemView.findViewById(R.id.search_group_member_count);
            imageView = itemView.findViewById(R.id.profile_image);
        }
    }
}

