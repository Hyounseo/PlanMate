package com.planmate.demo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiLearningRequest {

    /**
     * 오늘 공부시간(시간 단위)
     */
    private int studyHours;

    /**
     * 오늘 목표시간(시간 단위)
     */
    private int targetHours;

    /**
     * 목표명
     */
    private String goalTitle;

    /**
     * 목표 달성률
     */
    private double achievementRate;

    /**
     * 과목별 공부 비율
     *
     * 예)
     * Java 45%
     * SQL 30%
     * OS 25%
     */
    private String subjectRatios;

    /**
     * 다가오는 일정
     *
     * 예)
     * 자료구조 과제 D-1
     * SQLD 시험 D-3
     */
    private String upcomingSchedules;

}