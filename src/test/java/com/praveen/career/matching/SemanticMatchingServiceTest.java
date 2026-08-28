package com.praveen.career.matching;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SemanticMatchingServiceTest {

    private final SemanticMatchingService service = new SemanticMatchingService(new MatchingService());

    @Test
    void returnsRecommendationsWithSkillGaps() {
        SemanticMatchResponse result = service.analyze(new MatchRequest(
                "Java Spring Boot AWS",
                "Java Spring Boot Kafka AWS Kubernetes"
        ));

        assertThat(result.engine()).isEqualTo("deterministic-v1");
        assertThat(result.score()).isEqualTo(60);
        assertThat(result.missingSkills()).containsExactlyInAnyOrder("kafka", "kubernetes");
        assertThat(result.summary()).isNotBlank();
        assertThat(result.recommendations()).isNotEmpty();
    }
}
