package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailFindSubActivity extends AppCompatActivity {

    private TextView emailResultText;
    private Button goToPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_find_sub);

        emailResultText = findViewById(R.id.email_text);
        goToPasswordButton = findViewById(R.id.password_search_button);

        String nickname = getIntent().getStringExtra("nickname");
        String birthday = getIntent().getStringExtra("birthday");

        if (nickname == null || birthday == null) {
            Toast.makeText(this, "정보가 누락되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
        api.findEmailByNicknameAndBirthday(nickname, birthday).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String email = response.body().string();
                        emailResultText.setText("이메일은 " + email + " 입니다.");
                    } else {
                        emailResultText.setText("해당 사용자의 이메일을 찾을 수 없습니다.");
                    }
                } catch (IOException e) {
                    emailResultText.setText("오류가 발생했습니다.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                emailResultText.setText("서버 오류: " + t.getMessage());
            }
        });

        goToPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(EmailFindSubActivity.this, PasswordInputActivity.class);
            startActivity(intent);
        });
    }
}
