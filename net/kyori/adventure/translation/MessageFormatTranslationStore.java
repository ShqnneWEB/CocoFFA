/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.translation;

import java.text.MessageFormat;
import java.util.Locale;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.AbstractTranslationStore;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class MessageFormatTranslationStore
extends AbstractTranslationStore.StringBased<MessageFormat>
implements TranslationRegistry {
    MessageFormatTranslationStore(Key name) {
        super(name);
    }

    @Override
    @NotNull
    protected MessageFormat parse(@NotNull String string, @NotNull Locale locale) {
        return new MessageFormat(string, locale);
    }

    @Override
    @Nullable
    public MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        return (MessageFormat)this.translationValue(key, locale);
    }
}

