/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.serializer.gson.legacyimpl;

import java.io.IOException;
import java.util.UUID;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.gson.LegacyHoverEventSerializer;
import net.kyori.adventure.util.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class NBTLegacyHoverEventSerializerImpl
implements LegacyHoverEventSerializer {
    static final NBTLegacyHoverEventSerializerImpl INSTANCE = new NBTLegacyHoverEventSerializerImpl();
    private static final TagStringIO SNBT_IO = TagStringIO.get();
    private static final Codec<CompoundBinaryTag, String, IOException, IOException> SNBT_CODEC = Codec.codec(SNBT_IO::asCompound, SNBT_IO::asString);
    static final String ITEM_TYPE = "id";
    static final String ITEM_COUNT = "Count";
    static final String ITEM_TAG = "tag";
    static final String ENTITY_NAME = "name";
    static final String ENTITY_TYPE = "type";
    static final String ENTITY_ID = "id";

    private NBTLegacyHoverEventSerializerImpl() {
    }

    @Override
    public @NotNull HoverEvent.ShowItem deserializeShowItem(@NotNull Component input) throws IOException {
        NBTLegacyHoverEventSerializerImpl.assertTextComponent(input);
        CompoundBinaryTag contents = SNBT_CODEC.decode(((TextComponent)input).content());
        CompoundBinaryTag tag = contents.getCompound(ITEM_TAG);
        return HoverEvent.ShowItem.of(Key.key(contents.getString("id")), (int)contents.getByte(ITEM_COUNT, (byte)1), tag == CompoundBinaryTag.empty() ? null : BinaryTagHolder.encode(tag, SNBT_CODEC));
    }

    @Override
    public @NotNull HoverEvent.ShowEntity deserializeShowEntity(@NotNull Component input, Codec.Decoder<Component, String, ? extends RuntimeException> componentCodec) throws IOException {
        NBTLegacyHoverEventSerializerImpl.assertTextComponent(input);
        CompoundBinaryTag contents = SNBT_CODEC.decode(((TextComponent)input).content());
        return HoverEvent.ShowEntity.of(Key.key(contents.getString(ENTITY_TYPE)), UUID.fromString(contents.getString("id")), componentCodec.decode(contents.getString(ENTITY_NAME)));
    }

    private static void assertTextComponent(Component component) {
        if (!(component instanceof TextComponent) || !component.children().isEmpty()) {
            throw new IllegalArgumentException("Legacy events must be single Component instances");
        }
    }

    @Override
    @NotNull
    public Component serializeShowItem(@NotNull HoverEvent.ShowItem input) throws IOException {
        CompoundBinaryTag.Builder builder = (CompoundBinaryTag.Builder)((CompoundBinaryTag.Builder)CompoundBinaryTag.builder().putString("id", input.item().asString())).putByte(ITEM_COUNT, (byte)input.count());
        @Nullable BinaryTagHolder nbt = input.nbt();
        if (nbt != null) {
            builder.put(ITEM_TAG, nbt.get(SNBT_CODEC));
        }
        return Component.text(SNBT_CODEC.encode(builder.build()));
    }

    @Override
    @NotNull
    public Component serializeShowEntity(@NotNull HoverEvent.ShowEntity input, Codec.Encoder<Component, String, ? extends RuntimeException> componentCodec) throws IOException {
        CompoundBinaryTag.Builder builder = (CompoundBinaryTag.Builder)((CompoundBinaryTag.Builder)CompoundBinaryTag.builder().putString("id", input.id().toString())).putString(ENTITY_TYPE, input.type().asString());
        @Nullable Component name = input.name();
        if (name != null) {
            builder.putString(ENTITY_NAME, componentCodec.encode(name));
        }
        return Component.text(SNBT_CODEC.encode(builder.build()));
    }
}

