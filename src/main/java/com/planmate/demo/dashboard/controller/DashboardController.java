package com.planmate.demo.dashboard.controller;

import org.springframework.ui.Model;
import com.planmate.demo.dashboard.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // 지금은 화면 테스트용 임시값
        LocalDate goalDate = LocalDate.of(2026, 8, 31);
        int achievedSeconds = 3600;
        int targetSeconds = 7200;

        int dDay = Math.toIntExact(dashboardService.calculateDDay(goalDate));

        double achievementRate =
                dashboardService.calculateAchievementRate(
                        achievedSeconds,
                        targetSeconds
                );

        String recommendation =
                dashboardService.generateRecommendation(
                        achievementRate
                );

        model.addAttribute("dDay", dDay);
        model.addAttribute("achievementRate", achievementRate);
        model.addAttribute("recommendation", recommendation);

        return "dashboard";
    }
}