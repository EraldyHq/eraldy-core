package net.bytle.type;

import java.util.Objects;

/**
 * An abstract class that every {@link MediaType}
 * should extend
 * Why? Because there is the equality/hash function
 * If you create an enum, you should not forget to copy them
 */
public abstract class MediaTypeAbs implements MediaType {


    @Override
    public String getExtension() {
        return this.getSubType();
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public String toString() {
        return this.getType() + "/" + this.getSubType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaType)) return false;
        MediaType obejctMediaType = (MediaType) o;
        return this.getType().equals(obejctMediaType.getType()) && this.getSubType().equals(obejctMediaType.getSubType());
    }

}
