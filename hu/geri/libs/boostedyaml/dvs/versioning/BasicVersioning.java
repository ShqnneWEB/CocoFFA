/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.dvs.versioning;

import hu.geri.libs.boostedyaml.dvs.Pattern;
import hu.geri.libs.boostedyaml.dvs.segment.Segment;
import hu.geri.libs.boostedyaml.dvs.versioning.AutomaticVersioning;
import org.jetbrains.annotations.NotNull;

public class BasicVersioning
extends AutomaticVersioning {
    public static final Pattern PATTERN = new Pattern(Segment.range(1, Integer.MAX_VALUE));

    public BasicVersioning(@NotNull String route) {
        super(PATTERN, route);
    }
}

