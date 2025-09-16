/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.route.implementation;

import hu.geri.libs.boostedyaml.route.Route;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class SingleKeyRoute
implements Route {
    private final Object key;

    public SingleKeyRoute(@NotNull Object key) {
        this.key = Objects.requireNonNull(key, "Route cannot contain null keys!");
    }

    @Override
    @NotNull
    public String join(char separator) {
        return this.key.toString();
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    @NotNull
    public Object get(int i) {
        if (i != 0) {
            throw new ArrayIndexOutOfBoundsException("Index " + i + " for single key route!");
        }
        return this.key;
    }

    @Override
    @NotNull
    public Route parent() {
        throw new IllegalArgumentException("Empty routes are not allowed!");
    }

    @Override
    @NotNull
    public Route add(@NotNull Object key) {
        return Route.from(this.key, Objects.requireNonNull(key, "Route cannot contain null keys!"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Route)) {
            return false;
        }
        Route that = (Route)o;
        if (that.length() != 1) {
            return false;
        }
        return Objects.equals(this.key, that.get(0));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key);
    }

    public String toString() {
        return "SingleKeyRoute{key=" + this.key + '}';
    }
}

