package com.eraldy.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DockerCliTest {

    private DockerCli dockerCli;
    private DockerClient dockerClient;
    private static final String CONTAINER_NAME = "httpbin-test-container";
    private static final String IMAGE_NAME = "kennethreitz/httpbin";

    @BeforeEach
    void setUp() {
        // Create DockerContainer with httpbin image
        DockerContainer.Conf conf = DockerContainer.createConf(IMAGE_NAME);
        conf.setContainerName(CONTAINER_NAME);
        conf.setPortBonding(8080, 80); // Map container port 80 to host port 8080

        DockerContainer dockerContainer = conf.build();
        dockerCli = new DockerCli(dockerContainer);

        // Create Docker client for verification
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();
        dockerClient = DockerClientImpl.getInstance(config, httpClient);
    }

    @AfterEach
    void tearDown() {
        // Clean up - try to remove the container
        try {
            dockerClient.stopContainerCmd(CONTAINER_NAME).exec();
            dockerClient.removeContainerCmd(CONTAINER_NAME).exec();
        } catch (Exception e) {
            // Ignore exceptions during cleanup
        }
    }

    @Test
    void testRunStopContainer() throws InterruptedException {
        // Run the container
        dockerCli.run();

        // Verify container is running
        InspectContainerResponse containerInfo = dockerClient.inspectContainerCmd(CONTAINER_NAME).exec();
        assertTrue(containerInfo.getState().getRunning(), "Container should be running after run() call");

        // Run it again, should not throw any errors
        assertDoesNotThrow(() -> dockerCli.run(), "Running an already running container should not throw errors");

        // Stop the container
        dockerCli.stop();

        // Wait a moment for the container to stop
        Thread.sleep(1000);

        // Verify container is stopped
        containerInfo = dockerClient.inspectContainerCmd(CONTAINER_NAME).exec();
        assertFalse(containerInfo.getState().getRunning(), "Container should be stopped after stop() call");

        // Remove the container
        dockerCli.rm();

        // Verify container is removed
        assertThrows(NotFoundException.class, () -> dockerClient.inspectContainerCmd(CONTAINER_NAME).exec(),
                "Container should be removed after rm() call");
    }
}
