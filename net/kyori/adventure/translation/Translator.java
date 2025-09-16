/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.translation;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Translator {
    @Nullable
    public static Locale parseLocale(@NotNull String string) {
        String[] segments = string.split("_", 3);
        int length = segments.length;
        if (length == 1) {
            return new Locale(string);
        }
        if (length == 2) {
            return new Locale(segments[0], segments[1]);
        }
        if (length == 3) {
            return new Locale(segments[0], segments[1], segments[2]);
        }
        return null;
    }

    @NotNull
    public Key name();

    @NotNull
    default public TriState hasAnyTranslations() {
        return TriState.NOT_SET;
    }

    default public boolean canTranslate(@NotNull String key, @NotNull Locale locale) {
        Component translatedValue = this.translate(Component.translatable(Objects.requireNonNull(key, "key")), Objects.requireNonNull(locale, "locale"));
        if (translatedValue != null) {
            return true;
        }
        return this.translate(key, locale) != null;
    }

    @Nullable
    public MessageFormat translate(@NotNull String var1, @NotNull Locale var2);

    @Nullable
    default public Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
        return null;
    }
}

