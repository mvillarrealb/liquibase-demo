package org.mvillabe.books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.gcp.pubsub.PubSubAdmin;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.google.cloud.storage.*;

/**
 * Startup Listener to create required buckets and topics
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationListener {

    private final PubSubAdmin pubSubAdmin;

    private final Storage storage;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStarted() {
        try {
            log.info("Creating startup settings");
            storage.create(BucketInfo.of("attachments"), Storage.BucketTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));
            pubSubAdmin.createTopic("books.created");
            log.info("Created startup settings");
        } catch(Throwable throwable) {
            log.error("Error creating startup settings {}", throwable.getMessage(), throwable);
        }
    }
}
