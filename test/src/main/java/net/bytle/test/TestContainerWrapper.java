package net.bytle.test;

import com.github.dockerjava.api.model.Bind;
import net.bytle.os.Oss;
import net.bytle.type.Strings;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static net.bytle.os.Oss.MAX_PORT_NUMBER;
import static net.bytle.os.Oss.MIN_PORT_NUMBER;

/**
 * A wrapper around test container
 * * to implement the singleton container <a href="https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers">documentation</a>
 * * to print the command so that the container is long-lived
 * <p>
 * Features, if the port is not available:
 * * the container will not be started
 * * otherwise, it will be started and the docker command will be printed (in order to help the dev)
 * <p>
 * WaitingFor is not part of the api.
 * You can also use the correspondent test container in the constructor
 * that will have most of this default value.
 */
public class TestContainerWrapper {

  private final GenericContainer<?> container;
  private final String image;
  private String hostName = "localhost";
  private Integer hostPort;
  private Integer containerPort;
  private final String name;

  public TestContainerWrapper(String name, String dockerImageName) {
    this.name = name;
    this.image = dockerImageName;
    this.container = new GenericContainer<>(dockerImageName);
  }

  /**
   * By default, you should use this constructor as the pre-created container
   * of test container have also defaults
   * such as the WaitingFor conditions against logs to check if the container has started.
   */
  @SuppressWarnings("unused")
  public TestContainerWrapper(String name, GenericContainer<?> container) {
    this.container = container;
    this.name = name;
    this.image = container.getDockerImageName();
  }

  /**
   * Start the container if it's not detected already running (by port)
   */
  public TestContainerWrapper startContainer() {

    /**
     * Container port check
     */
    if (containerPort == null) {
      throw new RuntimeException("The container port should not be null");
    }
    /**
     * Host port init
     */
    if (this.hostPort == null) {
      if (this.containerPort >= MIN_PORT_NUMBER && this.containerPort <= MAX_PORT_NUMBER) {
        this.hostPort = containerPort;
        container.setPortBindings(List.of(hostPort + ":" + containerPort));
      }
    } else {
      if (this.hostPort < MIN_PORT_NUMBER || this.hostPort > MAX_PORT_NUMBER) {
        throw new RuntimeException("The host port is a privileged port" + hostPort + ". Choose one above " + MIN_PORT_NUMBER + " and below " + MAX_PORT_NUMBER);
      }
    }

    /**
     * Do we need to start the container
     */
    boolean startContainer = true;
    if (hostPort != null) {
      if (!Oss.portAvailable(hostPort)) {
        startContainer = false;
        System.out.println("The port " + hostPort + " is already busy, the container will not start.");
      } else {
        System.out.println("The port is available, the container will start");
      }
    }


    if (startContainer) {

      System.out.println("Starting the container");
      System.out.println("If you don't want to start and stop the container for each test.");
      if (this.hostPort == null) {
        System.out.println("Set a host port. Your container port " + this.containerPort + " is a privileged port and cannot be used on the host. Choose one above " + MIN_PORT_NUMBER + " and below " + MAX_PORT_NUMBER);
      } else {
        System.out.println("You can start it with the following command on Windows:");
        System.out.println();
        System.out.println(this.createDockerCommand());
      }
      System.out.println();

      container.start();
      this.hostName = container.getHost();
      if (this.hostPort == null) {
        this.hostPort = container.getFirstMappedPort();
      }


    } else {

      System.out.println("The container is already started");


    }

    return this;
  }

  public String createDockerCommand() {
    String windowsLineSeparator = "^" + Strings.EOL;
    String bashLineSeparator = "\\" + Strings.EOL;
    List<String> separators = Arrays.asList(windowsLineSeparator, bashLineSeparator);
    String spaces = "    ";

    StringBuilder stringBuilder = new StringBuilder();
    for (String separator : separators) {
      stringBuilder.append(Strings.EOL);
      if (separator.equals(windowsLineSeparator)) {
        stringBuilder.append("Cmd:").append(Strings.EOL);
      } else {
        stringBuilder.append("Bash:").append(Strings.EOL);
      }
      stringBuilder.append("docker run ").append(separator);
      for (Object env : container.getEnv()) {
        stringBuilder.append(spaces).append("-e ").append(env).append(" ").append(separator);
      }
      for (Bind bind : container.getBinds()) {
        // https://docs.docker.com/engine/storage/bind-mounts/#syntax
        String hostPath = bind.getPath();
        String containerPath = bind.getVolume().getPath();
        stringBuilder.append(spaces).append("--volume ").append(hostPath).append(":").append(containerPath).append(" ").append(separator);
      }
      stringBuilder
        .append(spaces).append("-p ").append(hostPort).append(":").append(containerPort).append(" ").append(separator)
        .append(spaces).append("-d ").append(separator)
        .append(spaces).append("--name ").append(this.name).append(" ").append(separator)
        .append(spaces).append(this.image).append(Strings.EOL);
    }


    return stringBuilder.toString();
  }

  public TestContainerWrapper withEnv(String key, String value) {
    this.container.withEnv(key, value);
    return this;
  }

  /**
   * @param containerPort - the container port
   *                      When the container starts, it will try to bind it to this port or use another one
   */
  public TestContainerWrapper withPort(Integer containerPort) {
    this.containerPort = containerPort;
    this.container.addExposedPort(containerPort);

    return this;
  }

  public TestContainerWrapper withPort(Integer hostPort, Integer containerPort) {
    this.hostPort = hostPort;
    this.containerPort = containerPort;
    container.setPortBindings(List.of(hostPort + ":" + containerPort));
    container.withExposedPorts(containerPort);
    return this;
  }

  @SuppressWarnings("unused")
  public String getHostName() {
    return this.hostName;
  }

  @SuppressWarnings("unused")
  public Integer getHostPort() {
    return this.hostPort;
  }

  @SuppressWarnings("unused")
  public boolean isRunning() {
    return container.isRunning();
  }

  /**
   * Note that Test Container Ryuk monitor and terminate Testcontainers containers on JVM exit
   */
  public void stop() {
    container.stop();
  }


  /**
   * [Bind Mount](https://docs.docker.com/engine/storage/bind-mounts/)
   *
   * @param containerPath - the container path
   * @param hostPath      - the host path
   */
  public TestContainerWrapper withBindMount(Path hostPath, String containerPath) {
    if (!Files.exists(hostPath)) {
      throw new RuntimeException("The host path (" + hostPath + ") does not exists");
    }
    this.container.withFileSystemBind(hostPath.toAbsolutePath().toString(), containerPath, BindMode.READ_WRITE);
    return this;
  }

  public GenericContainer<?> getContainer() {
    return this.container;
  }

}
