package net.bytle.type;

/**
 * An interface for an attribute.
 * Every attribute should be created with {@link AttributeAbs}
 * so that the equality function works
 * <p>
 * Attribute and not property because the product is called `tabulify`
 * <p></p>
 *
 */
public interface Attribute<T> {


  /**
   *
   * @return the name of an attribute
   * ie the unique key
   * This value is normalized with {@link KeyNormalizer} so that the case
   * does not have any effect
   */
  String getName();

  /**
   * @return the description of the attribute
   */
  String getDescription();

  /**
   * Optional the class of the value
   * It may be a java class or an enum class
   * (that could/should implement {@link AttributeValue} to define a domain)
   * It's used to validate the value when a {@link Variable} is created
   */
  Class<T> getClazz();

  /**
   *
   * @return a fix default value
   */
  T getDefaultValue();

  /**
   * @return the {@link KeyNormalizer} so that the name can be output in any case wanted case
   */
  KeyNormalizer getNormalizedName();

  /**
   * @return if the {@link #getClazz()} is a list or map, the class of the value or null
   */
  Class<?> getValueClazz();

  /**
   * @return if the {@link #getClazz()} is a map, the class of the key
   */
  Class<?> getKeyClazz();

  /**
   * For sorting
   */
  int compareTo(Attribute<T> o);

}
