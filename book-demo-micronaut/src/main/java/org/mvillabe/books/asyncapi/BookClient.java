package org.mvillabe.books.asyncapi;

import io.micronaut.gcp.pubsub.annotation.PubSubClient;
import io.micronaut.gcp.pubsub.annotation.Topic;

@PubSubClient
public interface BookClient {

    @Topic("books.created")
    void send(byte[] data);
}
