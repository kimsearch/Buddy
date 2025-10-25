package com.example.myapplication;

import java.time.LocalDate;

public class CalendarGoalItem {
    public String title;
    public String startDate;  // 서버에서 "yyyy-MM-dd" 형식의 문자열로 전달됨
    public int cycleDays;

    public CalendarGoalItem(String title, String startDate, int cycleDays) {
        this.title = title;
        this.startDate = startDate;
        this.cycleDays = cycleDays;
    }

    // 🔸 문자열을 LocalDate로 변환해서 반환
    public LocalDate getStartDateAsLocalDate() {
        return LocalDate.parse(startDate);
    }

    // ✅ 클릭한 날짜가 포함된 주기 반환
    public LocalDate[] getPeriodForDate(LocalDate selectedDate) {
        LocalDate start = getStartDateAsLocalDate();
        if (selectedDate.isBefore(start)) return null;

        long daysPassed = java.time.temporal.ChronoUnit.DAYS.between(start, selectedDate);
        long cycleIndex = daysPassed / cycleDays;

        LocalDate periodStart = start.plusDays(cycleIndex * cycleDays);
        LocalDate periodEnd = periodStart.plusDays(cycleDays - 1);
        return new LocalDate[]{periodStart, periodEnd};
    }
}
