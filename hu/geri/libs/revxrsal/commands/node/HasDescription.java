/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public interface HasDescription {
    @Nullable
    @Contract(pure=true)
    public String description();
}

