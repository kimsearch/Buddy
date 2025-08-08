package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupSearchPageActivity extends AppCompatActivity {

    private EditText searchGroupInput;
    private AppCompatImageButton searchButton;
    private TextView categoryTextView;
    private String selectedMainCategory = "";
    private String selectedSubCategory = "";


    private ImageButton navHome, navGroup, navSearch, navPet, navMyPage;

    private RecyclerView recyclerView;
    private GroupSearchAdapter adapter;
    private List<GroupSearchResponse> groupList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_search_page);

        // 하단 네비게이션
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navMyPage = findViewById(R.id.nav_mypage);
        navPet = findViewById(R.id.nav_pet);
        navSearch = findViewById(R.id.nav_search);

        // UI 연결
        searchGroupInput = findViewById(R.id.search_group_input_1);
        TextView groupSearchTitle = findViewById(R.id.group_search_title);
        searchButton = findViewById(R.id.search_group_input_button);
        categoryTextView = findViewById(R.id.category);

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.group_search_recycler);
        adapter = new GroupSearchAdapter(groupList, group -> {
            if (group.isJoined()) {
                // ✅ 이미 참여한 그룹이면 카테고리에 따라 분기
                String category = group.getCategory();
                Intent intent;
                if ("만보기".equals(category)) {
                    intent = new Intent(GroupSearchPageActivity.this, GroupMainStepActivity.class); // group_main_step.xml
                } else {
                    intent = new Intent(GroupSearchPageActivity.this, GroupMainActivity.class); // group_main.xml
                }
                intent.putExtra("groupId", group.getId());
                intent.putExtra("groupName", group.getName());
                intent.putExtra("category", category);
                startActivity(intent);
            } else {
                // ❗ 참여하지 않은 그룹이면 참가 요청 화면으로 이동
                Intent intent = new Intent(GroupSearchPageActivity.this, SearchJoinActivity.class);
                intent.putExtra("groupId", group.getId());
                intent.putExtra("groupName", group.getName());
                intent.putExtra("category", group.getCategory());
                intent.putExtra("startDate", group.getStartDate());
                intent.putExtra("memberCount", group.getMemberCount());
                intent.putExtra("description", group.getDescription());
                intent.putExtra("leaderNickname", group.getLeaderNickname());
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 하단 네비게이션 클릭 이벤트
        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));

        // 검색 버튼
        searchButton.setOnClickListener(v -> {
            String query = searchGroupInput.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
            } else {
                searchGroups(query, selectedMainCategory, selectedSubCategory);
            }
        });

        // 카테고리 선택 팝업
        findViewById(R.id.category_btn_1).setOnClickListener(v -> showCategoryPopup());
        findViewById(R.id.category_btn_2).setOnClickListener(v -> showFinanceSubCategoryPopup());
    }

    private Long getMyMemberId() {
        return getSharedPreferences("loginPrefs", MODE_PRIVATE).getLong("memberId", -1L);
    }

    private void searchGroups(String query, String categoryMain, String categorySub) {
        Long memberId = getMyMemberId();
        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);

        api.searchGroups(query, categoryMain, categorySub, memberId)
                .enqueue(new Callback<List<GroupSearchResponse>>() {
                    @Override
                    public void onResponse(Call<List<GroupSearchResponse>> call, Response<List<GroupSearchResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            groupList.clear();
                            groupList.addAll(response.body());

                            for (GroupSearchResponse group : response.body()) {
                                Log.d("GROUP", "받은 그룹: " + group.getName() + " → isJoined: " + group.isJoined());
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(GroupSearchPageActivity.this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<GroupSearchResponse>> call, Throwable t) {
                        Toast.makeText(GroupSearchPageActivity.this, "서버 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showCategoryPopup() {
        String[] subCategories = {"만보기", "섭취 칼로리", "운동 칼로리", "식단"};
        String categoryMain = "다이어트"; // 고정

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("다이어트 - 카테고리 선택");

        builder.setItems(subCategories, (dialog, which) -> {
            selectedMainCategory = categoryMain;
            selectedSubCategory = subCategories[which];

            // 선택된 카테고리 표시
            categoryTextView.setText("선택된 카테고리: " + selectedMainCategory + " - " + selectedSubCategory);

            // 🔥 선택 즉시 검색 수행
            String query = searchGroupInput.getText().toString().trim();
            searchGroups(query, selectedMainCategory, selectedSubCategory);
        });

        builder.show();
    }

    private void showFinanceSubCategoryPopup() {
        String[] subCategories = {"저축", "소비", "가계부", "부수입"};
        String categoryMain = "재테크"; //

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("재테크 - 카테고리 선택");

        builder.setItems(subCategories, (dialog, which) -> {
            selectedMainCategory = categoryMain;
            selectedSubCategory = subCategories[which];

            categoryTextView.setText("선택된 카테고리: " + selectedMainCategory + " - " + selectedSubCategory);

            String query = searchGroupInput.getText().toString().trim();
            searchGroups(query, selectedMainCategory, selectedSubCategory);
        });

        builder.show();
    }
}


