package net.bytle.cli;

import net.bytle.type.env.OsEnvs;
import org.junit.Assert;
import org.junit.Test;

public class CliParserValueAsEnvVariableTest {

    @Test
    public void envVariableTest() {



      CliCommand cli = CliCommand.createRootWithEmptyInput("env");

      String wordName = "--input";
      String env_variable_name = "ENV_VARIABLE_TEST";
      cli.addProperty(wordName)
                .setEnvName(env_variable_name);


      // The value found is the value of the environment variable
      String env_variable_value = "3";
      OsEnvs.add(env_variable_name, env_variable_value);
        CliParser cliParser = cli.parse();
        String inputValue = cliParser.getString(wordName);
        Assert.assertEquals("the value in the env variable must be found", env_variable_value, inputValue);
        OsEnvs.remove(env_variable_name);

    }
}
