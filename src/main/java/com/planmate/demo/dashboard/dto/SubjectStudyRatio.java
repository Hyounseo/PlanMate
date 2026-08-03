package com.planmate.demo.dashboard.dto;

public class SubjectStudyRatio {

    private final String subjectName;
    private final String color;
    private final int studySeconds;
    private final double ratio;

    public SubjectStudyRatio(
            String subjectName,
            String color,
            int studySeconds,
            double ratio
    ) {
        this.subjectName = subjectName;
        this.color = color;
        this.studySeconds = studySeconds;
        this.ratio = ratio;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getColor() {
        return color;
    }

    public int getStudySeconds() {
        return studySeconds;
    }

    public double getRatio() {
        return ratio;
    }
}