package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;

public class GroupMainReviewCheckActivity extends AppCompatActivity {

    private TextView groupMainTitle;
    private TextView groupGoalTextView;
    private ImageButton backButton;
    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;

    private RecyclerView rankingRecyclerView;
    private BarChart barChart;

    private TextView subjectText;                 // @id/subject_select_card
    private AppCompatImageButton subjectExpand;   // @id/btn_subject_expand
    private AppCompatCheckBox cbReviewConcept;    // @id/cb_review_concept
    private AppCompatCheckBox cbReviewProblems;   // @id/cb_review_problems
    private AppCompatCheckBox cbReviewWrongs;     // @id/cb_review_wrongs
    private EditText etReviewMemo;                // @id/et_review_memo
    private android.widget.Button btnSaveReview;  // @id/btn_save_review

    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;

    private static final String[] SUBJECTS = {"수학", "영어", "국어", "과학", "사회"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_review_check);

        groupMainTitle    = findViewById(R.id.group_main_title);

        backButton        = findViewById(R.id.back_button);
        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);

        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        barChart = findViewById(R.id.barChart);

        subjectText     = findViewById(R.id.subject_select_card);
        subjectExpand   = findViewById(R.id.btn_subject_expand);
        cbReviewConcept = findViewById(R.id.cb_review_concept);
        cbReviewProblems= findViewById(R.id.cb_review_problems);
        cbReviewWrongs  = findViewById(R.id.cb_review_wrongs);
        etReviewMemo    = findViewById(R.id.et_review_memo);
        btnSaveReview   = findViewById(R.id.btn_save_review);

        navHome   = findViewById(R.id.nav_home);
        navGroup  = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet    = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        Intent intent   = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");
        Long   groupId   = intent.getLongExtra("groupId", -1L);

        if (groupName != null) groupMainTitle.setText(groupName);
        if (groupGoal != null) groupGoalTextView.setText(groupGoal);

        backButton.setOnClickListener(v -> finish());

        notificationButton1.setOnClickListener(v -> {
            Intent i = new Intent(this, GroupMemberActivity.class);
            i.putExtra("groupId", groupId);
            startActivity(i);
        });
        notificationButton2.setOnClickListener(v ->
                startActivity(new Intent(this, GroupCommunityActivity.class))
        );
        notificationButton3.setOnClickListener(v ->
                startActivity(new Intent(this, AlarmPageActivity.class))
        );

        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));

        subjectExpand.setOnClickListener(v -> showSubjectPicker());
        subjectText.setOnClickListener(v -> showSubjectPicker());

        btnSaveReview.setOnClickListener(v -> {
            String subject = subjectText.getText() != null ? subjectText.getText().toString() : "과목";
            boolean concept = cbReviewConcept.isChecked();
            boolean problems = cbReviewProblems.isChecked();
            boolean wrongs = cbReviewWrongs.isChecked();
            String memo = etReviewMemo.getText() != null ? etReviewMemo.getText().toString() : "";

            String msg = "과목: " + subject +
                    "\n개념:" + (concept ? "O" : "X") +
                    "  문제:" + (problems ? "O" : "X") +
                    "  오답:" + (wrongs ? "O" : "X") +
                    (memo.isEmpty() ? "" : "\n메모: " + memo);

            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

            // TODO: 여기서 서버 저장/로컬 저장/상태 업데이트 등 구현
        });
    }

    private void showSubjectPicker() {
        int preSelected = 0;
        new AlertDialog.Builder(this)
                .setTitle("과목 선택")
                .setSingleChoiceItems(SUBJECTS, preSelected, (dialog, which) -> {
                    subjectText.setText(SUBJECTS[which]);
                    dialog.dismiss();
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
