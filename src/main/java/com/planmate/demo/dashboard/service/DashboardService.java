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
}