package com.praveen.career.matching;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class SemanticMatchingService {
    private final MatchingService matchingService;
    public SemanticMatchingService(MatchingService matchingService){this.matchingService=matchingService;}

    public SemanticMatchResponse analyze(MatchRequest request){
        MatchResponse b=matchingService.match(request); List<String> recommendations=new ArrayList<>();
        if(!b.missingSkills().isEmpty()) recommendations.add("Prioritize evidence for: "+String.join(", ",b.missingSkills()));
        if(b.experienceFit()<70) recommendations.add("The posting appears to request more years of experience than the resume explicitly demonstrates.");
        if(b.roleFit()<70) recommendations.add("Make the target role alignment clearer in the summary and most recent experience.");
        if(b.evidenceCount()<5) recommendations.add("Low-confidence score: the posting contains limited structured technical evidence, so treat this result as directional.");
        if(b.score()>=85) recommendations.add("Strong fit. Prepare concrete architecture, scale, trade-off, and production-ownership examples for the matched requirements.");
        else if(b.score()>=65) recommendations.add("Good potential fit. Strengthen resume bullets around the highest-impact missing or weak requirements.");
        else recommendations.add("Material gaps remain. Prioritize roles closer to your demonstrated stack or build evidence for the core missing requirements.");

        String confidence=b.evidenceCount()>=7?"high":b.evidenceCount()>=5?"moderate":"limited";
        String summary="Weighted fit: "+b.technicalCoverage()+"% technical coverage, "+b.experienceFit()+"% experience fit, and "+b.roleFit()+"% role alignment. Evidence confidence is "+confidence+".";
        return new SemanticMatchResponse("deterministic-v3",b.score(),b.matchedSkills(),b.missingSkills(),b.requiredSkills(),b.preferredSkills(),b.technicalCoverage(),b.experienceFit(),b.roleFit(),b.evidenceCount(),summary,recommendations);
    }
}
