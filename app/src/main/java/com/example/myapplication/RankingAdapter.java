package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private final List<RankingItem> rankingList;
    private final Context context;

    public RankingAdapter(Context context, List<RankingItem> rankingList) {
        this.context = context;
        this.rankingList = rankingList;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ranking, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        RankingItem item = rankingList.get(position);

        // ✅ 순위 텍스트 + 메달
        switch (position) {
            case 0:
                holder.rankText.setText("🥇 1위");
                holder.rankText.setTextColor(Color.parseColor("#FFD700")); // 금색
                break;
            case 1:
                holder.rankText.setText("🥈 2위");
                holder.rankText.setTextColor(Color.parseColor("#C0C0C0")); // 은색
                break;
            case 2:
                holder.rankText.setText("🥉 3위");
                holder.rankText.setTextColor(Color.parseColor("#CD7F32")); // 동색
                break;
            default:
                holder.rankText.setText((position + 1) + "위");
                holder.rankText.setTextColor(Color.parseColor("#2A3D45"));
                break;
        }

        // ✅ 닉네임
        holder.userName.setText(item.getNickname() != null ? item.getNickname() : "익명");

        // ✅ 진행률 (성공 횟수 기준)
        int progress = Math.max(0, Math.min(item.getSuccessCount(), 100));
        holder.progressBar.setProgress(progress);

        // ✅ 프로필 이미지 (기본 이미지)
        holder.profileImage.setImageResource(R.drawable.ic_profile);
    }

    @Override
    public int getItemCount() {
        // ✅ 최대 3명까지만 표시
        return Math.min(rankingList.size(), 3);
    }

    // ✅ ViewHolder 내부 클래스
    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView rankText, userName;
        ProgressBar progressBar;
        ImageView profileImage;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            rankText = itemView.findViewById(R.id.rankText);
            userName = itemView.findViewById(R.id.userName);
            progressBar = itemView.findViewById(R.id.progressBar);
            profileImage = itemView.findViewById(R.id.profileImage);
        }
    }
}
