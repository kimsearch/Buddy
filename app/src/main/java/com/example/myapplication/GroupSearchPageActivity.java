package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
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
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        // 기본 UI 연결
        searchGroupInput = findViewById(R.id.search_group_input_1);
        searchButton = findViewById(R.id.search_group_input_button);
        categoryTextView = findViewById(R.id.category);

        recyclerView = findViewById(R.id.group_search_recycler);
        adapter = new GroupSearchAdapter(groupList, group -> {
            // 클릭 시 이동 분기
            if (group.isJoined()) {
                String category = group.getCategory();
                Intent intent;

                if ("만보기".equals(category)) {
                    intent = new Intent(this, GroupMainStepActivity.class);
                } else if ("섭취 칼로리".equals(category)) {
                    intent = new Intent(this, GroupMainIntakeActivity.class);
                } else if ("운동 칼로리".equals(category)) {
                    intent = new Intent(this, GroupMainBurnedActivity.class);
                } else if ("몸무게".equals(category)) {
                    intent = new Intent(this, GroupMainWeightActivity.class);
                } else if ("학습 시간".equals(category)) {
                    intent = new Intent(this, GroupMainStudyTimeActivity.class);
                } else if ("문제 풀이 수".equals(category)) {
                    intent = new Intent(this, GroupMainStudyProgressActivity.class);
                } else if ("복습 체크".equals(category)) {
                    intent = new Intent(this, GroupMainReviewCheckActivity.class);
                } else if ("목표 점수".equals(category)) {
                    intent = new Intent(this, GroupMainGoalScoreActivity.class);
                } else if ("목표 권수".equals(category)) {
                    intent = new Intent(this, GroupMainGoalBooksActivity.class);
                } else if ("목표 시간".equals(category)) {
                    intent = new Intent(this, GroupMainGoalMinutesActivity.class);
                } else if ("읽은 시간".equals(category)) {
                    intent = new Intent(this, GroupMainTimeLogActivity.class);
                } else {
                    intent = new Intent(this, GroupMainActivity.class);
                }

                intent.putExtra("groupId", group.getId());
                intent.putExtra("groupName", group.getName());
                intent.putExtra("category", group.getCategory());
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, SearchJoinActivity.class);
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

        // 하단 네비게이션 연결
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

        // ✅ 각 메인 카테고리 버튼 연결
        findViewById(R.id.category_btn_1).setOnClickListener(v -> showDietSubCategoryPopup());
        findViewById(R.id.category_btn_2).setOnClickListener(v -> showFinanceSubCategoryPopup());
        findViewById(R.id.category_btn_3).setOnClickListener(v -> showStudySubCategoryPopup());
        findViewById(R.id.category_btn_4).setOnClickListener(v -> showReadingSubCategoryPopup());
    }

    // ✅ 다이어트 카테고리
    private void showDietSubCategoryPopup() {
        String[] subCategories = {"만보기", "섭취 칼로리", "운동 칼로리", "몸무게"};
        selectedMainCategory = "다이어트";
        showSubCategoryDialog("다이어트 - 세부 카테고리 선택", subCategories);
    }

    // ✅ 재테크 카테고리
    private void showFinanceSubCategoryPopup() {
        String[] subCategories = {"저축", "소비", "가계부", "부수입"};
        selectedMainCategory = "재테크";
        showSubCategoryDialog("재테크 - 세부 카테고리 선택", subCategories);
    }

    // ✅ 공부 카테고리
    private void showStudySubCategoryPopup() {
        String[] subCategories = {"학습 시간", "문제 풀이 수", "복습 체크", "목표 점수"};
        selectedMainCategory = "공부";
        showSubCategoryDialog("공부 - 세부 카테고리 선택", subCategories);
    }

    // ✅ 독서 카테고리
    private void showReadingSubCategoryPopup() {
        String[] subCategories = {"목표 권수", "목표 시간", "읽은 시간"};
        selectedMainCategory = "독서";
        showSubCategoryDialog("독서 - 세부 카테고리 선택", subCategories);
    }

    // ✅ 공통 카테고리 팝업
    private void showSubCategoryDialog(String title, String[] subCategories) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        builder.setItems(subCategories, (dialog, which) -> {
            selectedSubCategory = subCategories[which];
            categoryTextView.setText("선택된 카테고리: " + selectedMainCategory + " - " + selectedSubCategory);

            String query = searchGroupInput.getText().toString().trim();
            searchGroups(query, selectedMainCategory, selectedSubCategory);
        });

        builder.show();
    }

    private Long getMyMemberId() {
        return getSharedPreferences("loginPrefs", MODE_PRIVATE).getLong("memberId", -1L);
    }

    // ✅ 그룹 검색 API
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
                            adapter.notifyDataSetChanged();

                            Log.d("GroupSearch", "검색 결과 개수: " + groupList.size());
                        } else {
                            Toast.makeText(GroupSearchPageActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<GroupSearchResponse>> call, Throwable t) {
                        Toast.makeText(GroupSearchPageActivity.this, "서버 연결 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
