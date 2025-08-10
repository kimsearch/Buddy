package com.example.myapplication;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
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
    //그룹 생성
    @POST("/groups/create")
    Call<BuddyGroup> createGroup(@Body BuddyGroupDto dto, @Query("memberId") Long memberId);

    @GET("/groups/check-name")
    Call<ResponseBody> checkGroupName(@Query("name") String name);

    @GET("/groups/my-groups")
    Call<List<Group>> getMyGroups(@Query("memberId") Long memberId);

    @DELETE("/groups/exit")
    Call<ResponseBody> exitGroup(@Query("groupId") Long groupId, @Query("memberId") Long memberId);
    //게시글 작성
    @POST("/api/posts")
    Call<Post> createPost(
            @Query("memberId") Long memberId,
            @Query("groupId") Long groupId,
            @Query("title") String title,
            @Query("content") String content,
            @Query("imageUrl") String imageUrl
    );
    //게시글 목록
    @GET("/api/posts")
    Call<List<Post>> getPosts(@Query("groupId") Long groupId);
    //댓글
    @GET("/api/comments/post/{postId}")
    Call<List<CommentItem>> getCommentsByPost(@Path("postId") Long postId);
    //댓글 등록
    @POST("/api/comments")
    Call<PostComment> createComment(
            @Query("postId") Long postId,
            @Query("memberId") Long memberId,
            @Query("content") String content
    );
    //댓글 삭제
    @DELETE("/api/comments/{commentId}")
    Call<Void> deleteComment(@Path("commentId") Long commentId, @Query("memberId") Long memberId);

    //게시글 삭제
    @DELETE("/api/posts/{postId}")
    Call<Void> deletePost(@Path("postId") Long postId, @Query("memberId") Long memberId);

    @GET("/api/likes/check")
    Call<Boolean> hasLiked(@Query("postId") Long postId, @Query("memberId") Long memberId);

    // 좋아요 개수 확인 ✅ 이걸 제대로 지정
    @GET("/api/likes/count")
    Call<Long> countLikes(@Query("postId") Long postId);

    // 좋아요 토글
    @POST("/api/likes")
    Call<LikeResponse> toggleLike(@Query("postId") Long postId, @Query("memberId") Long memberId);
    //그룹 멤버 확인
    @GET("/groups/{groupId}/members")
    Call<List<GroupMemberItem>> getGroupMembers(@Path("groupId") Long groupId);

    @GET("/groups/calendar/goals")
    Call<List<CalendarGoalItem>> getCalendarGoals(@Query("memberId") Long memberId);

    @GET("/groups/search")
    Call<List<GroupSearchResponse>> searchGroups(
            @Query("query") String query,
            @Query("categoryMain") String categoryMain,
            @Query("categorySub") String categorySub,
            @Query("memberId") Long memberId
    );

    @POST("/group-requests/send")
    Call<Void> sendJoinRequest(@Query("groupId") Long groupId, @Query("memberId") Long memberId);

    @GET("/notifications")
    Call<List<Alarm>> getNotifications(@Query("memberId") Long memberId);

    @POST("/group-requests/handle")
    Call<Void> handleJoinRequest(@Body JoinRequestHandleDto dto);

    @POST("/api/group-goal-log/update-step")
    Call<Void> updateStepLog(@Body GroupGoalLogRequest request);

    //목표 달성률 api
    @GET("/api/group-goal-log/progress")
    Call<GoalProgressResponse> getGoalProgress(
            @Query("groupId") Long groupId,
            @Query("memberId") Long memberId
    );

    //랭킹 차트 API
    @GET("/api/group-goal-log/ranking/{groupId}")
    Call<List<RankingItem>> getRanking(@Path("groupId") Long groupId);

    @GET("/api/group-goal-log/history")
    Call<List<StepHistoryItem>> getWeeklyStepHistory(@Query("groupId") Long groupId, @Query("memberId") Long memberId);
    //펫 먹이
    @GET("/members/feed-count")
    Call<Integer> getFeedCount(@Query("memberId") Long memberId);
    //부수입 입력해서 서버에 저장
    @POST("/api/group-goal-log/update-income")
    Call<Void> updateIncome(@Body IncomeLogRequest req);

    //일주일 간 기록 보여주는 API
    @GET("/api/group-goal-log/record-history")
   Call<List<StepHistoryItem>> getWeeklyRecordHistory(@Query("groupId") Long groupId, @Query("memberId") Long memberId);

}