package com.planmate.demo.controller;

import com.planmate.demo.dashboard.dto.AiLearningRequest;
import com.planmate.demo.dashboard.dto.SubjectStudyRatio;
import com.planmate.demo.dashboard.model.StudyGoal;
import com.planmate.demo.dashboard.service.DashboardService;
import com.planmate.demo.entity.Schedule;
import com.planmate.demo.service.AiLearningService;
import com.planmate.demo.service.ScheduleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class AiRecommendationController {

    private final DashboardService dashboardService;
    private final ScheduleService scheduleService;
    private final AiLearningService aiLearningService;

    public AiRecommendationController(
            DashboardService dashboardService,
            ScheduleService scheduleService,
            AiLearningService aiLearningService
    ) {
        this.dashboardService = dashboardService;
        this.scheduleService = scheduleService;
        this.aiLearningService = aiLearningService;
    }

    @PostMapping("/api/ai/recommendation")
    public ResponseEntity<String> generateRecommendation(
            HttpSession session
    ) {

        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity
                    .status(401)
                    .body("로그인이 필요합니다.");
        }

        int totalStudySeconds =
                dashboardService
                        .getTodayTotalStudySeconds(userId);

        int studyHours =
                totalStudySeconds / 3600;

        List<SubjectStudyRatio> subjectRatios =
                dashboardService
                        .getTodaySubjectStudyRatios(userId);

        List<Schedule> upcomingSchedules =
                scheduleService
                        .getUpcomingSchedules(userId);

        Optional<StudyGoal> goalOptional =
                dashboardService
                        .getNearestGoal(userId);

        if (goalOptional.isEmpty()) {
            return ResponseEntity.ok(
                    "학습 목표를 등록하면 AI 맞춤 추천을 받을 수 있습니다."
            );
        }

        StudyGoal goal =
                goalOptional.get();

        int targetSeconds =
                goal.getTargetSeconds() != null
                        ? goal.getTargetSeconds()
                        : 0;

        int targetHours =
                targetSeconds / 3600;

        double achievementRate =
                dashboardService
                        .calculateAchievementRate(
                                totalStudySeconds,
                                targetSeconds
                        );

        String subjectRatiosText =
                subjectRatios.isEmpty()
                        ? "오늘 공부 기록 없음"
                        : subjectRatios.stream()
                          .map(subject ->
                               subject.getSubjectName()
                               + " "
                               + subject.getRatio()
                               + "%"
                          )
                          .collect(Collectors.joining(", "));

        LocalDate today =
                LocalDate.now();

        String upcomingSchedulesText =
                upcomingSchedules.isEmpty()
                        ? "다가오는 일정 없음"
                        : upcomingSchedules.stream()
                          .map(schedule -> {

                              long dDay =
                                      ChronoUnit.DAYS.between(
                                              today,
                                              schedule.getScheduleDate()
                                      );

                              String dDayText =
                              dDay == 0
                                      ? "D-Day"
                                      : "D-" + dDay;

                              return schedule.getTitle()
                                     + " "
                                     + dDayText;
                          })
                          .collect(Collectors.joining(", "));

        AiLearningRequest request =
                AiLearningRequest.builder()
                        .studyHours(studyHours)
                        .targetHours(targetHours)
                        .goalTitle(goal.getGoalTitle())
                        .achievementRate(achievementRate)
                        .subjectRatios(subjectRatiosText)
                        .upcomingSchedules(
                                upcomingSchedulesText
                        )
                        .build();

        String recommendation =
                aiLearningService
                        .generateRecommendation(request);

        return ResponseEntity.ok(recommendation);
    }
}