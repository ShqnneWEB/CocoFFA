/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.serializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentDecoder;
import net.kyori.adventure.text.serializer.ComponentEncoder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ComponentSerializer<I extends Component, O extends Component, R>
extends ComponentEncoder<I, R>,
ComponentDecoder<R, O> {
    @Override
    @NotNull
    public O deserialize(@NotNull R var1);

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    @Contract(value="!null -> !null; null -> null", pure=true)
    @Nullable
    default public O deseializeOrNull(@Nullable R input) {
        return ComponentDecoder.super.deserializeOrNull(input);
    }

    @Override
    @Contract(value="!null -> !null; null -> null", pure=true)
    @Nullable
    default public O deserializeOrNull(@Nullable R input) {
        return ComponentDecoder.super.deserializeOr(input, null);
    }

    @Override
    @Contract(value="!null, _ -> !null; null, _ -> param2", pure=true)
    @Nullable
    default public O deserializeOr(@Nullable R input, @Nullable O fallback) {
        return ComponentDecoder.super.deserializeOr(input, fallback);
    }

    @Override
    @NotNull
    public R serialize(@NotNull I var1);

    @Override
    @Contract(value="!null -> !null; null -> null", pure=true)
    @Nullable
    default public R serializeOrNull(@Nullable I component) {
        return this.serializeOr(component, null);
    }

    @Override
    @Contract(value="!null, _ -> !null; null, _ -> param2", pure=true)
    @Nullable
    default public R serializeOr(@Nullable I component, @Nullable R fallback) {
        if (component == null) {
            return fallback;
        }
        return this.serialize(component);
    }
}

