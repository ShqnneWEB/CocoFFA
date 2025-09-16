/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.dvs.versioning;

import hu.geri.libs.boostedyaml.block.implementation.Section;
import hu.geri.libs.boostedyaml.dvs.Pattern;
import hu.geri.libs.boostedyaml.dvs.Version;
import hu.geri.libs.boostedyaml.dvs.versioning.Versioning;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ManualVersioning
implements Versioning {
    private final Version documentVersion;
    private final Version defaultsVersion;

    public ManualVersioning(@NotNull Pattern pattern, @Nullable String documentVersionId, @NotNull String defaultsVersionId) {
        this.documentVersion = documentVersionId == null ? null : pattern.getVersion(documentVersionId);
        this.defaultsVersion = pattern.getVersion(defaultsVersionId);
    }

    @Override
    @Nullable
    public Version getDocumentVersion(@NotNull Section document, boolean defaults) {
        return defaults ? this.defaultsVersion : this.documentVersion;
    }

    @Override
    @NotNull
    public Version getFirstVersion() {
        return this.defaultsVersion.getPattern().getFirstVersion();
    }

    public String toString() {
        return "ManualVersioning{documentVersion=" + this.documentVersion + ", defaultsVersion=" + this.defaultsVersion + '}';
    }
}

