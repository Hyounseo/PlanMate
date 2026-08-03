package com.planmate.demo.dashboard.service;

import com.planmate.demo.dashboard.model.StudyGoal;
import com.planmate.demo.dashboard.repository.StudyGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class GoalService {

    private final StudyGoalRepository studyGoalRepository;

    public GoalService(StudyGoalRepository studyGoalRepository) {
        this.studyGoalRepository = studyGoalRepository;
    }

    // 로그인한 사용자의 가장 가까운 목표 조회
    public Optional<StudyGoal> getCurrentGoal(Long userId) {

        return studyGoalRepository
                .findFirstByUserIdAndGoalDateGreaterThanEqualOrderByGoalDateAsc(
                        userId,
                        LocalDate.now()
                );
    }

    // 목표 등록
    @Transactional
    public StudyGoal createGoal(
            Long userId,
            StudyGoal request
    ) {

        StudyGoal goal = new StudyGoal();

        goal.setUserId(userId);
        goal.setSubjectId(request.getSubjectId());
        goal.setGoalTitle(request.getGoalTitle());
        goal.setGoalDate(request.getGoalDate());
        goal.setTargetSeconds(request.getTargetSeconds());

        goal.setAchievedSeconds(0);
        goal.setAchievementRate(0.0);

        return studyGoalRepository.save(goal);
    }

    // 목표 수정
    @Transactional
    public StudyGoal updateGoal(
            Long goalId,
            Long userId,
            StudyGoal request
    ) {

        StudyGoal goal = studyGoalRepository
                .findById(goalId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "목표를 찾을 수 없습니다."
                        )
                );

        if (!goal.getUserId().equals(userId)) {
            throw new IllegalArgumentException(
                    "본인의 목표만 수정할 수 있습니다."
            );
        }

        goal.setSubjectId(request.getSubjectId());
        goal.setGoalTitle(request.getGoalTitle());
        goal.setGoalDate(request.getGoalDate());
        goal.setTargetSeconds(request.getTargetSeconds());

        return studyGoalRepository.save(goal);
    }

    // 목표 삭제
    @Transactional
    public void deleteGoal(
            Long goalId,
            Long userId
    ) {

        StudyGoal goal = studyGoalRepository
                .findById(goalId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "목표를 찾을 수 없습니다."
                        )
                );

        if (!goal.getUserId().equals(userId)) {
            throw new IllegalArgumentException(
                    "본인의 목표만 삭제할 수 있습니다."
            );
        }

        studyGoalRepository.delete(goal);
    }
}
