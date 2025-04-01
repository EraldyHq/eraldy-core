package net.bytle.type;

import net.bytle.exception.CastException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CastsTest {


  @Test
  public void castStringBigintTest() throws CastException {
    String source = "0";
    BigInteger target = new BigInteger("0");
    Assert.assertEquals("Same data", target, Casts.cast(source, BigInteger.class));
  }

  @Test
  public void castListToArray() throws CastException {
    List<Object> lists = Arrays.asList("1", "2", "3");
    Integer[] listsArray = Casts.castToArray(lists, Integer.class);
    Assert.assertEquals(listsArray.length, lists.size());
    Assert.assertEquals(listsArray[0], (Integer) 1);
    Assert.assertEquals(listsArray[1], (Integer) 2);
    Assert.assertEquals(listsArray[2], (Integer) 3);
  }

  @Test
  public void castObjectToArray() throws CastException {
    Object[] lists = {"1", "2", "3"};
    String[] listsArray = Casts.cast(lists, String[].class);
    Assert.assertEquals(listsArray.length, lists.length);
    Assert.assertEquals(listsArray[0], "1");
    Assert.assertEquals(listsArray[1], "2");
    Assert.assertEquals(listsArray[2], "3");
  }

  @Test
  public void castToListFromArrayTest() throws CastException {
    Object[] arrays = {"1", "2", "3"};
    List<Integer> list = Casts.castToList(arrays, Integer.class);
    Assert.assertEquals(arrays.length, list.size());
    Assert.assertEquals(Integer.valueOf(arrays[0].toString()), list.get(0));
    Assert.assertEquals(Integer.valueOf(arrays[1].toString()), list.get(1));
    Assert.assertEquals(Integer.valueOf(arrays[2].toString()), list.get(2));
  }

  @Test
  public void castToListFromSetTest() throws CastException {
    Object[] arrays = {"1", "2", "3"};
    Set<Object> set = new HashSet<>(Arrays.asList(arrays));
    List<Integer> list = Casts.castToList(set, Integer.class);
    Assert.assertEquals(arrays.length, list.size());
    Assert.assertEquals(Integer.valueOf(arrays[0].toString()), list.get(0));
    Assert.assertEquals(Integer.valueOf(arrays[1].toString()), list.get(1));
    Assert.assertEquals(Integer.valueOf(arrays[2].toString()), list.get(2));
  }

  @Test
  public void castToPathTest() throws CastException {
    Path path = Paths.get(".").toAbsolutePath();
    String stringPath = path.toString();
    Path castPath = Casts.cast(stringPath, Path.class);
    Assert.assertEquals(path, castPath);
  }

  @Test
  public void castToEnumTest() throws CastException {

    CastsEnum castEnum = Casts.cast("test", CastsEnum.class);
    Assert.assertEquals(CastsEnum.TEST, castEnum);

    CastsEnum castEnumFromValueOf;
    try {
      CastsEnum.valueOf("test");
    } catch (IllegalArgumentException e) {
      // throws because it does not match exactly
    }
    castEnumFromValueOf = CastsEnum.valueOf("TEST");
    Assert.assertEquals(CastsEnum.TEST, castEnumFromValueOf);

  }

  @SuppressWarnings({"ConstantConditions", "CastCanBeRemovedNarrowingVariableType"})
  @Test
  public void castsNull() {

    Object o = null;
    String s = (String) o;
    Assert.assertNull(s);

  }
}
