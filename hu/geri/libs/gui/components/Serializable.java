/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.gui.components;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface Serializable {
    public List<String> encodeGui();

    public void decodeGui(@NotNull List<String> var1);
}

