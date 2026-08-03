package com.planmate.demo.dashboard.repository;

import com.planmate.demo.dashboard.model.StudyGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface StudyGoalRepository extends JpaRepository<StudyGoal, Long> {

    /*
     * 로그인한 사용자의 목표 중
     * 오늘 이후에 있는 목표를 날짜가 가까운 순서로 조회
     */
    Optional<StudyGoal>
    findFirstByUserIdAndGoalDateGreaterThanEqualOrderByGoalDateAsc(
            Long userId,
            LocalDate today
    );
}