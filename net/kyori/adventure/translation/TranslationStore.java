/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.translation;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.ComponentTranslationStore;
import net.kyori.adventure.translation.MessageFormatTranslationStore;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;

public interface TranslationStore<T>
extends Translator {
    @NotNull
    public static TranslationStore<Component> component(@NotNull Key name) {
        return new ComponentTranslationStore(Objects.requireNonNull(name, "name"));
    }

    public static @NotNull StringBased<MessageFormat> messageFormat(@NotNull Key name) {
        return new MessageFormatTranslationStore(Objects.requireNonNull(name, "name"));
    }

    public boolean contains(@NotNull String var1);

    public boolean contains(@NotNull String var1, @NotNull Locale var2);

    @Override
    default public boolean canTranslate(@NotNull String key, @NotNull Locale locale) {
        return Translator.super.canTranslate(key, locale);
    }

    public void defaultLocale(@NotNull Locale var1);

    public void register(@NotNull String var1, @NotNull Locale var2, T var3);

    public void registerAll(@NotNull Locale var1, @NotNull Map<String, T> var2);

    public void registerAll(@NotNull Locale var1, @NotNull Set<String> var2, Function<String, T> var3);

    public void unregister(@NotNull String var1);

    public static interface StringBased<T>
    extends TranslationStore<T> {
        public void registerAll(@NotNull Locale var1, @NotNull Path var2, boolean var3);

        public void registerAll(@NotNull Locale var1, @NotNull ResourceBundle var2, boolean var3);
    }
}

