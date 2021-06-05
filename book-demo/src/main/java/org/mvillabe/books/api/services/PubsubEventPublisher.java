package org.mvillabe.books.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvillabe.books.api.common.CommonEvent;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Primary
@Slf4j
public class PubsubEventPublisher implements ApplicationEventPublisher {

    private final PubSubTemplate eventPublisher;

    private final ObjectMapper objectMapper;

    @Override
    public void publishEvent(Object event) {
        if(event instanceof CommonEvent) {
            CommonEvent commonEvent = (CommonEvent) event;
            try {
                log.info("Preparing to publish async event {}", event);
                eventPublisher.publish(commonEvent.getTopicId(), objectMapper.writeValueAsBytes(event))
                              .completable()
                              .orTimeout(10, TimeUnit.SECONDS)
                              .whenComplete(this::onComplete);
            } catch (JsonProcessingException e) {
                onError(e);
            }
        }
    }

    private void onComplete(String s, Throwable throwable) {
        if(throwable != null) {
           onError(throwable);
        } else {
            log.info("sucessfully published async event on pubsub with payload {}", s);
        }
    }

    private void onError(Throwable throwable) {
        log.error("Error publishing element on pubsub with cause {}", throwable.getMessage(), throwable);
    }
}
