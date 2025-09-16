/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.serializer.gson.impl;

import com.google.auto.service.AutoService;
import java.util.function.Supplier;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.util.Services;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
@AutoService(value={JSONComponentSerializer.Provider.class})
public final class JSONComponentSerializerProviderImpl
implements JSONComponentSerializer.Provider,
Services.Fallback {
    @Override
    @NotNull
    public JSONComponentSerializer instance() {
        return GsonComponentSerializer.gson();
    }

    @Override
    @NotNull
    public @NotNull Supplier<@NotNull JSONComponentSerializer.Builder> builder() {
        return GsonComponentSerializer::builder;
    }

    public String toString() {
        return "JSONComponentSerializerProviderImpl[GsonComponentSerializer]";
    }
}

