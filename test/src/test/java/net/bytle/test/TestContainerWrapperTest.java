package net.bytle.test;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestContainerWrapperTest {

    @Test
    public void databaseCommandTest() {
        TestContainerWrapper sqlServer = new TestContainerWrapper("sqlserver", "microsoft/mssql-server-linux:2017-latest")
                .withEnv("ACCEPT_EULA", "Y")
                .withEnv("SA_PASSWORD", "TheSecret1!")
                .withPort(1433);
        System.out.println(sqlServer.createDockerCommand());
    }

    @Test
    public void httpPrivilegedPortTest() {
        Path path = Paths.get("src", "test", "resources", "eraldy-test");
        TestContainerWrapper httpBin = new TestContainerWrapper("httpbin", "kennethreitz/httpbin:latest")
                .withBindMount(path, "/workdir")
                .withPort(80);
        System.out.println(httpBin.createDockerCommand());
        httpBin.startContainer().stop();


        httpBin = new TestContainerWrapper("httpbin", "kennethreitz/httpbin:latest")
                .withBindMount(path, "/workdir")
                .withPort(8081,80);
        System.out.println(httpBin.createDockerCommand());
        httpBin.startContainer().stop();

    }
}
