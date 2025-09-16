/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.serializer.gson.impl;

import com.google.auto.service.AutoService;
import com.google.gson.JsonNull;
import java.util.Collections;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.event.DataComponentValueConverterRegistry;
import net.kyori.adventure.text.serializer.gson.GsonDataComponentValue;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@AutoService(value={DataComponentValueConverterRegistry.Provider.class})
@ApiStatus.Internal
public final class GsonDataComponentValueConverterProvider
implements DataComponentValueConverterRegistry.Provider {
    private static final Key ID = Key.key("adventure", "serializer/gson");

    @Override
    @NotNull
    public Key id() {
        return ID;
    }

    @Override
    @NotNull
    public Iterable<DataComponentValueConverterRegistry.Conversion<?, ?>> conversions() {
        return Collections.singletonList(DataComponentValueConverterRegistry.Conversion.convert(DataComponentValue.Removed.class, GsonDataComponentValue.class, (k, removed) -> GsonDataComponentValue.gsonDataComponentValue(JsonNull.INSTANCE)));
    }
}

