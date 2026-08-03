package com.planmate.demo.service;

import com.planmate.demo.dashboard.dto.AiLearningRequest;
import org.springframework.stereotype.Service;

@Service
public class AiLearningService {

    private final GeminiService geminiService;

    public AiLearningService(
            GeminiService geminiService
    ) {
        this.geminiService = geminiService;
    }

    public String generateRecommendation(
            AiLearningRequest request
    ) {

        return geminiService
                .generateLearningFeedback(request);

    }

}