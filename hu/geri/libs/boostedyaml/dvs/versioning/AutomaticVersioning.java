/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.dvs.versioning;

import hu.geri.libs.boostedyaml.block.implementation.Section;
import hu.geri.libs.boostedyaml.dvs.Pattern;
import hu.geri.libs.boostedyaml.dvs.Version;
import hu.geri.libs.boostedyaml.dvs.versioning.Versioning;
import hu.geri.libs.boostedyaml.route.Route;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutomaticVersioning
implements Versioning {
    private final Pattern pattern;
    private final Route route;
    private final String strRoute;

    public AutomaticVersioning(@NotNull Pattern pattern, @NotNull Route route) {
        this.pattern = pattern;
        this.route = route;
        this.strRoute = null;
    }

    public AutomaticVersioning(@NotNull Pattern pattern, @NotNull String route) {
        this.pattern = pattern;
        this.route = null;
        this.strRoute = route;
    }

    @Override
    @Nullable
    public Version getDocumentVersion(@NotNull Section document, boolean defaults) {
        return (this.route != null ? document.getOptionalString(this.route) : document.getOptionalString(this.strRoute)).map(this.pattern::getVersion).orElse(null);
    }

    @Override
    @NotNull
    public Version getFirstVersion() {
        return this.pattern.getFirstVersion();
    }

    @Override
    public void updateVersionID(@NotNull Section updated, @NotNull Section def) {
        if (this.route != null) {
            updated.set(this.route, (Object)def.getString(this.route));
        } else {
            updated.set(this.strRoute, (Object)def.getString(this.strRoute));
        }
    }

    public String toString() {
        return "AutomaticVersioning{pattern=" + this.pattern + ", route='" + (this.route == null ? this.strRoute : this.route) + '\'' + '}';
    }
}

