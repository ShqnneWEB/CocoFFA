/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.serializer.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import java.util.Objects;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.serializer.gson.GsonDataComponentValueImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface GsonDataComponentValue
extends DataComponentValue {
    public static GsonDataComponentValue gsonDataComponentValue(@NotNull JsonElement data) {
        if (data instanceof JsonNull) {
            return GsonDataComponentValueImpl.RemovedGsonComponentValueImpl.INSTANCE;
        }
        return new GsonDataComponentValueImpl(Objects.requireNonNull(data, "data"));
    }

    @NotNull
    public JsonElement element();
}

