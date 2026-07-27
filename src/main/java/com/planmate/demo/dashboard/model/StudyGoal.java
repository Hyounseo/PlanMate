package com.planmate.demo.dashboard.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "study_goal")
@Getter
@Setter
@NoArgsConstructor
public class StudyGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long goalId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "goal_title")
    private String goalTitle;

    @Column(name = "goal_date")
    private LocalDate goalDate;

    @Column(name = "target_seconds")
    private Integer targetSeconds;

    @Column(name = "achieved_seconds")
    private Integer achievedSeconds;

    @Column(name = "achievement_rate")
    private Double achievementRate;
}