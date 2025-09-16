/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.platform.bukkit;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.platform.bukkit.MinecraftReflection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
public final class MinecraftComponentSerializer
implements ComponentSerializer<Component, Component, Object> {
    private static final MinecraftComponentSerializer INSTANCE = new MinecraftComponentSerializer();
    @Nullable
    private static final Class<?> CLASS_JSON_DESERIALIZER = MinecraftReflection.findClass("com.goo".concat("gle.gson.JsonDeserializer"));
    @Nullable
    private static final Class<?> CLASS_CHAT_COMPONENT = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("IChatBaseComponent"), MinecraftReflection.findMcClassName("network.chat.IChatBaseComponent"), MinecraftReflection.findMcClassName("network.chat.Component"));
    private static final AtomicReference<RuntimeException> INITIALIZATION_ERROR = new AtomicReference<UnsupportedOperationException>(new UnsupportedOperationException());
    private static final Object MC_TEXT_GSON;
    private static final MethodHandle TEXT_SERIALIZER_DESERIALIZE;
    private static final MethodHandle TEXT_SERIALIZER_SERIALIZE;
    private static final boolean SUPPORTED;

    public static boolean isSupported() {
        return SUPPORTED;
    }

    @NotNull
    public static MinecraftComponentSerializer get() {
        return INSTANCE;
    }

    @Override
    @NotNull
    public Component deserialize(@NotNull Object input) {
        if (!SUPPORTED) {
            throw INITIALIZATION_ERROR.get();
        }
        try {
            if (MC_TEXT_GSON != null) {
                JsonElement element = ((Gson)MC_TEXT_GSON).toJsonTree(input);
                return BukkitComponentSerializer.gson().serializer().fromJson(element, Component.class);
            }
            return GsonComponentSerializer.gson().deserialize(TEXT_SERIALIZER_SERIALIZE.invoke(input));
        } catch (Throwable error) {
            throw new UnsupportedOperationException(error);
        }
    }

    @Override
    @NotNull
    public Object serialize(@NotNull Component component) {
        if (!SUPPORTED) {
            throw INITIALIZATION_ERROR.get();
        }
        if (MC_TEXT_GSON != null) {
            JsonElement json = BukkitComponentSerializer.gson().serializer().toJsonTree(component);
            try {
                return ((Gson)MC_TEXT_GSON).fromJson(json, CLASS_CHAT_COMPONENT);
            } catch (Throwable error) {
                throw new UnsupportedOperationException(error);
            }
        }
        try {
            return TEXT_SERIALIZER_DESERIALIZE.invoke((String)BukkitComponentSerializer.gson().serialize(component));
        } catch (Throwable error) {
            throw new UnsupportedOperationException(error);
        }
    }

    static {
        Object gson = null;
        MethodHandle textSerializerDeserialize = null;
        MethodHandle textSerializerSerialize = null;
        try {
            Class<?> chatSerializerClass;
            if (CLASS_CHAT_COMPONENT != null && (chatSerializerClass = Arrays.stream(CLASS_CHAT_COMPONENT.getClasses()).filter(c -> {
                if (CLASS_JSON_DESERIALIZER != null) {
                    return CLASS_JSON_DESERIALIZER.isAssignableFrom((Class<?>)c);
                }
                for (Class<?> itf : c.getInterfaces()) {
                    if (!itf.getSimpleName().equals("JsonDeserializer")) continue;
                    return true;
                }
                return false;
            }).findAny().orElse(MinecraftReflection.findNmsClass("ChatSerializer"))) != null) {
                Field gsonField = Arrays.stream(chatSerializerClass.getDeclaredFields()).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> m.getType().equals(Gson.class)).findFirst().orElse(null);
                if (gsonField != null) {
                    gsonField.setAccessible(true);
                    gson = gsonField.get(null);
                } else {
                    Method[] declaredMethods = chatSerializerClass.getDeclaredMethods();
                    Method deserialize = Arrays.stream(declaredMethods).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> CLASS_CHAT_COMPONENT.isAssignableFrom(m.getReturnType())).filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(String.class)).min(Comparator.comparing(Method::getName)).orElse(null);
                    Method serialize = Arrays.stream(declaredMethods).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> m.getReturnType().equals(String.class)).filter(m -> m.getParameterCount() == 1 && CLASS_CHAT_COMPONENT.isAssignableFrom(m.getParameterTypes()[0])).findFirst().orElse(null);
                    if (deserialize != null) {
                        textSerializerDeserialize = MinecraftReflection.lookup().unreflect(deserialize);
                    }
                    if (serialize != null) {
                        textSerializerSerialize = MinecraftReflection.lookup().unreflect(serialize);
                    }
                }
            }
        } catch (Throwable error) {
            INITIALIZATION_ERROR.set(new UnsupportedOperationException("Error occurred during initialization", error));
        }
        MC_TEXT_GSON = gson;
        TEXT_SERIALIZER_DESERIALIZE = textSerializerDeserialize;
        TEXT_SERIALIZER_SERIALIZE = textSerializerSerialize;
        SUPPORTED = MC_TEXT_GSON != null || TEXT_SERIALIZER_DESERIALIZE != null && TEXT_SERIALIZER_SERIALIZE != null;
    }
}

