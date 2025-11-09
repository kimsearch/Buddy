package com.example.myapplication.network;

import com.example.myapplication.dto.BurnedRecordResponse;
import com.example.myapplication.dto.BurnedRequest;
import com.example.myapplication.dto.IntakeRecordResponse;
import com.example.myapplication.dto.IntakeRequest;
import com.example.myapplication.dto.SavingsRecordResponse;
import com.example.myapplication.dto.SavingsRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // --- 저축 ---
    @POST("api/savings")
    Call<Void> postSavings(@Body SavingsRequest request);

    @GET("api/savings/today/total")
    Call<Float> getSavingsTodayTotal(@Query("group") String group, @Query("user") String user);

    @GET("api/savings/today/history")
    Call<List<SavingsRecordResponse>> getSavingsHistory(@Query("group") String group);

    // --- 섭취 칼로리 ---
    @POST("api/intake")
    Call<Void> postIntake(@Body IntakeRequest request);

    @GET("api/intake/today/total")
    Call<Float> getIntakeTodayTotal(@Query("group") String group, @Query("user") String user);

    @GET("api/intake/today/history")
    Call<List<IntakeRecordResponse>> getIntakeHistory(@Query("group") String group);

    // --- 운동(소모) 칼로리 ---
    @POST("api/burned")
    Call<Void> postBurned(@Body BurnedRequest request);

    @GET("api/burned/today/total")
    Call<Float> getBurnedTodayTotal(@Query("group") String group, @Query("user") String user);

    @GET("api/burned/today/history")
    Call<List<BurnedRecordResponse>> getBurnedHistory(@Query("group") String group);
}
