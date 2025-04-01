package net.bytle.type;

public enum AttributeEnumForTest implements Attribute {

  BLUE
  ;


  @Override
  public String getDescription() {
    return this.toString();
  }

  @Override
  public Class<?> getValueClazz() {
    return  String.class;
  }

  @Override
  public Object getDefaultValue() {
    return null;
  }


}
