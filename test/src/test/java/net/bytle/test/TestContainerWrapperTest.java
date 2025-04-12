package net.bytle.test;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestContainerWrapperTest {

  @Test
  public void commandTest() {
    Path path = Paths.get("src","test","resources","eraldy-test");
    TestContainerWrapper sqlServer = new TestContainerWrapper("sqlserver", "microsoft/mssql-server-linux:2017-latest")
      .withEnv("ACCEPT_EULA", "Y")
      .withEnv("SA_PASSWORD", "TheSecret1!")
            .addBindMount("/workdir", path)
      .withPort(1433);
     System.out.println(sqlServer.createDockerCommand());
  }
}
