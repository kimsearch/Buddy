package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeGroupAdapter extends RecyclerView.Adapter<HomeGroupAdapter.ViewHolder> {

    private final List<Group> groupList;
    private final Context context;
    private final Long myMemberId;

    public HomeGroupAdapter(Context context, List<Group> groupList, Long myMemberId) {
        this.context = context;
        this.groupList = groupList;
        this.myMemberId = myMemberId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_group_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.groupNameBtn.setText(group.getName());

        // 🔸 목표 달성률에 따라 아이콘 변경
        float percent = group.getProgressPercent();  // 0~100 사이 값이라고 가정
        if (percent < 25) {
            holder.progressIcon.setBackgroundResource(R.drawable.ic_progress_none);
        } else if (percent < 50) {
            holder.progressIcon.setBackgroundResource(R.drawable.ic_progress_quarter);
        } else if (percent < 100) {
            holder.progressIcon.setBackgroundResource(R.drawable.ic_progress_half);
        } else {
            holder.progressIcon.setBackgroundResource(R.drawable.ic_progress_full);
        }

        holder.groupNameBtn.setOnClickListener(v -> {
            // 조건 분기: 다이어트 && 만보기 → GroupMainStepActivity로 이동
            Intent intent;
            if ("다이어트".equals(group.getCategory()) && "만보기".equals(group.getGoalType())) {
                intent = new Intent(context, GroupMainStepActivity.class);
            } else {
                intent = new Intent(context, GroupMainActivity.class);
            }

            // groupId SharedPreferences 저장
            context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putLong("groupId", group.getId())
                    .apply();

            // 그룹 정보 전달
            intent.putExtra("groupId", group.getId());
            intent.putExtra("groupName", group.getName());
            intent.putExtra("leaderId", group.getLeaderId());
            intent.putExtra("myMemberId", myMemberId);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button groupNameBtn;
        View progressIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameBtn = itemView.findViewById(R.id.btnGroupName);
            progressIcon = itemView.findViewById(R.id.progressIcon); // XML에 반드시 id 부여!
        }
    }
}
