package com.praveen.career.matching;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MatchingServiceTest {

    private final MatchingService service = new MatchingService();

    @Test
    void calculatesMatchScoreAndMissingSkills() {
        MatchRequest request = new MatchRequest(
                "Java Spring Boot AWS Docker",
                "Looking for Java, Spring Boot, Kafka and AWS experience"
        );

        MatchResponse response = service.match(request);

        assertThat(response.score()).isEqualTo(75);
        assertThat(response.matchedSkills()).contains("java", "spring boot", "aws");
        assertThat(response.missingSkills()).containsExactly("kafka");
    }

    @Test
    void returnsZeroWhenNoKnownSkillsAreRequired() {
        MatchResponse response = service.match(new MatchRequest(
                "Java developer",
                "Strong communication and collaboration"
        ));

        assertThat(response.score()).isZero();
        assertThat(response.matchedSkills()).isEmpty();
        assertThat(response.missingSkills()).isEmpty();
    }
}
