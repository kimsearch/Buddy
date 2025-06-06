package com.example.myapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ViewHolder> {

    private final List<GroupMemberItem> memberList;

    public GroupMemberAdapter(List<GroupMemberItem> memberList) {
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_member_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupMemberItem member = memberList.get(position);
        holder.memberName.setText(member.getNickname());

        if (member.isLeader()) {
            holder.crownIcon.setVisibility(View.VISIBLE);
            Log.d("GroupMemberAdapter", member.getNickname() + " => 리더이므로 왕관 표시");
        } else {
            holder.crownIcon.setVisibility(View.GONE);
            Log.d("GroupMemberAdapter", member.getNickname() + " => 일반 멤버이므로 숨김");
        }
    }


    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView memberName;
        ImageView memberProfile;
        ImageView crownIcon; // 👑 왕관 아이콘

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.member_name);
            memberProfile = itemView.findViewById(R.id.member_profile);
            crownIcon = itemView.findViewById(R.id.crown_icon); // XML에 반드시 정의돼야 함
        }
    }
}
