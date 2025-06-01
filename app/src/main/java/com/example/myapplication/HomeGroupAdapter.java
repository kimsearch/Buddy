package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

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

        holder.groupNameBtn.setOnClickListener(v -> {

            context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putLong("groupId", group.getId())
                    .apply();
            Intent intent = new Intent(context, GroupMainActivity.class);

            // 그룹 정보 전달 (필요한 만큼)
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameBtn = itemView.findViewById(R.id.btnGroupName);
        }
    }
}
