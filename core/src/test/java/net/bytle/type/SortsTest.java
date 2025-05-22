package net.bytle.type;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SortsTest {

  @Test
  void baselineNaturalSort() {

    List<String> actual = Stream.of("img12.png", "img10.png", "img2.png", "img1.png")
      .sorted(Sorts::naturalSortComparator)
      .collect(Collectors.toList());
    List<String> expected = Arrays.asList("img1.png", "img2.png", "img10.png", "img12.png");
    Assertions.assertEquals(expected, actual);

  }
}
