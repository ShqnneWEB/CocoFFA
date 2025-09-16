/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter;

import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import java.util.Comparator;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class PrioritySpec {
    private static final PrioritySpec DEFAULT = new PrioritySpec((o1, o2) -> 0);
    private static final PrioritySpec LOWEST = new PrioritySpec((o1, o2) -> {
        if (o1.getClass() == o2.getClass()) {
            return 0;
        }
        return 1;
    });
    private static final PrioritySpec HIGHEST = new PrioritySpec((o1, o2) -> {
        if (o1.getClass() == o2.getClass()) {
            return 0;
        }
        return -1;
    });
    private final Comparator<ParameterType<?, ?>> comparator;

    public PrioritySpec(Comparator<ParameterType<?, ?>> comparator) {
        this.comparator = comparator;
    }

    @Contract(value="-> new")
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull
    public static PrioritySpec lowest() {
        return LOWEST;
    }

    @NotNull
    public static PrioritySpec highest() {
        return HIGHEST;
    }

    @NotNull
    public static PrioritySpec defaultPriority() {
        return DEFAULT;
    }

    @Contract(pure=true, value="-> new")
    @NotNull
    public Builder toBuilder() {
        return new Builder(this.comparator);
    }

    public Comparator<ParameterType<?, ?>> comparator() {
        return this.comparator;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        PrioritySpec that = (PrioritySpec)obj;
        return Objects.equals(this.comparator, that.comparator);
    }

    public int hashCode() {
        return Objects.hash(this.comparator);
    }

    public String toString() {
        return "PrioritySpec[comparator=" + this.comparator + ']';
    }

    public static class Builder {
        private Comparator<ParameterType<?, ?>> comparator;

        public Builder() {
            this((o1, o2) -> 0);
        }

        public Builder(Comparator<ParameterType<?, ?>> comparator) {
            this.comparator = comparator;
        }

        @NotNull
        public Builder addComparator(@NotNull Comparator<ParameterType<?, ?>> newComparator) {
            this.comparator = newComparator.thenComparing(this.comparator);
            return this;
        }

        @NotNull
        public Builder higherThan(Class<? extends ParameterType<?, ?>> parameterType) {
            Comparator c = (o1, o2) -> {
                if (parameterType.isAssignableFrom(o2.getClass())) {
                    return -1;
                }
                if (parameterType.isAssignableFrom(o1.getClass())) {
                    return 1;
                }
                return 0;
            };
            return this.addComparator(c);
        }

        @NotNull
        public Builder lowerThan(Class<? extends ParameterType<?, ?>> parameterType) {
            Comparator c = (o1, o2) -> {
                if (parameterType.isAssignableFrom(o2.getClass())) {
                    return 1;
                }
                if (parameterType.isAssignableFrom(o1.getClass())) {
                    return -1;
                }
                return 0;
            };
            return this.addComparator(c);
        }

        @Contract(value="-> new", pure=true)
        @NotNull
        public PrioritySpec build() {
            return new PrioritySpec(this.comparator);
        }
    }
}

