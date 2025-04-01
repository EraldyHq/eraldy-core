package net.bytle.cli;

import net.bytle.type.env.OsEnvs;
import org.junit.Assert;
import org.junit.Test;

public class CliEnvTest {

  @Test
  public void envTest() {

    String appName = "test";
    String propertyName = "--conf";
    String defaultEnvConfKey = appName+"."+propertyName;
    String defaultEnvConfValue = "value for "+defaultEnvConfKey;
    CliCommand cliCommand = CliCommand.createRootWithEmptyInput(appName);
    // Change the default app home word
    cliCommand.addProperty(propertyName);

    // Test env
    OsEnvs.add(defaultEnvConfKey, defaultEnvConfValue);

    CliParser cliParser = cliCommand.parse();
    Assert.assertEquals("Equal", defaultEnvConfValue, cliParser.getString(propertyName));

    // Cleaning
    OsEnvs.remove(propertyName);

  }
}
