package com.planmate.demo.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.planmate.demo.dashboard.dto.AiLearningRequest;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private static final String MODEL_NAME =
            "gemini-3.6-flash";

    public String generateLearningFeedback(
            AiLearningRequest request
    ) {

        String prompt = buildPrompt(request);

        try (Client client = new Client()) {

            GenerateContentResponse response =
                    client.models.generateContent(
                            MODEL_NAME,
                            prompt,
                            null
                    );

            String result = response.text();

            if (result == null || result.isBlank()) {
                return "AI가 학습 추천을 생성하지 못했습니다.";
            }

            return result.trim();

        } catch (Exception e) {

            e.printStackTrace();

            return "AI 추천을 불러오지 못했습니다.";
        }
    }

    private String buildPrompt(
            AiLearningRequest request
    ) {

        return """
                당신은 대학생을 위한 AI 학습 코치입니다.

                아래 학습 데이터를 분석하여
                오늘의 학습 전략을 추천하세요.

                [현재 목표]
                %s

                [오늘 공부시간]
                %d시간

                [오늘 목표시간]
                %d시간

                [목표 달성률]
                %.1f%%

                [과목별 공부 비율]
                %s

                [다가오는 일정]
                %s

                반드시 아래 형식을 지키세요.

                우선순위:
                (가장 먼저 해야 할 공부 한 줄)

                추천 계획:
                (과목명 + 공부시간 한 줄)

                피드백:
                (격려 또는 조언 한 줄)

                추가 조건

                - 한국어로 작성
                - 전체 180자 이하
                - 최대 3줄
                - 같은 내용 반복 금지
                - 긴 문단 금지
                - 입력 데이터에 없는 사실은 절대 만들지 말 것
                - 마크다운(**, -, # 등) 사용 금지
                - 제목 추가 금지
                """
                .formatted(
                        valueOrDefault(
                                request.getGoalTitle(),
                                "등록된 목표 없음"
                        ),
                        request.getStudyHours(),
                        request.getTargetHours(),
                        request.getAchievementRate(),
                        valueOrDefault(
                                request.getSubjectRatios(),
                                "오늘 공부 기록 없음"
                        ),
                        valueOrDefault(
                                request.getUpcomingSchedules(),
                                "다가오는 일정 없음"
                        )
                );
    }

    private String valueOrDefault(
            String value,
            String defaultValue
    ) {

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return value;
    }

}