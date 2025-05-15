# Bytle Sftp - Test 


## Introduction
Some information about the test architecture.

## Sftp Server Configuration

### External Server
By default, Bytle Sftp will test with a [Mock Server](java/net/bytle/niofs/sftp/MockSshSftpServer.java).

If you want to use an external Sftp Server, you need to configure as Java System Property of environment variable, the following variables:

  * `BYTLE_SFTP_EXT_URI`: The URI used during the connection without path `sftp://user:password@host:22`
  * `BYTLE_SFTP_EXT_USER_DIR`: The home path used during the connection


### Mock Server
The [Mock Server](java/net/bytle/niofs/sftp/MockSshSftpServer.java) is not activated if the External Server is configured.
If you still want to use it, you need to set the following Java System Property of environment variable:

  * `BYTLE_SFTP_MOCK_URI`: Example: `sftp://localhost:22999`


## The Test structure

The Framework is Junit. 

All Test classes:

   * have the suffix name `Test`. 
   * generally instantiated the Sftp Servers with the [External Resource rule class: ExternalResourceFileSystemsRule.java](java/net/bytle/niofs/sftp/ExternalResourceFileSystemsRule.java).
   * generally are parametrized test over the Sftp Servers as external resource.
   * points to the [java.nio.file](https://docs.oracle.com/javase/8/docs/api/java/nio/file/package-summary.html) class that they test. For instance, the class [FilesTest.java](java/net/bytle/niofs/sftp/FilesTest.java) test the functions of the nio class [Files.java](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html) 
   
There is also a [test suite](java/net/bytle/niofs/sftp/SuiteTest.java). All the new test class must be added to this test suite as this is only class started by Maven. 

Why Are we using a test suite ?

  * To open once and close once the resources (Sftp File System)
  * Without test suite, by default, Maven (Surefire) will start the test classes parallel. The resource are actually static field, we will get then a concurrency problem and the [External Resource rule class](java/net/bytle/niofs/sftp/ExternalResourceFileSystemsRule.java) may closes the Sftp connection before that the tests are finished.

## The Test Resources

All files used for the test:

  * have the same name as prefix than their test class.


