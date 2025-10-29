package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;

public class GroupMainGoalMinutesActivity extends AppCompatActivity {

    private TextView groupMainTitle;
    private TextView groupGoalTextView;

    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;
    private ImageButton backButton;

    private RecyclerView rankingRecyclerView;

    public GroupMainGoalMinutesActivity(TextView groupGoalTextView) {
        this.groupGoalTextView = groupGoalTextView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_goal_minutes);

        groupMainTitle     = findViewById(R.id.group_main_title);

        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        BarChart barChart = findViewById(R.id.barChart);

        navHome   = findViewById(R.id.nav_home);
        navGroup  = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet    = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);
        backButton          = findViewById(R.id.back_button);

        Intent intent  = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");
        Long   groupId   = intent.getLongExtra("groupId", -1L);

        if (groupName != null) groupMainTitle.setText(groupName);
        if (groupGoal != null) groupGoalTextView.setText(groupGoal);

        backButton.setOnClickListener(v -> finish());

        notificationButton1.setOnClickListener(v -> {
            Intent i = new Intent(GroupMainGoalMinutesActivity.this, GroupMemberActivity.class);
            i.putExtra("groupId", groupId);
            startActivity(i);
        });
        notificationButton2.setOnClickListener(v ->
                startActivity(new Intent(GroupMainGoalMinutesActivity.this, GroupCommunityActivity.class))
        );
        notificationButton3.setOnClickListener(v ->
                startActivity(new Intent(GroupMainGoalMinutesActivity.this, AlarmPageActivity.class))
        );

        navHome.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class))
        );
        navGroup.setOnClickListener(v ->
                startActivity(new Intent(this, GroupPageActivity.class))
        );
        navSearch.setOnClickListener(v ->
                startActivity(new Intent(this, GroupSearchPageActivity.class))
        );
        navPet.setOnClickListener(v ->
                startActivity(new Intent(this, PetActivity.class))
        );
        navMyPage.setOnClickListener(v ->
                startActivity(new Intent(this, MyPageMainActivity.class))
        );
    }
}
