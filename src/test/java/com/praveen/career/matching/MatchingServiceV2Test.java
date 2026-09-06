package com.praveen.career.matching;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MatchingServiceV2Test {
    private final MatchingService service=new MatchingService();
    @Test void detectsBroaderRequirementsAndMissingSkills(){
        MatchResponse result=service.match(new MatchRequest(
                "Software Engineer 7 years Java Spring Boot AWS PostgreSQL Docker microservices",
                "Software Engineer 5+ years required Java Spring Boot AWS Kafka Kubernetes PostgreSQL Docker microservices"
        ));
        assertThat(result.matchedSkills()).contains("java","spring boot","aws","postgresql","docker","microservices");
        assertThat(result.missingSkills()).contains("kafka","kubernetes");
        assertThat(result.score()).isBetween(60,90);
        assertThat(result.technicalCoverage()).isBetween(70,80);
    }
    @Test void sparseEvidenceCannotClaimPerfectFit(){
        MatchResponse result=service.match(new MatchRequest("AWS","Experience with AWS"));
        assertThat(result.score()).isLessThanOrEqualTo(70);
        assertThat(result.score()).isLessThan(90);
        assertThat(result.evidenceCount()).isLessThan(3);
    }
    @Test void evenKeywordCompleteMatchDoesNotClaimCertainty(){
        MatchResponse result=service.match(new MatchRequest(
                "Software Engineer 8 years Java Spring Boot AWS Kafka Kubernetes",
                "Software Engineer 5 years Java Spring Boot AWS Kafka Kubernetes"
        ));
        assertThat(result.score()).isLessThanOrEqualTo(95);
    }
}
