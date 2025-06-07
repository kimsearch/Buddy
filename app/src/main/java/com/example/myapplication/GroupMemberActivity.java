package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupMemberActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GroupMemberAdapter memberAdapter;
    private ImageButton refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_member);

        recyclerView = findViewById(R.id.member_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        refreshButton = findViewById(R.id.member_refresh_button);
        refreshButton.setOnClickListener(v -> loadMemberList());

        loadMemberList(); // 초기 로딩
    }

    private void loadMemberList() {
        Long groupId = getIntent().getLongExtra("groupId", -1L);
        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);

        api.getGroupMembers(groupId).enqueue(new Callback<List<GroupMemberItem>>() {
            @Override
            public void onResponse(Call<List<GroupMemberItem>> call, Response<List<GroupMemberItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    memberAdapter = new GroupMemberAdapter(response.body());
                    recyclerView.setAdapter(memberAdapter);
                } else {
                    Toast.makeText(GroupMemberActivity.this, "멤버 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GroupMemberItem>> call, Throwable t) {
                Toast.makeText(GroupMemberActivity.this, "서버 오류", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
