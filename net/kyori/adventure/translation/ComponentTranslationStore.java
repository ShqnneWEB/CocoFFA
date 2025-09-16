/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.translation;

import java.text.MessageFormat;
import java.util.Locale;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.AbstractTranslationStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ComponentTranslationStore
extends AbstractTranslationStore<Component> {
    ComponentTranslationStore(@NotNull Key name) {
        super(name);
    }

    @Override
    @Nullable
    public MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        return null;
    }

    @Override
    @Nullable
    public Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
        Component translatedComponent = (Component)this.translationValue(component.key(), locale);
        if (translatedComponent == null) {
            return null;
        }
        return translatedComponent.append(component.children());
    }
}

