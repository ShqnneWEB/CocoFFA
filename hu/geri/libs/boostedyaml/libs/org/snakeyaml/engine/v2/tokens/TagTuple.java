/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.tokens;

import java.util.Objects;
import java.util.Optional;

public final class TagTuple {
    private final Optional<String> handle;
    private final String suffix;

    public TagTuple(Optional<String> handle, String suffix) {
        Objects.requireNonNull(handle);
        this.handle = handle;
        Objects.requireNonNull(suffix);
        this.suffix = suffix;
    }

    public Optional<String> getHandle() {
        return this.handle;
    }

    public String getSuffix() {
        return this.suffix;
    }
}

