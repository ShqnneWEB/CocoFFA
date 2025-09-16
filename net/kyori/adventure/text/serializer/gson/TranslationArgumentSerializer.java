/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.serializer.gson.SerializerFactory;

final class TranslationArgumentSerializer
extends TypeAdapter<TranslationArgument> {
    private final Gson gson;

    static TypeAdapter<TranslationArgument> create(Gson gson) {
        return new TranslationArgumentSerializer(gson).nullSafe();
    }

    private TranslationArgumentSerializer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, TranslationArgument value) throws IOException {
        Object raw = value.value();
        if (raw instanceof Boolean) {
            out.value((Boolean)raw);
        } else if (raw instanceof Number) {
            out.value((Number)raw);
        } else if (raw instanceof Component) {
            this.gson.toJson(raw, SerializerFactory.COMPONENT_TYPE, out);
        } else {
            throw new IllegalStateException("Unable to serialize translatable argument of type " + raw.getClass() + ": " + raw);
        }
    }

    @Override
    public TranslationArgument read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case BOOLEAN: {
                return TranslationArgument.bool(in.nextBoolean());
            }
            case NUMBER: {
                return TranslationArgument.numeric((Number)this.gson.fromJson(in, (Type)((Object)Number.class)));
            }
        }
        return TranslationArgument.component((ComponentLike)this.gson.fromJson(in, SerializerFactory.COMPONENT_TYPE));
    }
}

