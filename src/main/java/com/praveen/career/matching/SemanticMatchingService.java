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
        if(!b.missingSkills().isEmpty()) recommendations.add("Prioritize truthful evidence for: "+String.join(", ",b.missingSkills()));
        if(b.experienceFit()<70) recommendations.add("The posting appears to request stronger or more explicit experience evidence than the resume demonstrates.");
        if(b.roleFit()<70) recommendations.add("The target role family is not strongly demonstrated in the resume; make relevant responsibilities clearer where truthful.");
        if(b.evidenceCount()<5) recommendations.add("Limited job evidence: treat this score as directional rather than precise.");
        if(b.score()>=85) recommendations.add("Strong fit. Prepare architecture, scale, trade-off, and production-ownership examples for interview validation.");
        else if(b.score()>=65) recommendations.add("Potential fit. Strengthen evidence around the highest-impact weak or missing requirements.");
        else recommendations.add("Material gaps remain. Prioritize roles closer to demonstrated experience or build genuine evidence for core requirements.");
        String confidence=b.evidenceCount()>=9?"high":b.evidenceCount()>=6?"moderate":"limited";
        String summary="Evidence-weighted fit: "+b.technicalCoverage()+"% technical coverage, "+b.experienceFit()+"% experience fit, and "+b.roleFit()+"% role alignment. Overall confidence is "+confidence+"; the score also considers responsibility evidence and missing requirements.";
        return new SemanticMatchResponse("deterministic-v5",b.score(),b.matchedSkills(),b.missingSkills(),b.requiredSkills(),b.preferredSkills(),b.technicalCoverage(),b.experienceFit(),b.roleFit(),b.evidenceCount(),summary,recommendations);
    }
}
