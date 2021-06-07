package org.mvillabe.books;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.gcp.pubsub.PubSubAdmin;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.google.cloud.storage.*;

@Component
@RequiredArgsConstructor
public class ApplicationListener {
    private final PubSubAdmin pubSubAdmin;
    private final Storage storage;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStarted() {
        pubSubAdmin.createTopic("books.created");
        storage.create(BucketInfo.of("attachments"), Storage.BucketTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));
    }
}
