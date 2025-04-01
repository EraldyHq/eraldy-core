package net.bytle.type;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MapKeyIndependentTest {

  @Test
  public void baseTest() {

    MapKeyIndependent<Integer> map = new MapKeyIndependent<>();
    String lowercase = "smtp_to";
    map.put(lowercase,1);
    Assert.assertEquals(1, map.size());
    String uppercase = "SMTP_TO";
    map.put(uppercase,1);
    Assert.assertEquals(1, map.size());
    Assert.assertEquals((Integer) 1, map.get(uppercase));
    Assert.assertEquals((Integer) 1, map.get(lowercase));

    Map.Entry<String, Integer> entry = map.entrySet().iterator().next();
    Assert.assertEquals(uppercase, entry.getKey());

    /**
     * Put all
     */
    Map<String, Integer> collection = new HashMap<>();
    collection.put(lowercase,1);
    map.putAll(collection);
    Assert.assertEquals(1, map.size());


  }
}
