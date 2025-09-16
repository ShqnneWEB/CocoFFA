/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.route;

import hu.geri.libs.boostedyaml.route.Route;
import hu.geri.libs.boostedyaml.settings.general.GeneralSettings;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class RouteFactory {
    private final char separator;
    private final String escapedSeparator;

    public RouteFactory(@NotNull GeneralSettings generalSettings) {
        this.separator = generalSettings.getRouteSeparator();
        this.escapedSeparator = generalSettings.getEscapedSeparator();
    }

    public RouteFactory(char separator) {
        this.separator = separator;
        this.escapedSeparator = Pattern.quote(String.valueOf(separator));
    }

    public RouteFactory() {
        this.separator = (char)46;
        this.escapedSeparator = GeneralSettings.DEFAULT_ESCAPED_SEPARATOR;
    }

    @NotNull
    public Route create(String route) {
        return Route.fromString(route, this);
    }

    public char getSeparator() {
        return this.separator;
    }

    @NotNull
    public String getEscapedSeparator() {
        return this.escapedSeparator;
    }
}

