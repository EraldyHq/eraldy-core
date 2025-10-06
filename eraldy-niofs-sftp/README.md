# Eraldy - Sftp File System

## Introduction

The SFTP library is a SFTP File System
that provides access to the files on an SFTP server (that is, an SSH or SCP server).

## How to use

See [How to use this library](docs/java/README.md)



## Installation

The ``sftp-X.X.X.jar`` jar file must be in the classpath as Java NIO uses the system class loader to find [installed providers](http://docs.oracle.com/javase/8/docs/api/java/nio/file/spi/FileSystemProvider.html#installedProviders--).

```bash
export classpath=../path/To/sftp-X.X.X.jar
```

It's possible to bypass this restriction but the code gets more ugly. See an example [with the groovy class Loader](docs/groovy/traversing_method_1.groovy).

The java version must be version 8:

```bash
java -version
```

Output:

```
java version "1.8.0_45"
Java(TM) SE Runtime Environment (build 1.8.0_45-b15)
Java HotSpot(TM) 64-Bit Server VM (build 25.45-b02, mixed mode)
```

## URI format
To get a [path](http://docs.oracle.com/javase/tutorial/essential/io/path.html) or a [file system](http://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystems.html), you need to provide an [URI](http://docs.oracle.com/javase/8/docs/api/java/net/URI.html).


The ``Bytle Sftp`` URI is the below one.

    sftp://[ username[: password]@] hostname[: port][ /absolutePath ]

where:

  * ``sftp`` is the scheme.
  * ``username`` and ``password`` are the username and password. They must be [encoded](https://en.wikipedia.org/wiki/Percent-encoding) as it's an URI format.
  * ``hostname`` is the host name of the SFTP, SCP, SSH server. It defaults to ``localhost``.
  * ``port`` is the port of the SFTP, SCP, SSH server. It defaults to ``22``.
  * ``absolutePath`` is an absolute path. It must then begin with a `/`. It defaults to the ``user.home`` directory. If the path is a directory, it becomes the [working directory](http://gerardnico.com/wiki/file_system/working_directory).

For information, it's considered as an absolute URI because it specifies a scheme.

## Getting Started

### With the tutorial

The [Path Operations tutorial page](http://docs.oracle.com/javase/tutorial/essential/io/pathOps.html) shows you the full URI for the local file system
```java
Path p3 = Paths.get(URI.create("file:///Users/joe/FileTest.java"));
```
To follow the same tutorial with an ``sftp server``, you just need to replace the URI. For Example:
```java
Path p3 = Paths.get(URI.create("ssh://username:password@myHostName"));
```

### By Languages

  * with Java: See [Java](docs/java)
  * with Groovy: See [Groovy](docs/groovy)


## Implementation

  * Operating System: Actually, only a Linux/Solaris/Unix Server is supported (ie the root begins with "/").
  * The Watch Service is not implemented.
  * All non implemented function returns a [UnsupportedOperationException](http://docs.oracle.com/javase/8/docs/api/java/lang/UnsupportedOperationException.html)
  * The file system is not developed for concurrency. You need to open a file system from each threads.
  * The ``Btyle Sftp`` library is a concrete implementation of the [Java NIO FileSystemProvider Interface](http://docs.oracle.com/javase/8/docs/api/java/nio/file/spi/FileSystemProvider.html). See [Developing a Custom File System Provider](http://docs.oracle.com/javase/8/docs/technotes/guides/io/fsp/filesystemprovider.html)
  * The Sftp layer is implemented with the [JCraft Jsch library](http://www.jcraft.com/jsch/)

## Dev

  * Test: See the [README](src/test/README.md) files.


