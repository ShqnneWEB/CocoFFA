/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common;

import java.io.Serializable;

public class SpecVersion
implements Serializable {
    private final int major;
    private final int minor;

    public SpecVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public String getRepresentation() {
        return this.major + "." + this.minor;
    }

    public String toString() {
        return "Version{major=" + this.major + ", minor=" + this.minor + '}';
    }
}

