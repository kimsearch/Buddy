package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import com.example.myapplication.Group;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groupList;
    private Long myMemberId;
    private Context context;

    public interface OnDeleteClickListener {
        void onDelete(Group group);
    }

    private OnDeleteClickListener deleteClickListener;

    // 기존: listener 있는 생성자
    public GroupAdapter(Context context, List<Group> groupList, Long myMemberId, OnDeleteClickListener listener) {
        this.context = context;
        this.groupList = groupList;
        this.myMemberId = myMemberId;
        this.deleteClickListener = listener;
    }

    // ✅ 추가: listener 없는 생성자
    public GroupAdapter(Context context, List<Group> groupList, Long myMemberId) {
        this(context, groupList, myMemberId, null); // listener 없이 호출
    }


    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_row, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);

        // 그룹 이름 설정
        holder.groupNameButton.setText(group.getName());

        // 그룹장 여부에 따라 아이콘 변경
        if (Objects.equals(group.getLeaderId(), myMemberId)) {
            holder.roleIconButton.setImageResource(R.drawable.ic_crown);
        } else {
            holder.roleIconButton.setImageResource(R.drawable.ic_heart);
        }
        Log.d("GroupAdapter", "group: " + group.getName() +
                ", leaderId: " + group.getLeaderId() + ", myId: " + myMemberId);

        holder.groupNameButton.setOnClickListener(v -> {
            Intent intent;

            if ("다이어트".equals(group.getCategory()) && "만보기".equals(group.getGoalType())) {
                intent = new Intent(context, GroupMainStepActivity.class);
            } else if("재테크".equals(group.getCategory()) && "부수입".equals(group.getGoalType())) {
                intent = new Intent(context, GroupMainSideHustleActivity.class);
            } else if("재테크".equals(group.getCategory()) && "가계부".equals(group.getGoalType())) {
                intent = new Intent(context, GroupMainBudgetBookActivity.class);
            } else if("공부".equals(group.getCategory()) && "복습 체크".equals(group.getGoalType())) {
                intent = new Intent(context, GroupMainReviewCheckActivity.class);
            } else if("독서".equals(group.getCategory()) && "목표 권수".equals(group.getGoalType())) {
                intent = new Intent(context, GroupMainGoalBooksActivity.class);
            } else {
                intent = new Intent(context, GroupMainActivity.class);
            }

            // 그룹 정보 전달 (필요한 만큼)
            intent.putExtra("groupId", group.getId());
            intent.putExtra("groupName", group.getName());
            intent.putExtra("leaderId", group.getLeaderId());
            intent.putExtra("myMemberId", myMemberId);

            context.startActivity(intent);
        });

        // 삭제 버튼 클릭 시
        holder.deleteButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, GroupExitActivity.class);
            intent.putExtra("groupId", group.getId());
            intent.putExtra("groupName", group.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageButton roleIconButton;
        Button groupNameButton;
        ImageButton deleteButton;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            roleIconButton = itemView.findViewById(R.id.group_crown_button); // 혹은 group_icon_button
            groupNameButton = itemView.findViewById(R.id.group_goal_button_1);
            deleteButton = itemView.findViewById(R.id.delete_group_goals_button);
        }
    }
}