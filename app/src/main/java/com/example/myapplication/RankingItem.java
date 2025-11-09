package com.example.myapplication;

// 다용도 랭킹용 DTO/Model
public class RankingItem {

    private Long memberId;          // 사용자 ID
    private String nickname;        // 닉네임
    private int successCount;       // 성공 횟수 (습관형 그룹 등)
    private float value;            // 공부 시간, 운동량 등 랭킹 기준값
    private float yesterdayValue;   // 어제 값 (몸무게 비교용)
    private float lossValue;        // 몸무게 감소량 등 계산용
    private int profileResId;       // 프로필 리소스
    private int progress;           // UI용 (0~100)

    // --- 생성자 ---
    public RankingItem(Long memberId, String nickname, int successCount) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.successCount = successCount;
    }

    public RankingItem(Long memberId, String nickname, float value) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.value = value;
    }

    public RankingItem() {}

    // --- Getter ---
    public Long getMemberId() { return memberId; }

    public String getNickname() { return nickname; }

    public int getSuccessCount() { return successCount; }

    public float getValue() { return value; }  // ✅ 공부시간 / 운동시간 / 랭킹 기준값

    public float getYesterdayValue() { return yesterdayValue; }

    public float getLossValue() { return lossValue; }

    public int getProfileResId() { return profileResId; }

    public int getProgress() { return progress; }

    // --- Setter ---
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public void setNickname(String nickname) { this.nickname = nickname; }

    public void setSuccessCount(int successCount) { this.successCount = successCount; }

    public void setValue(float value) { this.value = value; }

    public void setYesterdayValue(float yesterdayValue) { this.yesterdayValue = yesterdayValue; }

    public void setLossValue(float lossValue) { this.lossValue = lossValue; }

    public void setProfileResId(int profileResId) { this.profileResId = profileResId; }

    public void setProgress(int progress) { this.progress = progress; }

    // --- Helper ---
    @Override
    public String toString() {
        return "RankingItem{" +
                "memberId=" + memberId +
                ", nickname='" + nickname + '\'' +
                ", value=" + value +
                ", lossValue=" + lossValue +
                '}';
    }
}
