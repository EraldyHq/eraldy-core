package net.bytle.type;

import net.bytle.exception.CastException;
import net.bytle.exception.IllegalArgumentExceptions;
import net.bytle.type.time.Date;
import net.bytle.type.time.Time;
import net.bytle.type.time.Timestamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


public class Casts {

  protected static Set<String> nullableStrings = new HashSet<>(Arrays.asList("", "null", "na"));


  /**
   * Cast to a collection
   */
  public static <T, E> T cast(Object sourceObject, Class<T> typeClazz, Class<E> elementClass) throws CastException {

    if (sourceObject == null) {
      return null;
    }

    if (elementClass == null) {
      return cast(sourceObject, typeClazz);
    }

    boolean isCollection = Collection.class.isAssignableFrom(typeClazz);
    if (!isCollection) {
      throw new CastException("The class " + typeClazz + " is not a collection");
    }

    if (sourceObject.getClass().equals(typeClazz)) {
      if (sourceObject.getClass().getComponentType() == elementClass) {
        return typeClazz.cast(sourceObject);
      }
    }

    // Collection Target Type or Element Type is different
    // We need to create a new one and to add the element
    Collection<E> target;
    try {
      //noinspection unchecked
      target = (Collection<E>) typeClazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new CastException(e);
    } catch (NoSuchMethodException e) {
      throw new CastException(typeClazz + " is a interface, not a collection type", e);
    }
    for (Object value : (Collection<?>) sourceObject) {
      target.add(Casts.cast(value, elementClass));
    }
    return typeClazz.cast(target);


  }


  /**
   * @param sourceObject - the object to cast
   * @param targetClass  - the class to cast
   * @param <T>          - the receiver class
   * @return null if the object is null, throw an exception if the class is not the expected one
   * the object to the asked clazz
   * @throws CastException when the cast does not work
   *                       If the class is an interface, we just check if it's an instance of and fail otherwise
   *                       Example: Number can be an integer, a float, a double, ...
   */
  public static <T> T cast(Object sourceObject, Class<T> targetClass) throws CastException {

    /*
     * Null
     */
    if (sourceObject == null) {
      return null;
    }


    if (targetClass == Number.class) {
      if (sourceObject instanceof Number) {
        /**
         * Number is an interface and
         * can't be instantiated
         * We just return the object
         */
        return (T) sourceObject;
      }
      throw new CastException("The source object is not a number. Value: " + sourceObject);
    }

    try {

      Class<?> sourceObjectClass = sourceObject.getClass();

      /**
       * Same class
       */
      if (sourceObjectClass.equals(targetClass)) {
        return targetClass.cast(sourceObject);
      }

      /**
       * Array
       */
      if (sourceObjectClass.isArray()) {
        if (!targetClass.isArray()) {
          if (targetClass.equals(String.class)) {
            String[] values = castToArray(sourceObject, String.class);
            //noinspection unchecked
            return (T) String.join(", ", values);
          }
          throw new CastException("The source object is an array and the target class is not");
        }
        //noinspection unchecked
        return (T) castToArray(sourceObject, targetClass.getComponentType());
      }

      /**
       * Nullable string
       */
      if (targetClass != String.class && sourceObjectClass == String.class) {
        if (nullableStrings.contains(sourceObject)) {
          return null;
        }
      }

      /**
       * Long
       */
      if (targetClass == Long.class) {
        return targetClass.cast(Longs.createFromObject(sourceObject).toLong());
      }


      /**
       * Key Normalizer
       */
      if (targetClass == KeyNormalizer.class) {
        if (sourceObject.getClass() != String.class) {
          throw new CastException("A string source object is mandatory to cast to KeyNormalizer. The source object is not a string but a " + sourceObject.getClass().getSimpleName());
        }
        return targetClass.cast(KeyNormalizer.create(sourceObject));
      }

      /**
       * Uri Enhanced
       */
      if (targetClass == UriEnhanced.class) {
        String uri = sourceObject.toString();
        try {

          return targetClass.cast(UriEnhanced.createFromString(uri));

        } catch (Exception e) {
          String message = "The string `" + uri + "` is not a valid uri.";
          if (uri.startsWith("\"") || uri.startsWith("'")) {
            message += " You should delete the character quote.";
          }
          message += " Error: " + e.getMessage();
          throw new CastException(message, e);
        }

      }

      if (targetClass == DnsName.class) {

        String dnsNameAsString = sourceObject.toString();
        return targetClass.cast(DnsName.create(dnsNameAsString));

      }

      /**
       * Boolean
       */
      if (targetClass == Boolean.class) {
        return targetClass.cast(Booleans.createFromObject(sourceObject).toBoolean());
      }
      /**
       * Integer and Smallint
       */
      if (targetClass == Integer.class) {
        return targetClass.cast(Integers.createFromObject(sourceObject).toInteger());
      }

      /**
       * Big integer
       */
      if (targetClass == BigInteger.class) {
        return targetClass.cast(BigIntegers.createFromObject(sourceObject).toBigInteger());
      }

      /**
       * Big Decimal (exact number), Numeric, decimal
       */
      if (targetClass == BigDecimal.class) {
        return targetClass.cast(BigDecimals.createFromObject(sourceObject).toBigDecimal());
      }

      /**
       * Float Double precision
       */
      if (targetClass == Double.class) {
        return targetClass.cast(Doubles.createFromObject(sourceObject).toDouble());
      }

      /**
       * Float Single precision
       */
      if (targetClass == Float.class) {
        /**
         * Not really error proof against precision error but yeah
         * Float is no more used
         */
        return targetClass.cast(Doubles.createFromObject(sourceObject).toFloat());
      }

      /**
       * Date
       */
      if (targetClass == java.sql.Date.class) {
        return targetClass.cast(Date.createFromObject(sourceObject).toSqlDate());
      }
      if (targetClass == LocalDate.class) {
        return targetClass.cast(Date.createFromObject(sourceObject).toLocalDate());
      }

      /**
       * Timestamp
       */
      if (targetClass == java.sql.Timestamp.class) {
        return targetClass.cast(Timestamp.createFromObject(sourceObject).toSqlTimestamp());
      }
      if (targetClass == LocalDateTime.class) {
        return targetClass.cast(Timestamp.createFromObject(sourceObject).toLocalDateTime());
      }
      if (targetClass == java.util.Date.class) {
        return targetClass.cast(Date.createFromObject(sourceObject).toDate());
      }


      /**
       * String
       */
      if (targetClass == String.class) {
        return targetClass.cast(sourceObject.toString());
      }

      /**
       * Character
       */
      if (targetClass == Character.class) {
        if (sourceObject.toString().length() != 1) {
          throw new CastException("The source object is not a string of length 1 (" + sourceObject + ")");
        }
        return targetClass.cast(sourceObject.toString().charAt(0));
      }

      /**
       * Time
       */
      if (targetClass == java.sql.Time.class) {
        return targetClass.cast(Time.createFromObject(sourceObject).toSqlTime());
      }

      /**
       * Time
       */
      if (targetClass == java.sql.SQLXML.class) {
        boolean isSqlXmlObject = java.sql.SQLXML.class.isAssignableFrom(sourceObject.getClass());
        if (isSqlXmlObject) {
          return targetClass.cast(sourceObject);
        }
        if (sourceObject.getClass() == String.class) {
          return targetClass.cast(SqlXmlFromString.create(sourceObject.toString()));
        }
        throw new CastException("The source value is not a string, nor a java.sql.SQLXML object");
      }

      /**
       * Clob
       */
      if (targetClass == java.sql.Clob.class) {
        return targetClass.cast(SqlClob.createFromObject(sourceObject));
      }

      /**
       * Path from string
       */
      if (targetClass == java.nio.file.Path.class) {
        return targetClass.cast(Paths.get(sourceObject.toString()));
      }

      /**
       * Enum
       */
      if (targetClass.isEnum()) {
        /**
         * {@link Enums#valueOf(Class, String)} is not used
         * because it needs exact match
         */
        KeyNormalizer normalizedLookupKey;
        if (sourceObject instanceof KeyNormalizer) {
          normalizedLookupKey = (KeyNormalizer) sourceObject;
        } else {
          normalizedLookupKey = KeyNormalizer.create(sourceObject.toString());
        }
        for (T constant : targetClass.getEnumConstants()) {
          if (constant == null) {
            throw new InternalError("The enum class (" + targetClass + ") does not have any constants");
          }
          if (KeyNormalizer.create(constant).equals(normalizedLookupKey)) {
            return constant;
          }
        }
        throw new ClassCastException("We couldn't cast the value (" + sourceObject + ") with the class (" + sourceObjectClass.getSimpleName() + ") to the enum class (" + targetClass.getSimpleName() + "). Possible values: " + Enums.toConstantAsStringCommaSeparated(targetClass));
      }

      /**
       * Charset
       */
      if (targetClass == Charset.class) {
        String charsetValue = sourceObject.toString();
        if (!Charset.isSupported(charsetValue)) {
          throw new IllegalCharsetNameException("The character set value (" + charsetValue + ") is not supported. You may set the character set to one of this values: " + String.join(", ", Charset.availableCharsets().keySet()));
        }
        return targetClass.cast(Charset.forName(charsetValue));

      }

      /**
       * If we are here, we have not yet a
       * transformation,
       * we try to cast it directly
       */
      try {
        return targetClass.cast(sourceObject);
      } catch (ClassCastException e) {
        throw new ClassCastException("We couldn't cast the value (" + sourceObject + ") with the class (" + sourceObjectClass.getSimpleName() + ") to the class (" + targetClass.getSimpleName() + ")");
      }

    } catch (IllegalCharsetNameException | ClassCastException e) {
      throw new CastException(e.getMessage(), e);
    }

  }

  public static <T> T[] castToArray(List<T> list) {

    //noinspection unchecked
    return (T[]) list.toArray();

  }

  /**
   * @param object      - a sequence of single values
   * @param targetClazz - the class to cast
   * @param <T>         - the type of class to return
   * @return A mix of Source: Effective Java; Item 26
   * <p>
   * (E[])new Object[INITIAL_ARRAY_LENGTH]
   * <p>
   * and
   * <p>
   * {@link Array#newInstance(Class, int)}
   */
  public static <T> T[] castToArray(Object object, Class<T> targetClazz) throws CastException {

    Class<?> valueObjectClass = object.getClass();

    if (targetClazz.isArray()) {
      throw new CastException("The target clazz should not be an array");
    }

    if (valueObjectClass.isArray()) {
      if (valueObjectClass.getComponentType() == targetClazz) {
        //noinspection unchecked
        return (T[]) object;
      }
      List<T> target = new ArrayList<>();
      for (Object value : (Object[]) object) {
        target.add(Casts.cast(value, targetClazz));
      }
      //noinspection unchecked
      return target.toArray((T[]) Array.newInstance(targetClazz, target.size()));
    }

    if (object instanceof Collection) {
      /**
       * The cast of the generic throw an unchecked
       * warning that is not true
       */
      List<T> target = new ArrayList<>();
      for (Object value : (Collection<?>) object) {
        target.add(Casts.cast(value, targetClazz));
      }
      //noinspection unchecked
      return target.toArray((T[]) Array.newInstance(targetClazz, target.size()));
    }

    throw new CastException("We could cast the value to an array of (" + targetClazz + ")");
  }


  /**
   * @param object    - the object to cast
   * @param clazzK    - the key class
   * @param clazzV    - the value class
   * @param <K>       - the type of key
   * @param <V>       - the type of value
   * @param strictKey - When casting to a new map, you may want a strict key and a loose value
   * @return the map
   */
  public static <K, V> Map<K, V> castToNewMap(Object object, Class<K> clazzK, Class<V> clazzV, Boolean strictKey) throws CastException {
    Map<?, ?> map;
    if (!(object instanceof Map)) {
      throw new ClassCastException("The object is not a map but a " + object.getClass().getSimpleName() + " and can't be then casted");
    } else {
      map = (Map<?, ?>) object;
    }

    Map<K, V> result = new HashMap<>();
    for (Map.Entry<?, ?> e : map.entrySet()) {
      K key;
      if (!strictKey) {
        key = Casts.cast(e.getKey(), clazzK);
      } else {
        Object elementKey = e.getKey();
        if (!clazzK.equals(elementKey.getClass())) {
          throw new CastException("The key (" + elementKey + ") is not a " + clazzK.getSimpleName() + ".");
        }
        //noinspection unchecked
        key = (K) elementKey;
      }
      result.put(
        key,
        Casts.cast(e.getValue(), clazzV)
      );
    }

    return result;

  }

  /**
   * Cast a map to another one b y creating a new map
   *
   * @param object - the object to cast
   * @param clazzK - the key class
   * @param clazzV - the value class
   * @param <K>    - the type of key
   * @param <V>    - the type of value
   * @return the map
   */
  public static <K, V> Map<K, V> castToNewMap(Object object, Class<K> clazzK, Class<V> clazzV) throws CastException {

    return castToNewMap(object, clazzK, clazzV, false);

  }

  /**
   * Same function as {@link #castToSameMap(Object, Class, Class)}
   * but without exception. To use when you know the data in advance.
   */
  public static <K, V> Map<K, V> castToSameMapSafe(Object object, Class<K> clazzK, Class<V> clazzV) {
    try {
      return castToSameMap(object, clazzK, clazzV);
    } catch (CastException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * This function cast the object to a map and don't create new one (meaning that the object
   * are not cast)
   *
   * @param object - an object
   * @param clazzK - the key class
   * @param clazzV - the value class
   * @param <K>    the key type
   * @param <V>    the value type
   * @return the same object but cast
   * @throws CastException if there is a problem
   */
  public static <K, V> Map<K, V> castToSameMap(Object object, Class<K> clazzK, Class<V> clazzV) throws CastException {

    if (!(object instanceof Map)) {
      throw new CastException("The object (value: " + object + ") is not a map but a " + object.getClass().getSimpleName() + " and can't be then casted");
    }

    Map.Entry<?, ?> firstElement = ((Map<?, ?>) object).entrySet().iterator().next();
    if (firstElement == null) {
      return (Map<K, V>) object;
    }
    if (firstElement.getKey() != null && !clazzK.equals(Object.class)) {
      if (!firstElement.getKey().getClass().equals(clazzK)) {
        throw new CastException("The key (" + firstElement.getKey() + ") is not a " + clazzK.getSimpleName() + " but a " + firstElement.getKey().getClass().getName());
      }
    }
    if (firstElement.getValue() != null && !clazzV.equals(Object.class)) {
      if (!firstElement.getValue().getClass().equals(clazzV)) {
        throw new CastException("The key (" + firstElement.getValue() + ") is not a " + clazzV.getSimpleName() + ".");
      }
    }

    return (Map<K, V>) object;

  }

  /**
   * Cast a list of unknown class to a list of clazz.
   *
   * @param clazz - the target class to cast
   * @param o     - a {@link Collection collection (list,array)} or an array
   * @param <T>   the return type
   * @return the list
   */
  public static <T> List<T> castToList(Object o, Class<T> clazz) throws CastException {

    if (o == null) {
      return null;
    }


    if (o instanceof List) {
      List<?> list = (List<?>) o;
      List<T> returnList = new ArrayList<>();
      for (Object object : list) {
        if (object.getClass() == clazz) {
          //noinspection unchecked
          return (List<T>) list;
        }
        returnList.add(cast(object, clazz));
      }
      return returnList;
    }

    if (o instanceof Collection) {
      Collection<?> array = ((Collection<?>) o);
      List<T> returnList = new ArrayList<>();
      for (Object object : array) {
        returnList.add(cast(object, clazz));
      }
      return returnList;
    }

    if (o.getClass().isArray()) {
      Object[] array = (Object[]) o;
      List<T> returnList = new ArrayList<>();
      for (Object object : array) {
        returnList.add(cast(object, clazz));
      }
      return returnList;
    }

    throw new IllegalArgumentException("The object is not a collection (list, set) nor an array but a " + o.getClass().getSimpleName() + " and can't therefore be cast to a list");

  }


  @SuppressWarnings("unused")
  public static <T> Set<T> toSameSet(Object object, Class<T> clazzV) {

    Set<?> set;
    if (!(object instanceof Set)) {
      throw new ClassCastException("The object (value: " + object + ") is not a set but a " + object.getClass().getSimpleName() + " and can't be then casted");
    } else {
      set = (Set<?>) object;
    }

    for (Object value : set) {
      if (!clazzV.equals(Object.class)) {
        if (!clazzV.isAssignableFrom(value.getClass())) {
          throw new ClassCastException("The value (" + value + ") is not a " + clazzV.getSimpleName() + ".");
        }
      }
    }

    //noinspection unchecked
    return (Set<T>) object;

  }

  /**
   * A cast that throws errors only at runtime
   *
   * @param value  the value
   * @param aClass the class
   * @param <T>    the type
   * @return the cast object
   */
  public static <T> T castSafe(Object value, Class<T> aClass) {
    try {
      return cast(value, aClass);
    } catch (CastException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> List<T> castToListSafe(Object o, Class<T> aClass) {
    try {
      return castToList(o, aClass);
    } catch (CastException e) {
      throw IllegalArgumentExceptions.createFromValue(o, e);
    }
  }

  public static <K, V> Map<K, V> castToNewMapSafe(Object o, Class<K> kClass, Class<V> vClass) {
    try {
      return castToNewMap(o, kClass, vClass);
    } catch (CastException e) {
      throw new ClassCastException(e.getMessage());
    }
  }

  public static <T> Collection<T> castToCollection(Object o, Class<T> clazzV) {
    if (o == null) {
      return null;
    }
    if (o instanceof Collection) {
      Collection<?> array = ((Collection<?>) o);
      for (Object object : array) {
        if (!clazzV.equals(Object.class)) {
          if (!clazzV.isAssignableFrom(object.getClass())) {
            throw new ClassCastException("The value (" + object + ") is not a " + clazzV.getSimpleName() + ".");
          }
        }
      }
      //noinspection unchecked
      return (Collection<T>) array;
    }
    throw new ClassCastException("The object (" + o + ") is not a collection but a " + o.getClass().getSimpleName());

  }


  /**
   * Converts a Reader (character stream) to a String.
   *
   * @param reader The character stream to read from
   * @return The string content from the reader
   * @throws CastException If an I/O error occurs during reading
   */
  @SuppressWarnings("unused")
  public static String castReaderToString(Reader reader) throws CastException {

    if (reader == null) {
      return null;
    }

    StringBuilder stringBuilder = new StringBuilder();
    try (BufferedReader bufferedReader = new BufferedReader(reader)) {
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        stringBuilder.append(line);
        stringBuilder.append(System.lineSeparator());
      }
    } catch (IOException e) {
      throw new CastException(e);
    }

    return stringBuilder.toString();

  }


}
