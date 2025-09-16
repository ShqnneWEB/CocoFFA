/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.resource;

import java.util.List;
import java.util.Objects;
import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.resource.ResourcePackCallback;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackInfoLike;
import net.kyori.adventure.resource.ResourcePackRequestImpl;
import net.kyori.adventure.resource.ResourcePackRequestLike;
import net.kyori.adventure.text.Component;
import net.kyori.examination.Examinable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ResourcePackRequest
extends Examinable,
ResourcePackRequestLike {
    @NotNull
    public static ResourcePackRequest addingRequest(@NotNull ResourcePackInfoLike first, @NotNull @NotNull ResourcePackInfoLike @NotNull ... others) {
        return (ResourcePackRequest)ResourcePackRequest.resourcePackRequest().packs(first, others).replace(false).build();
    }

    @NotNull
    public static Builder resourcePackRequest() {
        return new ResourcePackRequestImpl.BuilderImpl();
    }

    @NotNull
    public static Builder resourcePackRequest(@NotNull ResourcePackRequest existing) {
        return new ResourcePackRequestImpl.BuilderImpl(Objects.requireNonNull(existing, "existing"));
    }

    @NotNull
    public List<ResourcePackInfo> packs();

    @NotNull
    public ResourcePackRequest packs(@NotNull Iterable<? extends ResourcePackInfoLike> var1);

    @NotNull
    public ResourcePackCallback callback();

    @NotNull
    public ResourcePackRequest callback(@NotNull ResourcePackCallback var1);

    public boolean replace();

    @NotNull
    public ResourcePackRequest replace(boolean var1);

    public boolean required();

    @Nullable
    public Component prompt();

    @Override
    @NotNull
    default public ResourcePackRequest asResourcePackRequest() {
        return this;
    }

    public static interface Builder
    extends AbstractBuilder<ResourcePackRequest>,
    ResourcePackRequestLike {
        @Contract(value="_, _ -> this")
        @NotNull
        public Builder packs(@NotNull ResourcePackInfoLike var1, @NotNull @NotNull ResourcePackInfoLike @NotNull ... var2);

        @Contract(value="_ -> this")
        @NotNull
        public Builder packs(@NotNull Iterable<? extends ResourcePackInfoLike> var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder callback(@NotNull ResourcePackCallback var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder replace(boolean var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder required(boolean var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder prompt(@Nullable Component var1);

        @Override
        @NotNull
        default public ResourcePackRequest asResourcePackRequest() {
            return (ResourcePackRequest)this.build();
        }
    }
}

