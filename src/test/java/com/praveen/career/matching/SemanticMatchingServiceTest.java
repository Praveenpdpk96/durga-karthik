package com.praveen.career.matching;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SemanticMatchingServiceTest {
    private final SemanticMatchingService service=new SemanticMatchingService(new MatchingService());
    @Test void returnsWeightedEvidenceAndRecommendations(){
        SemanticMatchResponse result=service.analyze(new MatchRequest(
                "Senior Software Engineer with 7 years Java Spring Boot AWS",
                "Software Engineer. 5+ years required. Java Spring Boot Kafka AWS Kubernetes required"
        ));
        assertThat(result.engine()).isEqualTo("deterministic-v5");
        assertThat(result.missingSkills()).contains("kafka","kubernetes");
        assertThat(result.technicalCoverage()).isBetween(50,80);
        assertThat(result.experienceFit()).isEqualTo(100);
        assertThat(result.evidenceCount()).isGreaterThanOrEqualTo(5);
        assertThat(result.summary()).contains("technical coverage");
        assertThat(result.recommendations()).isNotEmpty();
    }
}
