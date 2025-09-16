/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.serializer.json;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializerAccessor;
import net.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
import net.kyori.adventure.util.PlatformAPI;
import net.kyori.option.OptionState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface JSONComponentSerializer
extends ComponentSerializer<Component, Component, String> {
    @NotNull
    public static JSONComponentSerializer json() {
        return JSONComponentSerializerAccessor.Instances.INSTANCE;
    }

    public static @NotNull Builder builder() {
        return JSONComponentSerializerAccessor.Instances.BUILDER_SUPPLIER.get();
    }

    public static interface Builder {
        @NotNull
        public Builder options(@NotNull OptionState var1);

        @NotNull
        public Builder editOptions(@NotNull Consumer<OptionState.Builder> var1);

        @Deprecated
        @NotNull
        public Builder downsampleColors();

        @NotNull
        public Builder legacyHoverEventSerializer(@Nullable LegacyHoverEventSerializer var1);

        @Deprecated
        @NotNull
        public Builder emitLegacyHoverEvent();

        @NotNull
        public JSONComponentSerializer build();
    }

    @PlatformAPI
    @ApiStatus.Internal
    public static interface Provider {
        @PlatformAPI
        @ApiStatus.Internal
        @NotNull
        public JSONComponentSerializer instance();

        @PlatformAPI
        @ApiStatus.Internal
        @NotNull
        public @NotNull Supplier<@NotNull Builder> builder();
    }
}

