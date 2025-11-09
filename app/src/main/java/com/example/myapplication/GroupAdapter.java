package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private final List<Group> groupList;
    private final Long myMemberId;
    private final Context context;

    public interface OnDeleteClickListener {
        void onDelete(Group group);
    }

    private final OnDeleteClickListener deleteClickListener;

    // listener 포함 생성자
    public GroupAdapter(Context context, List<Group> groupList, Long myMemberId, OnDeleteClickListener listener) {
        this.context = context;
        this.groupList = groupList;
        this.myMemberId = myMemberId;
        this.deleteClickListener = listener;
    }

    // listener 없이 기본 생성자
    public GroupAdapter(Context context, List<Group> groupList, Long myMemberId) {
        this(context, groupList, myMemberId, null);
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

        // 그룹 이름 표시
        holder.groupNameButton.setText(group.getName());

        // 그룹장 여부 표시 (왕관/하트)
        if (Objects.equals(group.getLeaderId(), myMemberId)) {
            holder.roleIconButton.setImageResource(R.drawable.ic_crown);
        } else {
            holder.roleIconButton.setImageResource(R.drawable.ic_heart);
        }

        Log.d("GroupAdapter", "group: " + group.getName() +
                ", category: " + group.getCategory() +
                ", goalType: " + group.getGoalType());

        // ✅ 그룹 이름 버튼 클릭 → 각 카테고리별 메인화면 분기
        holder.groupNameButton.setOnClickListener(v -> {
            Intent intent;

            switch (group.getCategory()) {
                // ------------------------- 다이어트 -------------------------
                case "다이어트":
                    switch (group.getGoalType()) {
                        case "만보기":
                            intent = new Intent(context, GroupMainStepActivity.class);
                            break;
                        case "섭취 칼로리":
                            intent = new Intent(context, GroupMainIntakeActivity.class);
                            break;
                        case "운동 칼로리":
                            intent = new Intent(context, GroupMainBurnedActivity.class);
                            break;
                        case "몸무게":
                            intent = new Intent(context, GroupMainWeightActivity.class);
                            break;
                        default:
                            intent = new Intent(context, GroupMainActivity.class);
                            break;
                    }
                    break;

                // ------------------------- 재테크 -------------------------
                case "재테크":
                    switch (group.getGoalType()) {
                        case "부수입":
                            intent = new Intent(context, GroupMainSideHustleActivity.class);
                            break;
                        case "가계부":
                            intent = new Intent(context, GroupMainBudgetBookActivity.class);
                            break;
                        case "소비":
                            intent = new Intent(context, GroupMainSpendActivity.class);
                            break;
                        case "저축":
                            intent = new Intent(context, GroupMainSavingsActivity.class);
                            break;
                        default:
                            intent = new Intent(context, GroupMainActivity.class);
                            break;
                    }
                    break;

                // ------------------------- 공부 -------------------------
                case "공부":
                    switch (group.getGoalType()) {
                        case "학습 시간":
                            intent = new Intent(context, GroupMainStudyTimeActivity.class);
                            break;
                        case "문제 풀이 수":
                            intent = new Intent(context, GroupMainStudyProgressActivity.class);
                            break;
                        case "복습 체크":
                            intent = new Intent(context, GroupMainReviewCheckActivity.class);
                            break;
                        case "목표 점수":
                            intent = new Intent(context, GroupMainGoalScoreActivity.class);
                            break;
                        default:
                            intent = new Intent(context, GroupMainActivity.class);
                            break;
                    }
                    break;

                // ------------------------- 독서 -------------------------
                case "독서":
                    switch (group.getGoalType()) {
                        case "목표 권수":
                            intent = new Intent(context, GroupMainGoalBooksActivity.class);
                            break;
                        case "목표 시간":
                            intent = new Intent(context, GroupMainGoalMinutesActivity.class);
                            break;
                        case "읽은 시간":
                            intent = new Intent(context, GroupMainTimeLogActivity.class);
                            break;
                        default:
                            intent = new Intent(context, GroupMainActivity.class);
                            break;
                    }
                    break;

                // ------------------------- 기타 기본 -------------------------
                default:
                    intent = new Intent(context, GroupMainActivity.class);
                    break;
            }

            // ✅ 인텐트에 그룹 정보 전달
            intent.putExtra("groupId", group.getId());
            intent.putExtra("groupName", group.getName());
            intent.putExtra("groupGoal", group.getGoalType());
            intent.putExtra("memberId", myMemberId);
            intent.putExtra("leaderId", group.getLeaderId());

            context.startActivity(intent);
        });

        // ✅ 삭제 버튼 클릭 시
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDelete(group);
            } else {
                // 기본 삭제 로직: GroupExitActivity 이동
                Intent intent = new Intent(context, GroupExitActivity.class);
                intent.putExtra("groupId", group.getId());
                intent.putExtra("groupName", group.getName());
                context.startActivity(intent);
            }
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
            roleIconButton = itemView.findViewById(R.id.group_crown_button);
            groupNameButton = itemView.findViewById(R.id.group_goal_button_1);
            deleteButton = itemView.findViewById(R.id.delete_group_goals_button);
        }
    }
}
