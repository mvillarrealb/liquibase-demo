package org.mvillabe.books.configuration;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import io.grpc.ManagedChannelBuilder;
import io.micronaut.context.annotation.*;
import io.micronaut.gcp.Modules;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.utility.DockerImageName;

import javax.inject.Named;
import java.io.IOException;

@Factory
@Requires(property = "micronaut.profile", value = "test")
public class BeanConfiguration {

    @Context
    public PubSubEmulatorContainer emulatorContainer() {
        System.out.println("mardicion");
        PubSubEmulatorContainer container = new PubSubEmulatorContainer(
                DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk:316.0.0-emulators")
        );
        container.start();
        return container;
    }

    @Context
    @Named(Modules.PUBSUB)
    public CredentialsProvider noCredentialsProvider() {
        return NoCredentialsProvider.create();
    }

    /**
     * Override default transport
     * @param emulatorContainer
     * @return
     */
    @Context
    @Named(Modules.PUBSUB)
    public TransportChannelProvider localChannelProvider(PubSubEmulatorContainer emulatorContainer) throws IOException {
        String host = emulatorContainer.getEmulatorEndpoint();
        TransportChannelProvider transportChannelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(ManagedChannelBuilder.forTarget(host).usePlaintext().build()));
        CredentialsProvider credentialsProvider = NoCredentialsProvider.create();

        SubscriptionAdminClient adminClient = SubscriptionAdminClient.create(
                SubscriptionAdminSettings.newBuilder()
                        .setTransportChannelProvider(transportChannelProvider)
                        .setCredentialsProvider(credentialsProvider)
                        .build()
        );
        TopicAdminClient topicClient = TopicAdminClient.create(
                TopicAdminSettings.newBuilder()
                                .setTransportChannelProvider(transportChannelProvider)
                                .setCredentialsProvider(credentialsProvider)
                                .build()
        );
        TopicName name = TopicName.ofProjectTopicName("test-containers", "books.created");
        Topic request = Topic.newBuilder()
                .setName(name.toString())
                .build();
        topicClient.createTopic(request);
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of("test-containers", "BookListener.onBookCreated");
        adminClient.createSubscription(subscriptionName, name, PushConfig.getDefaultInstance(), 30);
        return transportChannelProvider;
    }
}
