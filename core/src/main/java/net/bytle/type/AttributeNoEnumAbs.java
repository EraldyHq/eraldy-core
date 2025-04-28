package net.bytle.type;

import java.util.Objects;

@Deprecated
public abstract class AttributeNoEnumAbs<T> implements AttributeNoEnum<T>, Comparable<AttributeNoEnum<T>> {

    /**
     * The name normalized so that it's unique
     * through all cases
     */
    KeyNormalizer normalizedName = null;

    @Override
    public String getDescription() {
        return this.getName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNormalizedName());
    }

    @Override
    public KeyNormalizer getNormalizedName() {
        if (normalizedName != null) {
            return normalizedName;
        }
        return KeyNormalizer.create(this.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attribute)) return false;
        return this.getNormalizedName().equals(((AttributeNoEnumAbs<?>) o).getNormalizedName());
    }

    @Override
    public Class<?> getValueClazz() {
        return null;
    }

    @Override
    public Class<?> getKeyClazz() {
        return null;
    }

    @Override
    public String toString() {
        return getNormalizedName().toString();
    }

    @Override
    public int compareTo(AttributeNoEnum<T> o) {
        return getNormalizedName().toString().compareTo(o.getNormalizedName().toString());
    }


}
