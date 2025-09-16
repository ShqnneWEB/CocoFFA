/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.route;

import hu.geri.libs.boostedyaml.route.RouteFactory;
import hu.geri.libs.boostedyaml.route.implementation.MultiKeyRoute;
import hu.geri.libs.boostedyaml.route.implementation.SingleKeyRoute;
import java.util.Objects;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Route {
    @NotNull
    public static Route from(@NotNull Object ... route) {
        if (Objects.requireNonNull(route, "Route array cannot be null!").length == 0) {
            throw new IllegalArgumentException("Empty routes are not allowed!");
        }
        return route.length == 1 ? new SingleKeyRoute(route[0]) : new MultiKeyRoute(route);
    }

    @NotNull
    public static Route from(@NotNull Object key) {
        return new SingleKeyRoute(key);
    }

    @NotNull
    public static Route fromSingleKey(@NotNull Object key) {
        return new SingleKeyRoute(key);
    }

    @NotNull
    public static Route fromString(@NotNull String route) {
        return Route.fromString(route, '.');
    }

    @NotNull
    public static Route fromString(@NotNull String route, char separator) {
        return route.indexOf(separator) != -1 ? new MultiKeyRoute(route.split(Pattern.quote(String.valueOf(separator)))) : new SingleKeyRoute(route);
    }

    @NotNull
    public static Route fromString(@NotNull String route, @NotNull RouteFactory routeFactory) {
        return route.indexOf(routeFactory.getSeparator()) != -1 ? new MultiKeyRoute(route.split(routeFactory.getEscapedSeparator())) : new SingleKeyRoute(route);
    }

    @NotNull
    public static Route addTo(@Nullable Route route, @NotNull Object key) {
        return route == null ? Route.fromSingleKey(key) : route.add(key);
    }

    @NotNull
    public String join(char var1);

    public int length();

    @NotNull
    public Object get(int var1);

    @NotNull
    public Route add(@NotNull Object var1);

    @NotNull
    public Route parent();

    public boolean equals(Object var1);

    public int hashCode();
}

