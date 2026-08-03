package com.planmate.demo.dashboard.controller;

import com.planmate.demo.dashboard.model.StudyGoal;
import com.planmate.demo.dashboard.service.GoalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    // 현재 목표 조회
    @GetMapping("/current")
    public ResponseEntity<StudyGoal> getCurrentGoal(
            HttpSession session
    ) {

        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        return goalService.getCurrentGoal(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.noContent().build()
                );
    }

    // 목표 등록
    @PostMapping
    public ResponseEntity<StudyGoal> createGoal(
            @RequestBody StudyGoal request,
            HttpSession session
    ) {

        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        StudyGoal savedGoal =
                goalService.createGoal(
                        userId,
                        request
                );

        return ResponseEntity.ok(savedGoal);
    }

    // 목표 수정
    @PutMapping("/{goalId}")
    public ResponseEntity<StudyGoal> updateGoal(
            @PathVariable Long goalId,
            @RequestBody StudyGoal request,
            HttpSession session
    ) {

        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        StudyGoal updatedGoal =
                goalService.updateGoal(
                        goalId,
                        userId,
                        request
                );

        return ResponseEntity.ok(updatedGoal);
    }

    // 목표 삭제
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long goalId,
            HttpSession session
    ) {

        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        goalService.deleteGoal(
                goalId,
                userId
        );

        return ResponseEntity.noContent().build();
    }
}