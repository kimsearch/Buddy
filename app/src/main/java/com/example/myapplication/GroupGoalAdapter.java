// GroupGoalAdapter.java
package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupGoalAdapter extends RecyclerView.Adapter<GroupGoalAdapter.GoalViewHolder> {

    private final Context context;
    private final List<String> goalTitles;         // 제목만 저장
    private final List<Integer> completionRates;   // 달성률만 저장

    public GroupGoalAdapter(Context context, List<String> goalTitles, List<Integer> completionRates) {
        this.context = context;
        this.goalTitles = goalTitles;
        this.completionRates = completionRates;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        String title = goalTitles.get(position);
        int rate = completionRates.get(position);

        holder.goalTitle.setText(title);

        int iconRes;
        if (rate >= 100) {
            iconRes = R.drawable.ic_progress_full;
        } else if (rate >= 50) {
            iconRes = R.drawable.ic_progress_half;
        } else if (rate >= 11) {
            iconRes = R.drawable.ic_progress_quarter;
        } else {
            iconRes = R.drawable.ic_progress_none;
        }

        holder.goalProgressIcon.setImageResource(iconRes);
    }

    @Override
    public int getItemCount() {
        return goalTitles.size(); // or completionRates.size(), 둘 다 동일 개수여야 함
    }

    public static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView goalTitle;
        ImageView goalProgressIcon;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            goalTitle = itemView.findViewById(R.id.goal_title);
            goalProgressIcon = itemView.findViewById(R.id.goal_progress_icon);
        }
    }
}