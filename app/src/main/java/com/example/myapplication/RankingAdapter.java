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

        holder.rankText.setText((position + 1) + "위");
        holder.userName.setText(item.getName());
        holder.progressBar.setProgress(item.getProgress());
        holder.profileImage.setImageResource(item.getProfileResId());

        switch (position) {
            case 0:
                holder.rankText.setTextColor(Color.parseColor("#FFD700")); break;
            case 1:
                holder.rankText.setTextColor(Color.parseColor("#C0C0C0")); break;
            case 2:
                holder.rankText.setTextColor(Color.parseColor("#CD7F32")); break;
            default:
                holder.rankText.setTextColor(Color.parseColor("#2A3D45")); break;
        }
    }

    @Override
    public int getItemCount() {
        return rankingList.size();
    }

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