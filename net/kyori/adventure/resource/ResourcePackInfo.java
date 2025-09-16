/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.resource;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.resource.ResourcePackInfoImpl;
import net.kyori.adventure.resource.ResourcePackInfoLike;
import net.kyori.examination.Examinable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface ResourcePackInfo
extends Examinable,
ResourcePackInfoLike {
    @NotNull
    public static ResourcePackInfo resourcePackInfo(@NotNull UUID id, @NotNull URI uri, @NotNull String hash) {
        return new ResourcePackInfoImpl(id, uri, hash);
    }

    @NotNull
    public static Builder resourcePackInfo() {
        return new ResourcePackInfoImpl.BuilderImpl();
    }

    @NotNull
    public UUID id();

    @NotNull
    public URI uri();

    @NotNull
    public String hash();

    @Override
    @NotNull
    default public ResourcePackInfo asResourcePackInfo() {
        return this;
    }

    public static interface Builder
    extends AbstractBuilder<ResourcePackInfo>,
    ResourcePackInfoLike {
        @Contract(value="_ -> this")
        @NotNull
        public Builder id(@NotNull UUID var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder uri(@NotNull URI var1);

        @Contract(value="_ -> this")
        @NotNull
        public Builder hash(@NotNull String var1);

        @Override
        @NotNull
        public ResourcePackInfo build();

        @NotNull
        default public CompletableFuture<ResourcePackInfo> computeHashAndBuild() {
            return this.computeHashAndBuild(ForkJoinPool.commonPool());
        }

        @NotNull
        public CompletableFuture<ResourcePackInfo> computeHashAndBuild(@NotNull Executor var1);

        @Override
        @NotNull
        default public ResourcePackInfo asResourcePackInfo() {
            return this.build();
        }
    }
}

