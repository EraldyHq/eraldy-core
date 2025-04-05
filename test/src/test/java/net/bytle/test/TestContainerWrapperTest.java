package net.bytle.test;

import org.junit.Test;

public class TestContainerWrapperTest {

  @Test
  public void commandTest() {
    TestContainerWrapper sqlServer = new TestContainerWrapper("sqlserver", "microsoft/mssql-server-linux:2017-latest")
      .withEnv("ACCEPT_EULA", "Y")
      .withEnv("SA_PASSWORD", "TheSecret1!")
      .withPort(1433);
    System.out.println(sqlServer.createDockerCommand());
  }
}
