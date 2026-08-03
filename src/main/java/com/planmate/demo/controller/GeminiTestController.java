package com.planmate.demo.controller;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeminiTestController {

    @GetMapping("/api/gemini/test")
    public String testGemini() {

        try (Client client = new Client()) {

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-3.6-flash",
                            "한국어로 'Gemini 연결 성공'이라고 짧게 답해줘.",
                            null
                    );

            return response.text();

        } catch (Exception e) {
            e.printStackTrace();
            return "Gemini 연결 실패: " + e.getMessage();
        }
    }
}
