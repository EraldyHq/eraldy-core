package net.bytle.type;

public abstract class ManifestAttribute<T> extends AttributeString<T>{

  @Override
  public String toString() {
    return super.getNormalizedName().toCamelCase();
  }

}
