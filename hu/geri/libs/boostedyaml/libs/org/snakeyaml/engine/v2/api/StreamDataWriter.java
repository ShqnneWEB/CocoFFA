/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api;

public interface StreamDataWriter {
    default public void flush() {
    }

    public void write(String var1);

    public void write(String var1, int var2, int var3);
}

