package net.bytle.type;

/**
 * An interface for an attribute that is used against an enum
 * so that we get:
 * * a description
 * * a default value
 * <p>
 * <p>
 * attribute and not property because the product is called `tabulify`
 * <p></p>
 * Advantage over {@link AttributeNoEnum}
 * * easy casting
 * * easy switching (switch require an enum, object are not allowed)
 * * no need to create a list of all attributes (builtin)
 * Disadvantage over {@link AttributeNoEnum}
 * * No inheritance (adding a function in the interface will break all attribute)
 * * No equality for attribute created on the fly (not used though)
 */
@SuppressWarnings("deprecation")
public interface Attribute {


  /**
   * They key is the to string function
   *
   * This key is normalized {@link Key#toNormalizedKey(String)} (that is not uppercase, minus or underscore and trim dependent)
   * to:
   * - determine uniqueness
   * - cast to an enum (ie {@link Casts#cast(Object, Class)}})
   *
   * The key published to the outside world is done with the {@link Key#toCamelCaseValue(String)}
   * Public key are key that are going into external artifacts
   * such as configuration file, console output or workflow file
   *
   */


  /**
   * @return the description of the attribute
   */
  String getDescription();

  /**
   *
   * @return a fix default value
   */
  Object getDefaultValue();

  /**
   * Optional the class of the value
   * It may be:
   * * a java class
   * * or an enum class (that should implement {@link AttributeValue} for scalar value to define a domain)
   * It's used to:
   * * validate the value when a {@link Variable} is created
   * * create relational column
   * For complex value such as map and list, you need to take over
   */
  Class<?> getValueClazz();



}
