package net.bytle.type;

public class AttributeEnumForTest {


  public static Attribute<String> BLUE = new AttributeString<String>() {
    @Override
    public String getName() {
      return "blue";
    }
  };


}
