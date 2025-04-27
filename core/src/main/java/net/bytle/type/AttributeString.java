package net.bytle.type;


public abstract class AttributeString<T> extends AttributeAbs<T> {


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
