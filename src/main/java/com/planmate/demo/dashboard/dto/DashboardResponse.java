package com.planmate.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DashboardResponse {

    // 화면 오른쪽 위 사용자 이름
    private String userName;

    // 총 공부 시간(초)
    private Long totalStudySeconds;

    // 목표 날짜까지 남은 일수
    private Long dDay;

    // 전체 목표 달성률
    private Double achievementRate;

    // 과목별 공부 비율
    private List<SubjectRatio> subjectRatios;

    // 추천 이유
    private String recommendationReason;

    // 사용자에게 보여줄 추천 문장
    private String recommendationFeedback;

    @Getter
    @AllArgsConstructor
    public static class SubjectRatio {

        private Long subjectId;
        private String subjectName;

        // 해당 과목 공부 시간
        private Long studySeconds;

        // 전체 공부 시간에서 해당 과목이 차지하는 비율
        private Double ratio;
    }
}