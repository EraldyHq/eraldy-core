# Log

## About

As of today, we have created our own log facade over the Java logger.

## Why
### Why SLF4J ?

You take the SLF4J facade because:
* the logger implementation is not shipped. (The user of a library may use a single logging system and have then a single configuration file)
* the SLF4J API facade is already shipped with a lot of library
* we don't want to be able to mix logger instantiation between SLF4J and the backend

### Why Log4J ?
We have taken LOG4J because:
* of its documentation
* it's the only documentation that talks about async logging.
* it has also out of the box a cassandra appender.

## Other FYI

* Log (Slf4j + LogBack) (ie "ch.qos.logback:logback-classic:1.2.3")
* import org.apache.logging.log4j.LogManager;
