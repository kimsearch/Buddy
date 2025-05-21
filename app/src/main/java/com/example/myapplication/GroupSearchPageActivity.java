package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.app.AlertDialog;

public class GroupSearchPageActivity extends AppCompatActivity {

    private EditText searchGroupInput;
    private AppCompatImageButton searchButton;
    private TextView categoryTextView;
    private Button groupRfpButton;

    private ImageButton navHome, navGroup, navSearch, navPet, navMyPage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_search_page);

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

        LinearLayout groupItemLayout = findViewById(R.id.group_item_layout);
        groupItemLayout.setOnClickListener(v -> {
            Intent intent = new Intent(GroupSearchPageActivity.this, SearchJoinActivity.class);
            startActivity(intent);
        });


        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(GroupSearchPageActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navGroup.setOnClickListener(v -> {
            Intent intent = new Intent(GroupSearchPageActivity.this, GroupPageActivity.class);
            startActivity(intent);
        });

        navSearch.setOnClickListener(v -> {
            Intent intent = new Intent(GroupSearchPageActivity.this, GroupSearchPageActivity.class);
            startActivity(intent);
        });

        navPet.setOnClickListener(v -> {
            Intent intent = new Intent(GroupSearchPageActivity.this, PetActivity.class);
            startActivity(intent);
        });

        navMyPage.setOnClickListener(v -> {
            Intent intent = new Intent(GroupSearchPageActivity.this, MyPageMainActivity.class);
            startActivity(intent);
        });

        // 검색 버튼 클릭 이벤트 처리
        searchButton.setOnClickListener(v -> {
            String query = searchGroupInput.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(GroupSearchPageActivity.this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
            } else {
                // 검색 기능 처리
                searchGroups(query);
            }
        });

        // 카테고리 선택 버튼 클릭 이벤트 (category_btn_1 버튼 클릭 시)
        findViewById(R.id.category_btn_1).setOnClickListener(v -> {
            showCategoryPopup();
        });
    }

    // 그룹 검색 처리 (기본적인 검색 예시)
    private void searchGroups(String query) {
        // 예시: 검색된 그룹을 확인할 수 있는 로직 추가
        Toast.makeText(this, query + "에 대한 검색 결과", Toast.LENGTH_SHORT).show();
    }

    // 카테고리 선택 팝업
    private void showCategoryPopup() {
        String[] items = {"만보기", "섭취 칼로리", "운동 칼로리", "식단"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("카테고리 선택");
        builder.setItems(items, (dialog, which) -> {
            // 선택된 카테고리 설정
            categoryTextView.setText("선택된 카테고리: " + items[which]);
        });
        builder.show();
    }
}
