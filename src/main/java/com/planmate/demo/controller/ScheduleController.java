package com.planmate.demo.controller;

import com.planmate.demo.entity.Schedule;
import com.planmate.demo.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // 로그인한 사용자의 특정 기간 일정 조회
    @GetMapping
    public List<Schedule> getSchedules(
            @SessionAttribute("userId") Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        return scheduleService.getSchedules(
                userId,
                startDate,
                endDate
        );
    }

    // 로그인한 사용자의 일정 등록
    @PostMapping
    public Schedule createSchedule(
            @SessionAttribute("userId") Long userId,
            @RequestBody Schedule request
    ) {
        return scheduleService.createSchedule(
                userId,
                request
        );
    }

    // 로그인한 사용자의 일정 수정
    @PutMapping("/{scheduleId}")
    public Schedule updateSchedule(
            @SessionAttribute("userId") Long userId,
            @PathVariable Long scheduleId,
            @RequestBody Schedule request
    ) {
        return scheduleService.updateSchedule(
                scheduleId,
                userId,
                request
        );
    }

    // 로그인한 사용자의 일정 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @SessionAttribute("userId") Long userId,
            @PathVariable Long scheduleId
    ) {
        scheduleService.deleteSchedule(
                scheduleId,
                userId
        );

        return ResponseEntity.noContent().build();
    }
}