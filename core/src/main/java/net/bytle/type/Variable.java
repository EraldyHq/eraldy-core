package net.bytle.type;

import net.bytle.exception.CastException;
import net.bytle.exception.NoValueException;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A super set of a key pair value
 * adding functionality such:
 * * as encryption/decryption
 * * key case independence
 */
public class Variable implements Comparable<Variable> {


  private Attribute attribute;

  private final Origin origin;

  /**
   * Raw/first value
   */
  private Object originalValue;

  /**
   * A decrypted value by a passphrase
   * If this value is not null, the original value is encrypted
   * A value that was template processed or that was calculated by a function
   * if the value does not exist
   */
  private Object clearValue;


  /**
   * The unique normalized identifier for a variable by name
   */
  private final String normalizedKey;

  /**
   * A function that gives the value
   */
  private Supplier<?> valueProvider;

  private Variable(Attribute attribute, Origin origin) {

    this.attribute = attribute;
    this.normalizedKey = KeyNormalizer.create(this.attribute.toString()).toCliLongOptionName();
    if (origin == null) {
      throw new IllegalArgumentException("The origin of the variable (" + this + ") was null, it should not");
    }
    // origin is important for security reason, that's why it is in the constructor
    this.origin = origin;

  }

  public static Variable create(String name, Origin origin) {
    return createWithClass(name, origin, String.class);
  }


  public static Variable createWithClass(String name, Origin origin, Class<?> clazz) {
    return createWithClassAndDefault(name, origin, clazz, null);
  }

  public static Variable createWithClassAndDefault(String name, Origin origin, Class<?> clazz, Object defaultValue) {

    Attribute attributeFromName = new Attribute() {

      @Override
      public String getDescription() {
        return name;
      }

      @Override
      public Class<?> getValueClazz() {
        return clazz;
      }

      @Override
      public Object getDefaultValue() {
        return defaultValue;
      }

      @Override
      public String toString() {
        return name;
      }

    };

    return new Variable(attributeFromName, origin);
  }

  public static Variable create(Attribute attribute, Origin origin) {
    return new Variable(attribute, origin);
  }

  /**
   * @param originalValue - the raw/origina value as found in the file
   * @return the variable for chaining
   */
  public Variable setOriginalValue(Object originalValue) {
    if (this.originalValue != null && !originalValue.equals(this.originalValue)) {
      throw new RuntimeException("You can't change the original value of the variable " + this + " from (" + this.originalValue + ") to " + this.originalValue);
    }
    Class<?> valueClazz = this.attribute.getValueClazz();
    if (valueClazz == null) {
      throw new ClassCastException("The class of the attribute " + this.attribute + " should not be null");
    }
    try {
      this.originalValue = Casts.cast(originalValue, valueClazz);
    } catch (CastException e) {
      // It's not a secret as it's the original value
      throw new ClassCastException("The value " + originalValue + " of " + this.getAttribute() + " is not a " + valueClazz);
    }
    return this;
  }


  public Origin getOrigin() {
    return this.origin;
  }


  public Object getOriginalValue() {
    return this.originalValue;
  }

  /**
   * @return the value to be used in the application in clear and cast as specified by the {@link Attribute#getValueClazz()}
   */
  public Object getValueOrDefault() throws NoValueException {

    Object valueOrDefaultNonCasted = this.getValueOrDefaultNonCasted();

    Class<?> valueClazz = this.attribute.getValueClazz();
    if (valueClazz == null) {
      return valueOrDefaultNonCasted;
    }
    try {
      return Casts.cast(valueOrDefaultNonCasted, valueClazz);
    } catch (CastException e) {
      /**
       * TODO: should be when setting the value
       */
      throw new ClassCastException(e.getMessage());
    }

  }

  public Object getValueOrDefaultNonCasted() throws NoValueException {
    try {

      return this.getValue();

    } catch (NoValueException e) {

      Object value = this.attribute.getDefaultValue();
      if (value != null) {
        return value;
      }

      throw new NoValueException("No value or default value found");
    }

  }


  @Override
  public int compareTo(Variable o) {
    return this.normalizedKey.compareTo(o.normalizedKey);
  }

  public Variable setClearValue(Object decrypted) {
    this.clearValue = decrypted;
    return this;
  }

  @Override
  public String toString() {
    /**
     * No clear value in the log
     */
    return this.getPublicName() + " = " + Strings.createFromObjectNullSafe(this.originalValue);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return this.normalizedKey.equals(((Variable) o).normalizedKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.normalizedKey);
  }

  public Attribute getAttribute() {
    return this.attribute;
  }

  public <T> T getValueOrDefaultCastAs(Class<T> clazz) throws NoValueException, CastException {
    Object object = this.getValueOrDefault();
    return Casts.cast(object, clazz);
  }


  public String getUniqueName() {
    return this.normalizedKey;
  }

  /**
   * @return the name in a public format fashion
   */
  public String getPublicName() {

    return this.normalizedKey;

  }

  public Object getValue() throws NoValueException {
    if (this.clearValue != null) {
      return this.clearValue;
    }
    if (this.originalValue != null) {
      return this.originalValue;
    }
    if (this.valueProvider != null) {
      return this.valueProvider.get();
    }
    throw new NoValueException("No value found");
  }

  public boolean hasNullValue() {
    try {
      this.getValueOrDefault();
      return true;
    } catch (NoValueException e) {
      return false;
    }
  }

  public Object getValueOrDefaultOrNull() {
    try {
      return this.getValueOrDefault();
    } catch (NoValueException e) {
      return null;
    }
  }

  public String getWebName() {
    return Key.toUriName(this.attribute.toString());
  }

  /**
   * @return the string value or the empty string if not found
   */
  public String getValueOrDefaultAsStringNotNull() {
    try {
      return String.valueOf(getValueOrDefault());
    } catch (NoValueException e) {
      return "";
    }
  }

  /**
   * @param valueProvider - the function that should return the default value (use it if you want to get the value at runtime)
   * @return the variable
   */
  public Variable setValueProvider(Supplier<?> valueProvider) {
    this.valueProvider = valueProvider;
    return this;
  }


  public void setAttribute(Attribute attribute) {
    this.attribute = attribute;
  }

}
