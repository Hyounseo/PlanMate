package com.planmate.demo.dashboard.controller;

import com.planmate.demo.dashboard.dto.SubjectStudyRatio;
import com.planmate.demo.dashboard.model.StudyGoal;
import com.planmate.demo.dashboard.service.DashboardService;
import com.planmate.demo.entity.Schedule;
import com.planmate.demo.service.ScheduleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final ScheduleService scheduleService;

    public DashboardController(
            DashboardService dashboardService,
            ScheduleService scheduleService
    ) {
        this.dashboardService = dashboardService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Model model,
            HttpSession session
    ) {

        // 로그인한 사용자 번호 가져오기
        Long userId =
                (Long) session.getAttribute("userId");

        // 로그인하지 않은 사용자는 로그인 화면으로 이동
        if (userId == null) {
            return "redirect:/login";
        }

        /*
         * 오늘 총 공부시간 조회
         */
        int totalStudySeconds =
                dashboardService
                        .getTodayTotalStudySeconds(userId);

        int studyHours =
                totalStudySeconds / 3600;

        int studyMinutes =
                (totalStudySeconds % 3600) / 60;

        /*
         * 오늘 과목별 공부 비율 조회
         */
        List<SubjectStudyRatio> subjectRatios =
                dashboardService
                        .getTodaySubjectStudyRatios(userId);

        /*
         * 오늘 이후 가장 가까운 일정 3개 조회
         */
        List<Schedule> upcomingSchedules =
                scheduleService
                        .getUpcomingSchedules(userId);

        /*
         * 로그인한 사용자의 가장 가까운 목표 조회
         */
        Optional<StudyGoal> goalOptional =
                dashboardService
                        .getNearestGoal(userId);

        boolean hasGoal =
                goalOptional.isPresent();

        /*
         * 목표가 없을 때 사용할 기본값
         */
        String goalTitle =
                "등록된 목표가 없습니다.";

        int dDay = 0;

        int targetSeconds = 0;
        int targetHours = 0;
        int targetMinutes = 0;

        double achievementRate = 0.0;

        /*
         * 목표가 존재하는 경우
         */
        if (hasGoal) {

            StudyGoal goal =
                    goalOptional.get();

            goalTitle =
                    goal.getGoalTitle();

            dDay = Math.toIntExact(
                    dashboardService
                            .calculateDDay(
                                    goal.getGoalDate()
                            )
            );

            targetSeconds =
                    goal.getTargetSeconds() != null
                            ? goal.getTargetSeconds()
                            : 0;

            targetHours =
                    targetSeconds / 3600;

            targetMinutes =
                    (targetSeconds % 3600) / 60;

            achievementRate =
                    dashboardService
                            .calculateAchievementRate(
                                    totalStudySeconds,
                                    targetSeconds
                            );
        }

        /*
         * 달성률 기반 추천 문구
         */
        String recommendation;

        if (!hasGoal) {
            recommendation =
                    "🎯 학습 목표를 등록하면 "
                            + "목표 달성률과 맞춤 추천을 "
                            + "확인할 수 있습니다.";
        } else {
            recommendation =
                    dashboardService
                            .generateRecommendation(
                                    achievementRate
                            );
        }

        /*
         * 화면에 전달할 데이터
         */
        model.addAttribute(
                "studyHours",
                studyHours
        );

        model.addAttribute(
                "studyMinutes",
                studyMinutes
        );

        model.addAttribute(
                "totalStudySeconds",
                totalStudySeconds
        );

        model.addAttribute(
                "subjectRatios",
                subjectRatios
        );

        model.addAttribute(
                "upcomingSchedules",
                upcomingSchedules
        );

        model.addAttribute(
                "hasGoal",
                hasGoal
        );

        model.addAttribute(
                "goalTitle",
                goalTitle
        );

        model.addAttribute(
                "dDay",
                dDay
        );

        model.addAttribute(
                "targetSeconds",
                targetSeconds
        );

        model.addAttribute(
                "targetHours",
                targetHours
        );

        model.addAttribute(
                "targetMinutes",
                targetMinutes
        );

        model.addAttribute(
                "achievementRate",
                achievementRate
        );

        model.addAttribute(
                "recommendation",
                recommendation
        );

        return "dashboard";
    }
}