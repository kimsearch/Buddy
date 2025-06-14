package com.example.myapplication; // 패키지명은 프로젝트에 맞게 수정

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GroupMemberActivity extends AppCompatActivity {

    private LinearLayout memberListContainer;
    private ImageButton refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_member);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish(); //
        });

        memberListContainer = findViewById(R.id.member_recycler_view);
        refreshButton = findViewById(R.id.member_refresh_button);

        // 초기 멤버 리스트 추가
        loadMemberList();

        // 새로고침 버튼 클릭 시 리스트 갱신
        refreshButton.setOnClickListener(v -> {
            memberListContainer.removeAllViews(); // 기존 리스트 삭제
            loadMemberList(); // 새로 로딩
        });
    }

    // 멤버 데이터 불러와서 리스트에 추가
    private void loadMemberList() {
        String[] memberNames = {"홍길동", "김철수", "이영희", "박민수"};

        for (String name : memberNames) {
            addMemberItem(name);
        }
    }

    // 멤버 항목 하나씩 추가
    private void addMemberItem(String name) {
        LinearLayout memberItem = new LinearLayout(this);
        memberItem.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        memberItem.setOrientation(LinearLayout.HORIZONTAL);
        memberItem.setPadding(12, 12, 12, 12);
        memberItem.setBackgroundColor(getResources().getColor(android.R.color.white));
        memberItem.setElevation(2);
        memberItem.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // 아이콘 버튼
        ImageButton icon = new ImageButton(this);
        icon.setLayoutParams(new LinearLayout.LayoutParams(100, 100)); // 크기 조절
        icon.setImageResource(R.drawable.ic_home);
        icon.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);

        // 이름 텍스트
        TextView nameText = new TextView(this);
        nameText.setText(name);
        nameText.setTextSize(18f);
        nameText.setTextColor(getResources().getColor(R.color.black));
        nameText.setPadding(24, 0, 0, 0);

        // 레이아웃에 추가
        memberItem.addView(icon);
        memberItem.addView(nameText);
        memberListContainer.addView(memberItem);
    }
}
