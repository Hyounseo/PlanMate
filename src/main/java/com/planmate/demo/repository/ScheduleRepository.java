package com.planmate.demo.repository;

import com.planmate.demo.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository
        extends JpaRepository<Schedule, Long> {

    // 특정 사용자의 기간 내 일정 조회
    List<Schedule>
    findByUserIdAndScheduleDateBetweenOrderByScheduleDateAscStartTimeAsc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    // 일정 번호와 사용자 번호로 일정 한 개 조회
    Optional<Schedule> findByScheduleIdAndUserId(
            Long scheduleId,
            Long userId
    );

    void deleteBySubjectIdAndUserId(Long subjectId, Long userId);
}
