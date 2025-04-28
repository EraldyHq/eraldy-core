package net.bytle.type;

@Deprecated
public abstract class AttributeNoEnumString<T> extends AttributeNoEnumAbs<T> {


  @Override
  public Class<T> getClazz() {

    //noinspection unchecked
    return (Class<T>) String.class;

  }


  @Override
  public T getDefaultValue() {
    //noinspection unchecked
    return (T) "";
  }

}
