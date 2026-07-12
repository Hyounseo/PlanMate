package com.planmate.demo.study.service;

import com.planmate.demo.study.model.StudyRecord;
import com.planmate.demo.study.repository.StudyRecordRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final StudyRecordRepository studyRecordRepository;

    public StatisticsService(StudyRecordRepository studyRecordRepository) {
        this.studyRecordRepository = studyRecordRepository;
    }

    // 특정 날짜의 총 공부시간(초)
    public int getDailyTotalSeconds(Long userId, LocalDate date) {
        return sumSeconds(studyRecordRepository.findByUserIdAndStudyDateBetween(userId, date, date));
    }

    // 이번 주(월~일) 총 공부시간(초)
    public int getWeeklyTotalSeconds(Long userId, LocalDate anyDateInWeek) {
        LocalDate monday = anyDateInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = anyDateInWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return sumSeconds(studyRecordRepository.findByUserIdAndStudyDateBetween(userId, monday, sunday));
    }

    // 이번 달 총 공부시간(초)
    public int getMonthlyTotalSeconds(Long userId, LocalDate anyDateInMonth) {
        LocalDate firstDay = anyDateInMonth.withDayOfMonth(1);
        LocalDate lastDay = anyDateInMonth.with(TemporalAdjusters.lastDayOfMonth());
        return sumSeconds(studyRecordRepository.findByUserIdAndStudyDateBetween(userId, firstDay, lastDay));
    }

    // 과목별 공부시간 합계 (Chart.js에 바로 넣을 수 있는 형태: subjectId -> 초)
    public Map<Long, Integer> getSecondsBySubject(Long userId, LocalDate startDate, LocalDate endDate) {
        List<StudyRecord> records = studyRecordRepository.findByUserIdAndStudyDateBetween(userId, startDate, endDate);
        return records.stream()
                .filter(r -> r.getDurationSeconds() != null) // 진행중인 기록은 통계에서 제외
                .collect(Collectors.groupingBy(
                        StudyRecord::getSubjectId,
                        Collectors.summingInt(StudyRecord::getDurationSeconds)
                ));
    }

    private int sumSeconds(List<StudyRecord> records) {
        return records.stream()
                .filter(r -> r.getDurationSeconds() != null)
                .mapToInt(StudyRecord::getDurationSeconds)
                .sum();
    }
}
