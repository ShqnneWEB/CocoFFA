/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.serializer.gson;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import net.kyori.adventure.util.Index;

final class IndexedSerializer<E>
extends TypeAdapter<E> {
    private final String name;
    private final Index<String, E> map;
    private final boolean throwOnUnknownKey;

    public static <E> TypeAdapter<E> strict(String name, Index<String, E> map) {
        return new IndexedSerializer<E>(name, map, true).nullSafe();
    }

    public static <E> TypeAdapter<E> lenient(String name, Index<String, E> map) {
        return new IndexedSerializer<E>(name, map, false).nullSafe();
    }

    private IndexedSerializer(String name, Index<String, E> map, boolean throwOnUnknownKey) {
        this.name = name;
        this.map = map;
        this.throwOnUnknownKey = throwOnUnknownKey;
    }

    @Override
    public void write(JsonWriter out, E value) throws IOException {
        out.value(this.map.key(value));
    }

    @Override
    public E read(JsonReader in) throws IOException {
        String string = in.nextString();
        E value = this.map.value(string);
        if (value != null) {
            return value;
        }
        if (this.throwOnUnknownKey) {
            throw new JsonParseException("invalid " + this.name + ":  " + string);
        }
        return null;
    }
}

