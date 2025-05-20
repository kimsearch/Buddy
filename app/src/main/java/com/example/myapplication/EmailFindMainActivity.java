package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailFindMainActivity extends AppCompatActivity {

    private EditText nicknameInput, birthdayInput;
    private Button nicknameCheckButton, birthdayCheckButton, emailFindButton;

    private boolean isNicknameChecked = false;
    private boolean isBirthdayChecked = false;

    private Retrofit_interface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_find_main);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish(); //
        });

        nicknameInput = findViewById(R.id.edittext_nickname);
        birthdayInput = findViewById(R.id.edittext_birthday_name);
        nicknameCheckButton = findViewById(R.id.nickname_okay_button);
        birthdayCheckButton = findViewById(R.id.birthday_okay_button);
        emailFindButton = findViewById(R.id.email_search_button);

        emailFindButton.setEnabled(false);
        apiService = Retrofit_client.getInstance().create(Retrofit_interface.class);

        nicknameCheckButton.setOnClickListener(v -> {
            String nickname = nicknameInput.getText().toString().trim();
            if (nickname.isEmpty()) {
                Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 닉네임 존재 여부 확인
            apiService.checkNickname(nickname).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 409) {
                        isNicknameChecked = true;
                        Toast.makeText(EmailFindMainActivity.this, "닉네임 확인 완료!", Toast.LENGTH_SHORT).show();
                        checkIfReadyToSearch();
                    } else {
                        Toast.makeText(EmailFindMainActivity.this, "존재하지 않는 닉네임입니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(EmailFindMainActivity.this, "서버 오류", Toast.LENGTH_SHORT).show();
                }
            });
        });

        birthdayCheckButton.setOnClickListener(v -> {
            String nickname = nicknameInput.getText().toString().trim();
            String birthday = birthdayInput.getText().toString().trim();

            if (birthday.isEmpty()) {
                Toast.makeText(this, "생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            apiService.verifyNickname(nickname, birthday).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        isBirthdayChecked = true;
                        Toast.makeText(EmailFindMainActivity.this, "생년월일 확인 완료!", Toast.LENGTH_SHORT).show();
                        checkIfReadyToSearch();
                    } else {
                        Toast.makeText(EmailFindMainActivity.this, "생년월일이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(EmailFindMainActivity.this, "서버 오류", Toast.LENGTH_SHORT).show();
                }
            });
        });

        emailFindButton.setOnClickListener(v -> {
            String nickname = nicknameInput.getText().toString().trim();
            String birthday = birthdayInput.getText().toString().trim();

            Intent intent = new Intent(EmailFindMainActivity.this, EmailFindSubActivity.class);
            intent.putExtra("nickname", nickname);
            intent.putExtra("birthday", birthday);
            startActivity(intent);
        });
    }

    private void checkIfReadyToSearch() {
        if (isNicknameChecked && isBirthdayChecked) {
            emailFindButton.setEnabled(true);
        }
    }
}
