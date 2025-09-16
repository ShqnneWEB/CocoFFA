/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text.serializer.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import org.jetbrains.annotations.Nullable;

final class GsonHacks {
    private GsonHacks() {
    }

    static boolean isNullOrEmpty(@Nullable JsonElement element) {
        return element == null || element.isJsonNull() || element.isJsonArray() && element.getAsJsonArray().size() == 0 || element.isJsonObject() && element.getAsJsonObject().entrySet().isEmpty();
    }

    static boolean readBoolean(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        if (peek == JsonToken.BOOLEAN) {
            return in.nextBoolean();
        }
        if (peek == JsonToken.STRING) {
            return Boolean.parseBoolean(in.nextString());
        }
        if (peek == JsonToken.NUMBER) {
            return in.nextString().equals("1");
        }
        throw new JsonParseException("Token of type " + (Object)((Object)peek) + " cannot be interpreted as a boolean");
    }

    static String readString(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        if (peek == JsonToken.STRING || peek == JsonToken.NUMBER) {
            return in.nextString();
        }
        if (peek == JsonToken.BOOLEAN) {
            return String.valueOf(in.nextBoolean());
        }
        throw new JsonParseException("Token of type " + (Object)((Object)peek) + " cannot be interpreted as a string");
    }
}

