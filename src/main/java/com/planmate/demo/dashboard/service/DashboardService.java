package com.planmate.demo.dashboard.service;

import com.planmate.demo.dashboard.dto.SubjectStudyRatio;
import com.planmate.demo.dashboard.model.StudyGoal;
import com.planmate.demo.dashboard.repository.RecommendationRepository;
import com.planmate.demo.dashboard.repository.StudyGoalRepository;
import com.planmate.demo.entity.Subject;
import com.planmate.demo.repository.SubjectRepository;
import com.planmate.demo.study.model.StudyRecord;
import com.planmate.demo.study.repository.StudyRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {

    private final StudyGoalRepository studyGoalRepository;
    private final RecommendationRepository recommendationRepository;
    private final StudyRecordRepository studyRecordRepository;
    private final SubjectRepository subjectRepository;

    public DashboardService(
            StudyGoalRepository studyGoalRepository,
            RecommendationRepository recommendationRepository,
            StudyRecordRepository studyRecordRepository,
            SubjectRepository subjectRepository
    ) {
        this.studyGoalRepository = studyGoalRepository;
        this.recommendationRepository = recommendationRepository;
        this.studyRecordRepository = studyRecordRepository;
        this.subjectRepository = subjectRepository;
    }

    /**
     * 로그인한 사용자의 목표 중
     * 오늘 이후 가장 가까운 목표를 조회한다.
     */
    public Optional<StudyGoal> getNearestGoal(Long userId) {

        if (userId == null) {
            return Optional.empty();
        }

        LocalDate today = LocalDate.now();

        return studyGoalRepository
                .findFirstByUserIdAndGoalDateGreaterThanEqualOrderByGoalDateAsc(
                        userId,
                        today
                );
    }

    /**
     * 목표일까지 남은 날짜를 계산한다.
     */
    public long calculateDDay(LocalDate goalDate) {

        if (goalDate == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();

        return ChronoUnit.DAYS.between(today, goalDate);
    }

    /**
     * 목표 달성률을 계산한다.
     */
    public double calculateAchievementRate(
            int achievedSeconds,
            int targetSeconds
    ) {

        if (targetSeconds <= 0) {
            return 0.0;
        }

        double rate =
                (double) achievedSeconds / targetSeconds * 100;

        return Math.round(rate * 10) / 10.0;
    }

    /**
     * 로그인한 사용자의 오늘 총 공부시간을 조회한다.
     */
    public int getTodayTotalStudySeconds(Long userId) {

        if (userId == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();

        List<StudyRecord> records =
                studyRecordRepository.findByUserIdAndStudyDateBetween(
                        userId,
                        today,
                        today
                );

        return records.stream()
                .filter(record ->
                        record.getDurationSeconds() != null
                )
                .mapToInt(
                        StudyRecord::getDurationSeconds
                )
                .sum();
    }

    /**
     * 오늘 과목별 공부 비율을 계산한다.
     */
    public List<SubjectStudyRatio> getTodaySubjectStudyRatios(
            Long userId
    ) {

        List<SubjectStudyRatio> result =
                new ArrayList<>();

        if (userId == null) {
            return result;
        }

        LocalDate today = LocalDate.now();

        List<StudyRecord> records =
                studyRecordRepository.findByUserIdAndStudyDateBetween(
                        userId,
                        today,
                        today
                );

        int totalStudySeconds = records.stream()
                .filter(record ->
                        record.getDurationSeconds() != null
                )
                .mapToInt(
                        StudyRecord::getDurationSeconds
                )
                .sum();

        if (totalStudySeconds == 0) {
            return result;
        }

        List<Subject> subjects =
                subjectRepository.findByUserId(userId);

        for (Subject subject : subjects) {

            int subjectStudySeconds = records.stream()
                    .filter(record ->
                            record.getDurationSeconds() != null
                    )
                    .filter(record ->
                            record.getSubjectId()
                                    .equals(
                                            subject.getSubjectId()
                                    )
                    )
                    .mapToInt(
                            StudyRecord::getDurationSeconds
                    )
                    .sum();

            if (subjectStudySeconds == 0) {
                continue;
            }

            double ratio =
                    (double) subjectStudySeconds
                            / totalStudySeconds
                            * 100;

            ratio =
                    Math.round(ratio * 10) / 10.0;

            result.add(
                    new SubjectStudyRatio(
                            subject.getSubjectName(),
                            subject.getColor(),
                            subjectStudySeconds,
                            ratio
                    )
            );
        }

        return result;
    }

    /**
     * 목표 달성률에 따라 추천 문구를 생성한다.
     */
    public String generateRecommendation(
            double achievementRate
    ) {

        if (achievementRate < 30) {
            return "📖 오늘은 목표 공부량이 많이 부족합니다. "
                    + "1시간 이상 추가 학습을 추천합니다.";

        } else if (achievementRate < 60) {
            return "✍️ 조금만 더 공부하면 목표에 가까워집니다. "
                    + "30분 추가 학습을 추천합니다.";

        } else if (achievementRate < 80) {
            return "👍 좋은 학습 흐름입니다. "
                    + "현재 페이스를 유지하세요.";

        } else if (achievementRate < 100) {
            return "🎉 목표 달성이 얼마 남지 않았습니다. "
                    + "조금만 더 힘내세요!";

        } else {
            return "🏆 오늘의 목표를 모두 달성했습니다! "
                    + "정말 수고하셨습니다.";
        }
    }
}