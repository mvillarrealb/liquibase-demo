package org.mvillabe.books.api.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvillabe.books.domain.events.CommonEvent;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Primary
@Slf4j
public class PubSubEventListener {

    private final PubSubTemplate eventPublisher;

    private final ObjectMapper objectMapper;

    @Async
    @EventListener
    public void onCommonEvent(CommonEvent commonEvent) {
        try {
            log.info("Preparing to publish async event {}", commonEvent);
            eventPublisher.publish(commonEvent.getTopicId(), objectMapper.writeValueAsBytes(commonEvent))
                    .completable()
                    .orTimeout(15, TimeUnit.SECONDS)
                    .whenComplete(this::onComplete);
        } catch (JsonProcessingException e) {
            log.error("Error serializing payload for event {} with message: {}",commonEvent, e.getMessage(), e);
        }
    }

    private void onComplete(String s, Throwable throwable) {
        if(throwable != null) {
           onError(throwable);
        } else {
            log.info("Sucessfully published async event on pubsub with payload {}", s);
        }
    }

    private void onError(Throwable throwable) {
        log.error("Error publishing element on pubsub with cause {}", throwable.getMessage(), throwable);
    }
}
