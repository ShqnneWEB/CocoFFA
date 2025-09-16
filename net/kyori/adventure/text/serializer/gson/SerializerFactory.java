/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import java.util.UUID;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.BlockNBTComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.BlockNBTComponentPosSerializer;
import net.kyori.adventure.text.serializer.gson.ClickEventActionSerializer;
import net.kyori.adventure.text.serializer.gson.ComponentSerializerImpl;
import net.kyori.adventure.text.serializer.gson.HoverEventActionSerializer;
import net.kyori.adventure.text.serializer.gson.KeySerializer;
import net.kyori.adventure.text.serializer.gson.ShadowColorSerializer;
import net.kyori.adventure.text.serializer.gson.ShowEntitySerializer;
import net.kyori.adventure.text.serializer.gson.ShowItemSerializer;
import net.kyori.adventure.text.serializer.gson.StyleSerializer;
import net.kyori.adventure.text.serializer.gson.TextColorSerializer;
import net.kyori.adventure.text.serializer.gson.TextColorWrapper;
import net.kyori.adventure.text.serializer.gson.TextDecorationSerializer;
import net.kyori.adventure.text.serializer.gson.TranslationArgumentSerializer;
import net.kyori.adventure.text.serializer.gson.UUIDSerializer;
import net.kyori.adventure.text.serializer.json.JSONOptions;
import net.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
import net.kyori.option.OptionState;
import org.jetbrains.annotations.Nullable;

final class SerializerFactory
implements TypeAdapterFactory {
    static final Class<Key> KEY_TYPE = Key.class;
    static final Class<Component> COMPONENT_TYPE = Component.class;
    static final Class<Style> STYLE_TYPE = Style.class;
    static final Class<ClickEvent.Action> CLICK_ACTION_TYPE = ClickEvent.Action.class;
    static final Class<HoverEvent.Action> HOVER_ACTION_TYPE = HoverEvent.Action.class;
    static final Class<HoverEvent.ShowItem> SHOW_ITEM_TYPE = HoverEvent.ShowItem.class;
    static final Class<HoverEvent.ShowEntity> SHOW_ENTITY_TYPE = HoverEvent.ShowEntity.class;
    static final Class<String> STRING_TYPE = String.class;
    static final Class<TextColorWrapper> COLOR_WRAPPER_TYPE = TextColorWrapper.class;
    static final Class<TextColor> COLOR_TYPE = TextColor.class;
    static final Class<ShadowColor> SHADOW_COLOR_TYPE = ShadowColor.class;
    static final Class<TextDecoration> TEXT_DECORATION_TYPE = TextDecoration.class;
    static final Class<BlockNBTComponent.Pos> BLOCK_NBT_POS_TYPE = BlockNBTComponent.Pos.class;
    static final Class<UUID> UUID_TYPE = UUID.class;
    static final Class<TranslationArgument> TRANSLATION_ARGUMENT_TYPE = TranslationArgument.class;
    private final OptionState features;
    private final LegacyHoverEventSerializer legacyHoverSerializer;

    SerializerFactory(OptionState features, @Nullable LegacyHoverEventSerializer legacyHoverSerializer) {
        this.features = features;
        this.legacyHoverSerializer = legacyHoverSerializer;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> rawType = type.getRawType();
        if (COMPONENT_TYPE.isAssignableFrom(rawType)) {
            return ComponentSerializerImpl.create(this.features, gson);
        }
        if (KEY_TYPE.isAssignableFrom(rawType)) {
            return KeySerializer.INSTANCE;
        }
        if (STYLE_TYPE.isAssignableFrom(rawType)) {
            return StyleSerializer.create(this.legacyHoverSerializer, this.features, gson);
        }
        if (CLICK_ACTION_TYPE.isAssignableFrom(rawType)) {
            return ClickEventActionSerializer.INSTANCE;
        }
        if (HOVER_ACTION_TYPE.isAssignableFrom(rawType)) {
            return HoverEventActionSerializer.INSTANCE;
        }
        if (SHOW_ITEM_TYPE.isAssignableFrom(rawType)) {
            return ShowItemSerializer.create(gson, this.features);
        }
        if (SHOW_ENTITY_TYPE.isAssignableFrom(rawType)) {
            return ShowEntitySerializer.create(gson, this.features);
        }
        if (COLOR_WRAPPER_TYPE.isAssignableFrom(rawType)) {
            return TextColorWrapper.Serializer.INSTANCE;
        }
        if (COLOR_TYPE.isAssignableFrom(rawType)) {
            return this.features.value(JSONOptions.EMIT_RGB) != false ? TextColorSerializer.INSTANCE : TextColorSerializer.DOWNSAMPLE_COLOR;
        }
        if (SHADOW_COLOR_TYPE.isAssignableFrom(rawType)) {
            return ShadowColorSerializer.create(this.features);
        }
        if (TEXT_DECORATION_TYPE.isAssignableFrom(rawType)) {
            return TextDecorationSerializer.INSTANCE;
        }
        if (BLOCK_NBT_POS_TYPE.isAssignableFrom(rawType)) {
            return BlockNBTComponentPosSerializer.INSTANCE;
        }
        if (UUID_TYPE.isAssignableFrom(rawType)) {
            return UUIDSerializer.uuidSerializer(this.features);
        }
        if (TRANSLATION_ARGUMENT_TYPE.isAssignableFrom(rawType)) {
            return TranslationArgumentSerializer.create(gson);
        }
        return null;
    }
}

