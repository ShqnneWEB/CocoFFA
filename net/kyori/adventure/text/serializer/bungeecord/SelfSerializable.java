/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.serializer.bungeecord;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

interface SelfSerializable {
    public void write(JsonWriter var1) throws IOException;

    public static class AdapterFactory
    implements TypeAdapterFactory {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (!SelfSerializable.class.isAssignableFrom(type.getRawType())) {
                return null;
            }
            return new SelfSerializableTypeAdapter<T>(type);
        }

        static {
            SelfSerializableTypeAdapter.class.getName();
        }

        static class SelfSerializableTypeAdapter<T>
        extends TypeAdapter<T> {
            private final TypeToken<T> type;

            SelfSerializableTypeAdapter(TypeToken<T> type) {
                this.type = type;
            }

            @Override
            public void write(JsonWriter out, T value) throws IOException {
                ((SelfSerializable)value).write(out);
            }

            @Override
            public T read(JsonReader in) {
                throw new UnsupportedOperationException("Cannot load values of type " + this.type.getType().getTypeName());
            }
        }
    }
}

