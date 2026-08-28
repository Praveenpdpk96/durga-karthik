package com.praveen.career.events;

import com.praveen.career.application.JobApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@ConditionalOnProperty(name = "career.events.enabled", havingValue = "true")
public class KafkaApplicationEventPublisher implements ApplicationEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public KafkaApplicationEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${career.events.application-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void applicationCreated(JobApplication application) {
        publish("APPLICATION_CREATED", application);
    }

    @Override
    public void applicationStatusChanged(JobApplication application) {
        publish("APPLICATION_STATUS_CHANGED", application);
    }

    private void publish(String eventType, JobApplication application) {
        ApplicationEvent event = new ApplicationEvent(
                eventType,
                application.getId(),
                application.getCompany(),
                application.getRole(),
                application.getStatus(),
                Instant.now()
        );
        kafkaTemplate.send(topic, String.valueOf(application.getId()), event);
    }
}
