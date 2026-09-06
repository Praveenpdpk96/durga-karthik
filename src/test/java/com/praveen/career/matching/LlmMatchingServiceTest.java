package com.praveen.career.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class LlmMatchingServiceTest {
    @Test
    void fallsBackWithoutApiKey() {
        LlmMatchingService service = new LlmMatchingService(
                new MatchingService(), new ObjectMapper(), RestClient.builder(), false, "", "gpt-5.6-luna", "https://api.openai.com/v1/responses");
        LlmAnalysisResponse response = service.analyze(new MatchRequest("Java Spring Boot AWS", "Java Spring Boot Kafka AWS"));
        assertThat(response.aiEnhanced()).isFalse();
        assertThat(response.engine()).isEqualTo("deterministic-v5-fallback");
        assertThat(response.baselineScore()).isGreaterThan(0);
        assertThat(response.note()).contains("not enabled");
    }
}
