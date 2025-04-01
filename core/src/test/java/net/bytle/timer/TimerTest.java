package net.bytle.timer;

import org.junit.Test;

public class TimerTest {


    @Test
    public void baseTest() {

        Timer timer = Timer
                .create("test")
                .start();
        timer.stop();

        System.out.println(timer.getResponseTimeInMilliSeconds());
        System.out.println(timer.getResponseTimeInString());

    }
}
