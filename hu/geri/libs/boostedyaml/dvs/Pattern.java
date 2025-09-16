/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.dvs;

import hu.geri.libs.boostedyaml.dvs.Version;
import hu.geri.libs.boostedyaml.dvs.segment.Segment;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Pattern {
    private final Segment[] segments;

    public Pattern(@NotNull Segment ... segments) {
        this.segments = segments;
    }

    @Deprecated
    @NotNull
    public Segment getPart(int index) {
        return this.segments[index];
    }

    @NotNull
    public Segment getSegment(int index) {
        return this.segments[index];
    }

    @Nullable
    public Version getVersion(@NotNull String versionId) {
        int[] cursors = new int[this.segments.length];
        int start = 0;
        for (int index = 0; index < this.segments.length; ++index) {
            int cursor = this.segments[index].parse(versionId, start);
            if (cursor == -1) {
                return null;
            }
            cursors[index] = cursor;
            start += this.segments[index].getElementLength(cursor);
        }
        return new Version(versionId, this, cursors);
    }

    public Version getFirstVersion() {
        return new Version(null, this, new int[this.segments.length]);
    }

    public String toString() {
        return "Pattern{segments=" + Arrays.toString(this.segments) + '}';
    }
}

