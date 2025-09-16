/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.VirtualComponentRenderer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface VirtualComponent
extends TextComponent {
    @NotNull
    public Class<?> contextType();

    @NotNull
    public VirtualComponentRenderer<?> renderer();
}

