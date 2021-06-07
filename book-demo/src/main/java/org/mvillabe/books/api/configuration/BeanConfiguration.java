package org.mvillabe.books.api.configuration;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.spi.v1.HttpStorageRpc;
import org.springframework.cloud.gcp.autoconfigure.storage.GcpStorageAutoConfiguration;
import org.springframework.cloud.gcp.core.GcpProjectIdProvider;
import org.springframework.cloud.gcp.core.UserAgentHeaderProvider;
import org.springframework.cloud.gcp.pubsub.PubSubAdmin;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.support.PublisherFactory;
import org.springframework.cloud.gcp.pubsub.support.SubscriberFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.LinkedHashMap;


@Configuration
@Profile("test")
public class BeanConfiguration {

    @Bean
    public PubSubEmulatorContainer pubSubEmulatorContainer(ConfigurableEnvironment environment) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        PubSubEmulatorContainer container = new PubSubEmulatorContainer(
                DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk:316.0.0-emulators")
        );
        container.start();
        map.put("EMULATOR_HOST", container.getEmulatorEndpoint());
        MapPropertySource propertySource = new MapPropertySource("pubSubEmulatorContainer", map);
        environment.getPropertySources().addFirst(propertySource);
        return container;
    }

    @Bean
    public StorageEmulator storageEmulator() {
        StorageEmulator storageEmulator = new StorageEmulator();
        storageEmulator.start();
        return storageEmulator;
    }

    /**
     * Replace default spring cloud Storage Bean with the one intercepted by our emulator endpoint
     * @param emulator Storage Emulator instance to get forwarded port
     * @param gcpProjectIdProvider Default GCP projectId provider bean
     * @param credentialsProvider Default GCP credentialsProvider bean
     * @return Storage Bean Highjacked to
     * @throws IOException
     */
    @Bean
    @Primary
    @DependsOn({ "storageEmulator" })
    public Storage storage(
    StorageEmulator emulator,
    GcpProjectIdProvider gcpProjectIdProvider,
    CredentialsProvider credentialsProvider
    ) throws IOException {
        /*GcpStorageAutoConfiguration*/
        return StorageOptions.newBuilder()
                .setHeaderProvider(new UserAgentHeaderProvider(GcpStorageAutoConfiguration.class))
                .setProjectId(gcpProjectIdProvider.getProjectId())
                .setCredentials(credentialsProvider.getCredentials())
                .setHost(emulator.getEmulatorEndpoint())
                .build()
                .getService();
    }

    @Bean
    @Primary
    @DependsOn({ "pubSubEmulatorContainer" })
    public PubSubTemplate fileProcessor(PublisherFactory publisherFactory, SubscriberFactory subscriberFactory, PubSubAdmin pubSubAdmin) {
        return new PubSubTemplate(publisherFactory, subscriberFactory);
    }

    /**
     * This api is a tricky endpoint to provide service account dummy login
     */
    @RestController
    private class OauthController {
        @PostMapping("/oauth2/auth")
        public String auth() {
            return "{\"access_token\":\"4/P7q7W91\",\"scope\": \"read\",\"token_type\": \"Bearer\": \"expires_in\":3600}";
        }
        @PostMapping("/token")
        public String token() {
            return "{\"expires_in\":3600,\"issued_at\":\"1420262924658\",\"scope\":\"READ\",\"application_name\":\"ce1e94a2-9c3e-42fa-a2c6-1ee01815476b\",\"refresh_token_issued_at\":\"1420262924658\",\"status\":\"approved\",\"refresh_token_status\":\"approved\",\"api_product_list\":[],\"developer.email\":\"tesla@weathersample.com\",\"organization_id\":\"0\",\"token_type\":\"BearerToken\",\"refresh_token\":\"fYACGW7OCPtCNDEnRSnqFlEgogboFPMm\",\"client_id\":\"5jUAdGv9pBouF0wOH5keAVI35GBtx3dT\",\"access_token\":\"2l4IQtZXbn5WBJdL6EF7uenOWRsi\",\"organization_name\":\"docs\",\"refresh_token_expires_in\":86399,\"refresh_count\":\"0\"}";
        }
    }
}
