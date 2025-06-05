package com.eraldy.docker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DockerImageTest {

  @Test
  void baseline() {

    DockerImage busyBox = DockerImage.create("busybox");
    Assertions.assertEquals("docker.io/library/busybox:latest", busyBox.getCanonicalName());

  }

}
