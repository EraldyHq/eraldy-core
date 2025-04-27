package net.bytle.type;

import java.util.Objects;

public abstract class AttributeAbs<T> implements Attribute<T>, Comparable<Attribute<T>> {

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
        return this.getNormalizedName().equals(((AttributeAbs<?>) o).getNormalizedName());
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
    public int compareTo(Attribute<T> o) {
        return getNormalizedName().toString().compareTo(o.getNormalizedName().toString());
    }


}
