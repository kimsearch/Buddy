package com.example.myapplication;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Retrofit_interface {

    // 회원가입
    @POST("/members")
    Call<ResponseBody> createMember(@Body Member member);

    // 닉네임 중복 확인
    @GET("/members/check-nickname")
    Call<ResponseBody> checkNickname(@Query("nickname") String nickname);

    // 이메일 인증 요청
    @POST("/members/email-auth/request")
    Call<ResponseBody> requestEmailAuth(@Query("email") String email);

    // 이메일 인증 코드 확인
    @POST("/members/email-auth/verify")
    Call<ResponseBody> verifyEmailCode(@Query("email") String email, @Query("code") String code);

    // ✅ 로그인 응답 타입 수정 (닉네임 포함된 응답 처리)
    @POST("/members/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // 이메일 찾기 (닉네임 + 생일 기반)
    @POST("/members/find-email")
    Call<ResponseBody> findEmailByNicknameAndBirthday(
            @Query("nickname") String nickname,
            @Query("birthday") String birthday
    );

    // 이메일 찾기 전에 닉네임 + 생일 유효성 검사
    @POST("/members/verify-nickname")
    Call<ResponseBody> verifyNickname(
            @Query("nickname") String nickname,
            @Query("birthday") String birthday
    );

    // 비밀번호 변경 (이메일 + 새 비밀번호)
    @POST("/members/reset-password-simple")
    Call<ResponseBody> resetPassword(
            @Query("email") String email,
            @Query("newPassword") String newPassword
    );
    //회원 탈퇴
    @DELETE("/members/delete")
    Call<ResponseBody> deleteMember(@Query("email") String email);

    // 이메일 존재 여부 확인
    @GET("/members/check-email")
    Call<ResponseBody> checkEmail(@Query("email") String email);
}