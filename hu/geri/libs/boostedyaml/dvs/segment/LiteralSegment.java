/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.dvs.segment;

import hu.geri.libs.boostedyaml.dvs.segment.Segment;
import java.util.Arrays;

public class LiteralSegment
implements Segment {
    private final String[] elements;

    public LiteralSegment(String ... elements) {
        this.elements = elements;
    }

    @Override
    public int parse(String versionId, int index) {
        for (int i = 0; i < this.elements.length; ++i) {
            if (!versionId.startsWith(this.elements[i], index)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public String getElement(int index) {
        return this.elements[index];
    }

    @Override
    public int getElementLength(int index) {
        return this.elements[index].length();
    }

    @Override
    public int length() {
        return this.elements.length;
    }

    public String toString() {
        return "LiteralSegment{elements=" + Arrays.toString(this.elements) + '}';
    }
}

