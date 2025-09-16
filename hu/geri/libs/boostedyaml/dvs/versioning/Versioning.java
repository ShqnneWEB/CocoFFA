/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.dvs.versioning;

import hu.geri.libs.boostedyaml.block.implementation.Section;
import hu.geri.libs.boostedyaml.dvs.Version;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Versioning {
    @Nullable
    public Version getDocumentVersion(@NotNull Section var1, boolean var2);

    @NotNull
    public Version getFirstVersion();

    default public void updateVersionID(@NotNull Section updated, @NotNull Section def) {
    }
}

