// AlarmPageActivity.java
package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AlarmPageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlarmAdapter alarmAdapter;
    private TextView emptyMessageTextView;
    private List<Alarm> alarmList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_page);

        // recyclerView: 알림 리스트 보여줄 컴포넌트
        // emptyMessageTextView: 알림 없을 때 "알림이 없습니다" 텍스트 보여주는 view
        recyclerView = findViewById(R.id.alarm_recycler_view);
        emptyMessageTextView = findViewById(R.id.empty_message);

        // 처음에 빈 알림 리스트 만들고
        alarmList = new ArrayList<>();

        // 테스트용 알림
        // 메세지만 보여주고 버튼은 안 보여줌
        alarmList.add(new Alarm("설정한 목표 잊으신 거 아니죠?", Alarm.TYPE_NORMAL));
        // 메세지, 버튼 둘 다 보여줌
        alarmList.add(new Alarm("홍길동님이 그룹 참가를 요청했어요", Alarm.TYPE_REQUEST));

        // 빈 알림 리스트 만든 걸 어댑터에 연결
        alarmAdapter = new AlarmAdapter(alarmList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(alarmAdapter);

        // 이 메서드는 알림이 있는지 없는지를 판단해서, 뭘 보여줄지 정함
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (alarmList.isEmpty()) {
            emptyMessageTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyMessageTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
