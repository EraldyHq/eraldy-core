package net.bytle.log;


import org.junit.Assert;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;


public class LogTest {

    @Test
    public void basicTest() {

        Log log = Logs.createFromClazz(LogTest.class);


        log.info("bouh");
        log.info("bouh");

        Assert.assertEquals("Number of handlers", 1, log.getHandlers().length);

        log.setFormat("%1$tH:%1$tM:%1$tS - %2$s - %4$s - %5$s%n");
        log.info("bouh");
        Assert.assertEquals("Number of handlers", 1, log.getHandlers().length);
        log.setFormat(Log.DEFAULT_FORMAT);

    }

    @Test
    public void twoLoggers() {


        Logger LOGGER_FIRST = Logs.createFromClazz(LogTest.class);

        LOGGER_FIRST.info("first");

        Logger LOGGER_SECOND =
                Logs
                        .createFromClazz(LogTest.class);

        LOGGER_SECOND.info("second");
        LOGGER_FIRST.info("first");

        // We must see 3 messages
    }

    @Test
    public void colors() {


        Logger logger = Logs.createFromClazz(LogTest.class);
        logger.setLevel(Level.INFO);
        logger.severe("Red ?");
        logger.info("Normal Color?");

    }

    @Test
    public void minimalistLog() {


        Logger LOGGER = Logs.createFromClazz(LogTest.class).setFormat("%5$s%n");
        Logs.setLevel(Level.INFO);
        LOGGER.info("We will see only this message, no time, ...");

    }

}
