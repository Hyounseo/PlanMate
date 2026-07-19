package com.planmate.demo.dashboard.service;

import com.planmate.demo.dashboard.repository.RecommendationRepository;
import com.planmate.demo.dashboard.repository.StudyGoalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class DashboardService {

    private final StudyGoalRepository StudyGoalRepository;
    private final RecommendationRepository RecommendationRepository;

    public DashboardService(
            StudyGoalRepository studyGoalRepository,
            RecommendationRepository recommendationRepository
    ) {
        this.StudyGoalRepository = studyGoalRepository;
        this.RecommendationRepository = recommendationRepository;
    }

    public long calculateDDay(LocalDate goalDate) {

        if (goalDate == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();

        return ChronoUnit.DAYS.between(today, goalDate);
    }

    public double calculateAchievementRate(int achievedSeconds, int targetSeconds) {

        if (targetSeconds <= 0) {
            return 0.0;
        }

        double rate = (double) achievedSeconds / targetSeconds * 100;

        return Math.round(rate * 10) / 10.0;
    }

    public String generateRecommendation(double achievementRate) {

        if (achievementRate < 30) {
            return "📖 오늘은 목표 공부량이 많이 부족합니다. 1시간 이상 추가 학습을 추천합니다.";

        } else if (achievementRate < 60) {
            return "✍️ 조금만 더 공부하면 목표에 가까워집니다. 30분 추가 학습을 추천합니다.";

        } else if (achievementRate < 80) {
            return "👍 좋은 학습 흐름입니다. 현재 페이스를 유지하세요.";

        } else if (achievementRate < 100) {
            return "🎉 목표 달성이 얼마 남지 않았습니다. 조금만 더 힘내세요!";

        } else {
            return "🏆 오늘의 목표를 모두 달성했습니다! 정말 수고하셨습니다.";
        }
    }
}