/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslationArgument;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface TranslationArgumentLike
extends ComponentLike {
    @NotNull
    public TranslationArgument asTranslationArgument();

    @Override
    @NotNull
    default public Component asComponent() {
        return this.asTranslationArgument().asComponent();
    }
}

