/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.dvs.segment;

import hu.geri.libs.boostedyaml.dvs.segment.LiteralSegment;
import hu.geri.libs.boostedyaml.dvs.segment.RangeSegment;

public interface Segment {
    public static Segment range(int start, int end, int step, int fill) {
        return new RangeSegment(start, end, step, fill);
    }

    public static Segment range(int start, int end, int step) {
        return new RangeSegment(start, end, step, 0);
    }

    public static Segment range(int start, int end) {
        return new RangeSegment(start, end, start < end ? 1 : -1, 0);
    }

    public static Segment literal(String ... elements) {
        return new LiteralSegment(elements);
    }

    public int parse(String var1, int var2);

    public String getElement(int var1);

    public int getElementLength(int var1);

    public int length();
}

