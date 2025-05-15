package net.bytle.test;

import net.bytle.type.Strings;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Docker container to start, stop, run Docker container
 */
public class DockerContainer {

  private final Conf conf;

  public DockerContainer(Conf conf) {
    this.conf = conf;
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
      for (Map.Entry<String, String> env : conf.env.entrySet()) {
        stringBuilder.append(spaces).append("-e ")
          .append(env.getKey())
          .append("=")
          .append(env.getValue())
          .append(" ")
          .append(separator);
      }
      for (Map.Entry<Path, Path> volumeMapEntry : conf.volumeMap.entrySet()) {
        // https://docs.docker.com/engine/storage/bind-mounts/#syntax
        String hostPath = volumeMapEntry.getKey().toAbsolutePath().toString();
        String containerPath = volumeMapEntry.getValue().toAbsolutePath().toString();
        stringBuilder.append(spaces).append("--volume ").append(hostPath).append(":").append(containerPath).append(" ").append(separator);
      }
      for (Map.Entry<Integer, Integer> portMapEntry : conf.portMap.entrySet()) {
        stringBuilder.append(spaces).append("-p ").append(portMapEntry.getKey()).append(":").append(portMapEntry.getValue()).append(" ").append(separator);
      }
      stringBuilder
        .append(spaces).append("-d ").append(separator)
        .append(spaces).append("--name ").append(conf.containerName).append(" ").append(separator)
        .append(spaces).append(conf.image).append(Strings.EOL);
    }


    return stringBuilder.toString();
  }

  public static Conf createConf(String image) {
    return new Conf(image);
  }


  public static class Conf {

    private String containerName = "test-container";
    private final String image;
    private Map<Integer, Integer> portMap = new HashMap<>();
    private Map<String, String> env = new HashMap<>();
    private Map<Path, Path> volumeMap = new HashMap<>();

    public Conf(String image) {
      this.image = image;
    }

    public void setContainerName(String containerName) {
      this.containerName = containerName;
    }

    public Conf setPortBonding(Integer hostPort, Integer containerPort) {
      this.portMap.put(hostPort, containerPort);
      return this;
    }

    public Conf setVolumeBonding(Path hostPath, Path containerPath) {
      this.volumeMap.put(hostPath, containerPath);
      return this;
    }

    public Conf setEnv(String key, String value) {
      this.env.put(key, value);
      return this;
    }

    public DockerContainer build(){
      return new DockerContainer(this);
    }

  }
}
