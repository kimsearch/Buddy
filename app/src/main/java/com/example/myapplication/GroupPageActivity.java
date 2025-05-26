package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupPageActivity extends AppCompatActivity {

    private AppCompatImageButton notificationBtn, navHome, navSearch, navPet, navMyPage;
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private List<Group> groupList;
    private Button createRoomBtn;
    private Long myMemberId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_page);

        myMemberId = getMyMemberId();

        initViews();
        setupRecyclerView();
        setupListeners();
    }

    private Long getMyMemberId() {
        return getSharedPreferences("loginPrefs", MODE_PRIVATE)
                .getLong("memberId", -1L);
    }

    private void initViews() {
        notificationBtn = findViewById(R.id.notification_button);
        navHome = findViewById(R.id.nav_home);
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);
        recyclerView = findViewById(R.id.group_recycler_view);
        createRoomBtn = findViewById(R.id.create_room_button);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
        api.getMyGroups(myMemberId).enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    groupList = response.body();
                    groupAdapter = new GroupAdapter(GroupPageActivity.this, groupList, myMemberId);
                    recyclerView.setAdapter(groupAdapter);
                } else {
                    // 에러 처리: 서버는 연결됐지만 결과 없음
                    Toast.makeText(GroupPageActivity.this, "그룹 목록을 불러오지 못했습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                // 에러 처리: 서버 연결 실패
                Toast.makeText(GroupPageActivity.this, "서버 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private List<Group> getGroupList() {
        List<Group> list = new ArrayList<>();
        list.add(new Group(1L, "하루 만보 걷기 챌린지", 18L));
        list.add(new Group(2L, "같이 다이어트 해요", 19L));
        list.add(new Group(3L, "버디퀘스트 화이팅", 20L));
        return list;
    }

    private void setupListeners() {
        notificationBtn.setOnClickListener(v -> navigateTo(AlarmPageActivity.class));

        navHome.setOnClickListener(v -> navigateTo(MainActivity.class));
        navSearch.setOnClickListener(v -> navigateTo(GroupSearchPageActivity.class));
        navPet.setOnClickListener(v -> navigateTo(PetActivity.class));
        navMyPage.setOnClickListener(v -> navigateTo(MyPageMainActivity.class));
        createRoomBtn.setOnClickListener(v -> navigateTo(GroupMakeActivity.class));
    }

    private void navigateTo(Class<?> targetActivity) {
        startActivity(new Intent(this, targetActivity));
    }
}
