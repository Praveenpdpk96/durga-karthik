package com.praveen.career.matching;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MatchingServiceV2Test {
    private final MatchingService service = new MatchingService();

    @Test
    void detectsBroaderTechnicalRequirementsAndMissingSkills() {
        MatchResponse result = service.match(new MatchRequest(
                "Java Spring Boot AWS PostgreSQL Docker microservices",
                "Java Spring Boot AWS Kafka Kubernetes PostgreSQL Docker microservices required"
        ));
        assertThat(result.matchedSkills()).contains("java", "spring boot", "aws", "postgresql", "docker", "microservices");
        assertThat(result.missingSkills()).contains("kafka", "kubernetes");
        assertThat(result.score()).isBetween(70, 80);
    }

    @Test
    void doesNotClaimPerfectFitFromSingleDetectedKeyword() {
        MatchResponse result = service.match(new MatchRequest("AWS", "Experience with AWS"));
        assertThat(result.score()).isEqualTo(70);
    }
}
