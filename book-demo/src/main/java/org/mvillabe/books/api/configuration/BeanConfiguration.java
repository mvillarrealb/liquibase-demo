package org.mvillabe.books.api.configuration;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gcp.autoconfigure.pubsub.GcpPubSubProperties;
import org.springframework.cloud.gcp.core.GcpProjectIdProvider;
import org.springframework.context.annotation.*;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.utility.DockerImageName;

@Profile("test")
@Configuration
public class BeanConfiguration {

    @Bean("pubSubEmulatorContainer")
    public PubSubEmulatorContainer pubSubEmulatorContainer() {
        PubSubEmulatorContainer container = new PubSubEmulatorContainer(
            DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk:316.0.0-emulators")
        );
        container.start();
        return container;
    }

    @Bean
    public StorageEmulator storageEmulator() {
        StorageEmulator storageEmulator = new StorageEmulator();
        storageEmulator.start();
        return storageEmulator;
    }

    @Bean
    @Primary
    public Storage storage(StorageEmulator emulator, GcpProjectIdProvider gcpProjectIdProvider) {
        return StorageOptions.newBuilder()
                .setProjectId(gcpProjectIdProvider.getProjectId())
                .setHost(emulator.getEmulatorEndpoint())
                .build()
                .getService();
    }

    /*Override GcpPubSubProperties bean with System property EMULATOR_HOST*/
    @Primary
    @ConfigurationProperties("spring.cloud.gcp.pubsub")
    @Autowired
    @Bean
    public GcpPubSubProperties configurationProperties(PubSubEmulatorContainer pubSubEmulatorContainer) {
        System.setProperty("EMULATOR_HOST", pubSubEmulatorContainer.getEmulatorEndpoint());
        return new GcpPubSubProperties();
    }

}
