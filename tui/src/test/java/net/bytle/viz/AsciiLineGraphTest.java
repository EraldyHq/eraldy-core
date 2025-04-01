package net.bytle.viz;

import org.junit.Test;

import java.util.stream.IntStream;

public class AsciiLineGraphTest {


  @Test
  public void baseLineGraph() {

    int collectionSize = 70;
    int height = 5;
    int cycle = 6;
    double[] data = IntStream.range(0, collectionSize + 1)
      .mapToDouble(i -> height * Math.sin(i * ((Math.PI * cycle) / collectionSize)))
      .toArray();
    System.out.println(AsciiLineGraph.fromSeries(data).plot());

  }

}
