/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.format;

import java.util.EnumMap;
import java.util.Map;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.DecorationMap;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@ApiStatus.NonExtendable
public interface StyleGetter {
    @Nullable
    public Key font();

    @Nullable
    public TextColor color();

    @Nullable
    public ShadowColor shadowColor();

    default public boolean hasDecoration(@NotNull TextDecoration decoration) {
        return this.decoration(decoration) == TextDecoration.State.TRUE;
    }

    public @NotNull TextDecoration.State decoration(@NotNull TextDecoration var1);

    default public @Unmodifiable @NotNull Map<TextDecoration, TextDecoration.State> decorations() {
        EnumMap<TextDecoration, TextDecoration.State> decorations = new EnumMap<TextDecoration, TextDecoration.State>(TextDecoration.class);
        for (TextDecoration decoration : DecorationMap.DECORATIONS) {
            TextDecoration.State value = this.decoration(decoration);
            decorations.put(decoration, value);
        }
        return decorations;
    }

    @Nullable
    public ClickEvent clickEvent();

    @Nullable
    public HoverEvent<?> hoverEvent();

    @Nullable
    public String insertion();
}

