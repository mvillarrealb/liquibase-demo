package org.mvillabe.books.api.configuration;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class StorageEmulator extends GenericContainer<StorageEmulator> {
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("fsouza/fake-gcs-server");

    private static final String CMD = "-scheme http";

    private static final int PORT = 4443;

    public StorageEmulator() {
        super(DEFAULT_IMAGE_NAME);
        withExposedPorts(PORT);
        withCommand(CMD);
    }

    public String getEmulatorEndpoint() {
        return "http://" + getContainerIpAddress() + ":" + getMappedPort(PORT);
    }
}
