package com.example.myapplication;  // 패키지명은 실제 프로젝트에 맞게 수정

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class EmailFindMainActivity extends AppCompatActivity {

    private Button nicknameOkayButton, birthdayOkayButton, emailSearchButton;
    private boolean isNicknameConfirmed = false;
    private boolean isBirthdayConfirmed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_find_main);  // XML 파일 이름과 맞춰야 함

        nicknameOkayButton = findViewById(R.id.nickname_okay_button);
        birthdayOkayButton = findViewById(R.id.birthday_okay_button);
        emailSearchButton = findViewById(R.id.email_search_button);

        // 초기 상태: 이메일 찾기 버튼 비활성화
        emailSearchButton.setEnabled(false);
        emailSearchButton.setAlpha(0.5f); // 흐리게 표시

        nicknameOkayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNicknameConfirmed = true;
                checkAllConfirmed();
            }
        });

        birthdayOkayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBirthdayConfirmed = true;
                checkAllConfirmed();
            }
        });

        emailSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 비밀번호 입력 화면으로 이동
                Intent intent = new Intent(EmailFindMainActivity.this, PasswordInputActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkAllConfirmed() {
        if (isNicknameConfirmed && isBirthdayConfirmed) {
            emailSearchButton.setEnabled(true);
            emailSearchButton.setAlpha(1f);  // 버튼을 선명하게
        }
    }
}
