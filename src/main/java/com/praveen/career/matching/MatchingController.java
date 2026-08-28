package com.praveen.career.matching;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchingController {

    private final MatchingService matchingService;
    private final SemanticMatchingService semanticMatchingService;

    public MatchingController(MatchingService matchingService, SemanticMatchingService semanticMatchingService) {
        this.matchingService = matchingService;
        this.semanticMatchingService = semanticMatchingService;
    }

    @PostMapping
    public ResponseEntity<MatchResponse> match(@Valid @RequestBody MatchRequest request) {
        return ResponseEntity.ok(matchingService.match(request));
    }

    @PostMapping("/semantic")
    public ResponseEntity<SemanticMatchResponse> semanticMatch(@Valid @RequestBody MatchRequest request) {
        return ResponseEntity.ok(semanticMatchingService.analyze(request));
    }
}
