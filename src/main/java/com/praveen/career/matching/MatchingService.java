package com.praveen.career.matching;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MatchingService {
    private static final Map<String,List<String>> SKILLS=new LinkedHashMap<>();
    static {
        SKILLS.put("java",List.of("java","jdk")); SKILLS.put("spring boot",List.of("spring boot","springboot")); SKILLS.put("spring webflux",List.of("spring webflux","webflux","reactive programming")); SKILLS.put("spring security",List.of("spring security")); SKILLS.put("microservices",List.of("microservices","microservice architecture")); SKILLS.put("rest api",List.of("rest api","restful","rest services")); SKILLS.put("graphql",List.of("graphql")); SKILLS.put("python",List.of("python")); SKILLS.put("kotlin",List.of("kotlin")); SKILLS.put("typescript",List.of("typescript")); SKILLS.put("javascript",List.of("javascript")); SKILLS.put("angular",List.of("angular")); SKILLS.put("react",List.of("react","reactjs","react.js")); SKILLS.put("kafka",List.of("kafka","apache kafka")); SKILLS.put("rabbitmq",List.of("rabbitmq","rabbit mq")); SKILLS.put("aws",List.of("aws","amazon web services")); SKILLS.put("azure",List.of("azure","microsoft azure")); SKILLS.put("gcp",List.of("gcp","google cloud")); SKILLS.put("docker",List.of("docker","containerization")); SKILLS.put("kubernetes",List.of("kubernetes","k8s")); SKILLS.put("jenkins",List.of("jenkins")); SKILLS.put("github actions",List.of("github actions")); SKILLS.put("ci/cd",List.of("ci/cd","continuous integration","continuous delivery","continuous deployment")); SKILLS.put("postgresql",List.of("postgresql","postgres")); SKILLS.put("mysql",List.of("mysql")); SKILLS.put("oracle",List.of("oracle database","oracle db","oracle sql")); SKILLS.put("sql server",List.of("sql server","mssql")); SKILLS.put("sql",List.of("sql")); SKILLS.put("mongodb",List.of("mongodb","mongo db")); SKILLS.put("dynamodb",List.of("dynamodb","dynamo db")); SKILLS.put("cosmos db",List.of("cosmos db","cosmosdb")); SKILLS.put("snowflake",List.of("snowflake")); SKILLS.put("redis",List.of("redis")); SKILLS.put("linux",List.of("linux","unix")); SKILLS.put("maven",List.of("maven")); SKILLS.put("gradle",List.of("gradle")); SKILLS.put("junit",List.of("junit")); SKILLS.put("mockito",List.of("mockito")); SKILLS.put("terraform",List.of("terraform")); SKILLS.put("oauth",List.of("oauth","oauth2","openid connect"));
    }
    private static final Pattern YEARS=Pattern.compile("(\\d{1,2})\\+?\\s*(?:years?|yrs?)",Pattern.CASE_INSENSITIVE);
    private static final List<String> PREFERRED_MARKERS=List.of("preferred","nice to have","bonus","plus","desired");
    private static final List<String> REQUIRED_MARKERS=List.of("required","must have","minimum qualification","basic qualification","requirements","you will need");

    public MatchResponse match(MatchRequest request){
        String resume=normalize(request.resumeText()),job=normalize(request.jobDescription());
        Set<String> jobSkills=extract(job),resumeSkills=extract(resume);
        Set<String> preferred=classify(job,jobSkills,PREFERRED_MARKERS);
        Set<String> required=new LinkedHashSet<>(jobSkills); required.removeAll(preferred);
        // If the posting does not label requirements clearly, all recognized technical skills are treated as core evidence.
        if(required.isEmpty()&&!jobSkills.isEmpty()) required.addAll(jobSkills);
        Set<String> matched=new LinkedHashSet<>(jobSkills); matched.retainAll(resumeSkills);
        Set<String> missing=new LinkedHashSet<>(jobSkills); missing.removeAll(resumeSkills);

        double requiredCoverage=coverage(required,resumeSkills);
        double preferredCoverage=coverage(preferred,resumeSkills);
        int technical=(int)Math.round(requiredCoverage*85+preferredCoverage*15);
        if(preferred.isEmpty()) technical=(int)Math.round(requiredCoverage*100);

        int requiredYears=maxYears(job); int resumeYears=maxYears(resume);
        int experienceFit=requiredYears==0?70:Math.min(100,(int)Math.round(resumeYears*100.0/requiredYears));
        int roleFit=roleFit(resume,job);
        int evidence=jobSkills.size()+(requiredYears>0?1:0)+(roleFit!=70?1:0);

        int score=(int)Math.round(technical*.72+experienceFit*.18+roleFit*.10);
        // Confidence penalty: a broad fit claim requires enough evidence from the posting.
        if(evidence<3) score=Math.min(score,65); else if(evidence<5) score=Math.min(score,82);
        // 100 should be exceptional even for keyword-complete resumes because this engine cannot assess interview depth or recency.
        score=Math.min(score,96);
        return new MatchResponse(score,matched,missing,required,preferred,technical,experienceFit,roleFit,evidence);
    }

    private Set<String> classify(String job,Set<String> skills,List<String> markers){
        Set<String> result=new LinkedHashSet<>();
        for(String skill:skills){int pos=job.indexOf(skill); if(pos<0)continue; int from=Math.max(0,pos-100),to=Math.min(job.length(),pos+100); String window=job.substring(from,to); if(markers.stream().anyMatch(window::contains))result.add(skill);}
        return result;
    }
    private int roleFit(String resume,String job){
        List<String> families=List.of("backend","front end","frontend","full stack","fullstack","software engineer","software developer","data engineer","devops","platform engineer");
        Set<String> jobRoles=new HashSet<>(),resumeRoles=new HashSet<>(); for(String f:families){if(job.contains(f))jobRoles.add(f.replace("front end","frontend").replace("fullstack","full stack"));if(resume.contains(f))resumeRoles.add(f.replace("front end","frontend").replace("fullstack","full stack"));}
        if(jobRoles.isEmpty())return 70; jobRoles.retainAll(resumeRoles); return jobRoles.isEmpty()?45:100;
    }
    private int maxYears(String text){Matcher m=YEARS.matcher(text);int max=0;while(m.find())max=Math.max(max,Integer.parseInt(m.group(1)));return max;}
    private double coverage(Set<String> target,Set<String> resume){if(target.isEmpty())return 0;long n=target.stream().filter(resume::contains).count();return n*1.0/target.size();}
    private Set<String> extract(String text){Set<String> found=new LinkedHashSet<>();SKILLS.forEach((canonical,aliases)->{if(aliases.stream().anyMatch(a->containsPhrase(text,a)))found.add(canonical);});return found;}
    private boolean containsPhrase(String text,String phrase){return Pattern.compile("(?<![a-z0-9])"+Pattern.quote(phrase)+"(?![a-z0-9])").matcher(text).find();}
    private String normalize(String value){return value==null?"":value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9+#./-]+"," ").replaceAll("\\s+"," ").trim();}
}
