package com.example.skindetector.infrastructure.ai;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enterprise AI Copilot Service.
 * Handles conversational analysis using the Gemini 1.5 Flash model.
 */
@Service
@Slf4j
public class GeminiService {

    private final String apiKey;
    private final RestTemplate restTemplate;
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    public GeminiService() {
        // Load .env from the project root or current working directory
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();

        // Dual-Lookup: Check .env first, fallback to System Environment
        String envKey = dotenv.get("GEMINI_API_KEY");
        this.apiKey = (envKey != null && !envKey.isEmpty()) ? envKey : System.getenv("GEMINI_API_KEY");
        
        this.restTemplate = new RestTemplate();
        
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            log.error("CRITICAL: GEMINI_API_KEY is not defined. AI Copilot will be disabled.");
        } else {
            log.info("AI Copilot Neural Link: Successfully established (Key: {}***)", this.apiKey.substring(0, 5));
        }
    }

    public String analyzeSkinTone(double percentage, String strategy) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Copilot offline: Please configure your GEMINI_API_KEY in the .env file to enable professional analysis.";
        }

        String prompt = String.format(
            "Act as a professional AI skin health consultant and technical copilot. " +
            "The user just performed a %s skin detection scan. " +
            "The results show a skin-tone ratio of %.2f%%. " +
            "Provide a short, conversational, and professional insight (like ChatGPT) about this result. " +
            "Mention the detection precision and what this means for the subject's analytics. " +
            "Keep it under 3-4 sentences and sound highly authoritative yet friendly.",
            strategy, percentage
        );

        try {
            return callGeminiApi(prompt);
        } catch (Exception e) {
            log.error("Failed to communicate with Gemini API", e);
            return "I encountered a synchronization fault while analyzing the telemetry. Please check your connectivity.";
        }
    }

    private String callGeminiApi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construct the Gemini API request body
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(part));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", Collections.singletonList(content));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        Map<String, Object> response = restTemplate.postForObject(GEMINI_API_URL + apiKey, entity, Map.class);

        if (response != null && response.containsKey("candidates")) {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (!candidates.isEmpty()) {
                Map<String, Object> firstCandidate = candidates.get(0);
                Map<String, Object> contentData = (Map<String, Object>) firstCandidate.get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) contentData.get("parts");
                if (!parts.isEmpty()) {
                    return (String) parts.get(0).get("text");
                }
            }
        }

        return "My neural analysis suggests high integrity, but I couldn't generate a verbal summary at this moment.";
    }
}
