package com.example.myapplication;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit_client {

    private static Retrofit retrofit;

    // 생성자 비공개 (싱글턴 패턴)
    private Retrofit_client() {}

    // ✅ 메서드 이름을 getClient()로 사용
    public static Retrofit getInstance() {
        if (retrofit == null) {
            // 로깅 인터셉터 설정 (요청/응답 로그 확인용)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://자기 ip 사용:8080/") // 에뮬레이터에서 로컬 서버 접속용
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
