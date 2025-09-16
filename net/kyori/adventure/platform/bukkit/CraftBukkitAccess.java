/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.platform.bukkit;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import net.kyori.adventure.platform.bukkit.CraftBukkitFacet;
import net.kyori.adventure.platform.bukkit.MinecraftReflection;
import net.kyori.adventure.platform.facet.Knob;
import org.jetbrains.annotations.Nullable;

final class CraftBukkitAccess {
    @Nullable
    static final Class<?> CLASS_CHAT_COMPONENT = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("IChatBaseComponent"), MinecraftReflection.findMcClassName("network.chat.IChatBaseComponent"), MinecraftReflection.findMcClassName("network.chat.Component"));
    @Nullable
    static final Class<?> CLASS_REGISTRY = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("IRegistry"), MinecraftReflection.findMcClassName("core.IRegistry"), MinecraftReflection.findMcClassName("core.Registry"));
    @Nullable
    static final Class<?> CLASS_SERVER_LEVEL = MinecraftReflection.findClass(MinecraftReflection.findMcClassName("server.level.WorldServer"), MinecraftReflection.findMcClassName("server.level.ServerLevel"));
    @Nullable
    static final Class<?> CLASS_LEVEL = MinecraftReflection.findClass(MinecraftReflection.findMcClassName("world.level.World"), MinecraftReflection.findMcClassName("world.level.Level"));
    @Nullable
    static final Class<?> CLASS_REGISTRY_ACCESS = MinecraftReflection.findClass(MinecraftReflection.findMcClassName("core.IRegistryCustom"), MinecraftReflection.findMcClassName("core.RegistryAccess"));
    @Nullable
    static final Class<?> CLASS_RESOURCE_KEY = MinecraftReflection.findClass(MinecraftReflection.findMcClassName("resources.ResourceKey"));
    @Nullable
    static final Class<?> CLASS_RESOURCE_LOCATION = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("MinecraftKey"), MinecraftReflection.findMcClassName("resources.MinecraftKey"), MinecraftReflection.findMcClassName("resources.ResourceLocation"));
    @Nullable
    static final Class<?> CLASS_NMS_ENTITY = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("Entity"), MinecraftReflection.findMcClassName("world.entity.Entity"));
    @Nullable
    static final Class<?> CLASS_BUILT_IN_REGISTRIES = MinecraftReflection.findClass(MinecraftReflection.findMcClassName("core.registries.BuiltInRegistries"));
    @Nullable
    static final Class<?> CLASS_HOLDER = MinecraftReflection.findClass(MinecraftReflection.findMcClassName("core.Holder"));
    @Nullable
    static final Class<?> CLASS_WRITABLE_REGISTRY = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("IRegistryWritable"), MinecraftReflection.findMcClassName("core.IRegistryWritable"), MinecraftReflection.findMcClassName("core.WritableRegistry"));

    private CraftBukkitAccess() {
    }

    static final class EntitySound_1_19_3 {
        @Nullable
        static final MethodHandle NEW_RESOURCE_LOCATION = MinecraftReflection.findConstructor(CLASS_RESOURCE_LOCATION, String.class, String.class);
        @Nullable
        static final MethodHandle REGISTRY_GET_OPTIONAL = MinecraftReflection.searchMethod(CLASS_REGISTRY, (Integer)1, "getOptional", Optional.class, CLASS_RESOURCE_LOCATION);
        @Nullable
        static final MethodHandle REGISTRY_WRAP_AS_HOLDER = MinecraftReflection.searchMethod(CLASS_REGISTRY, (Integer)1, "wrapAsHolder", CLASS_HOLDER, Object.class);
        @Nullable
        static final MethodHandle SOUND_EVENT_CREATE_VARIABLE_RANGE = MinecraftReflection.searchMethod(EntitySound.CLASS_SOUND_EVENT, (Integer)9, "createVariableRangeEvent", EntitySound.CLASS_SOUND_EVENT, CLASS_RESOURCE_LOCATION);
        @Nullable
        static final MethodHandle NEW_CLIENTBOUND_ENTITY_SOUND = MinecraftReflection.findConstructor(EntitySound.CLASS_CLIENTBOUND_ENTITY_SOUND, CLASS_HOLDER, EntitySound.CLASS_SOUND_SOURCE, CLASS_NMS_ENTITY, Float.TYPE, Float.TYPE, Long.TYPE);
        @Nullable
        static final Object SOUND_EVENT_REGISTRY;

        private EntitySound_1_19_3() {
        }

        static boolean isSupported() {
            return NEW_CLIENTBOUND_ENTITY_SOUND != null && SOUND_EVENT_REGISTRY != null && NEW_RESOURCE_LOCATION != null && REGISTRY_GET_OPTIONAL != null && REGISTRY_WRAP_AS_HOLDER != null && SOUND_EVENT_CREATE_VARIABLE_RANGE != null;
        }

        static {
            Object soundEventRegistry = null;
            try {
                Field soundEventRegistryField = MinecraftReflection.findField(CLASS_BUILT_IN_REGISTRIES, CLASS_REGISTRY, "SOUND_EVENT");
                if (soundEventRegistryField != null) {
                    soundEventRegistry = soundEventRegistryField.get(null);
                } else if (CLASS_BUILT_IN_REGISTRIES != null && REGISTRY_GET_OPTIONAL != null && NEW_RESOURCE_LOCATION != null) {
                    Object rootRegistry = null;
                    for (Field field : CLASS_BUILT_IN_REGISTRIES.getDeclaredFields()) {
                        int mask = 26;
                        if ((field.getModifiers() & 0x1A) != 26 || !field.getType().equals(CLASS_WRITABLE_REGISTRY)) continue;
                        field.setAccessible(true);
                        rootRegistry = field.get(null);
                        break;
                    }
                    if (rootRegistry != null) {
                        soundEventRegistry = REGISTRY_GET_OPTIONAL.invoke(rootRegistry, NEW_RESOURCE_LOCATION.invoke("minecraft", "sound_event")).orElse(null);
                    }
                }
            } catch (Throwable error) {
                Knob.logError(error, "Failed to initialize EntitySound_1_19_3 CraftBukkit facet", new Object[0]);
            }
            SOUND_EVENT_REGISTRY = soundEventRegistry;
        }
    }

    static final class EntitySound {
        @Nullable
        static final Class<?> CLASS_CLIENTBOUND_ENTITY_SOUND = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("PacketPlayOutEntitySound"), MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutEntitySound"), MinecraftReflection.findMcClassName("network.protocol.game.ClientboundSoundEntityPacket"));
        @Nullable
        static final Class<?> CLASS_SOUND_SOURCE = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("SoundCategory"), MinecraftReflection.findMcClassName("sounds.SoundCategory"), MinecraftReflection.findMcClassName("sounds.SoundSource"));
        @Nullable
        static final Class<?> CLASS_SOUND_EVENT = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("SoundEffect"), MinecraftReflection.findMcClassName("sounds.SoundEffect"), MinecraftReflection.findMcClassName("sounds.SoundEvent"));
        @Nullable
        static final MethodHandle SOUND_SOURCE_GET_NAME;

        private EntitySound() {
        }

        static boolean isSupported() {
            return SOUND_SOURCE_GET_NAME != null;
        }

        static {
            MethodHandle soundSourceGetName = null;
            if (CLASS_SOUND_SOURCE != null) {
                for (Method method : CLASS_SOUND_SOURCE.getDeclaredMethods()) {
                    if (!method.getReturnType().equals(String.class) || method.getParameterCount() != 0 || "name".equals(method.getName()) || !Modifier.isPublic(method.getModifiers())) continue;
                    try {
                        soundSourceGetName = MinecraftReflection.lookup().unreflect(method);
                    } catch (IllegalAccessException illegalAccessException) {}
                    break;
                }
            }
            SOUND_SOURCE_GET_NAME = soundSourceGetName;
        }
    }

    static final class Chat1_19_3 {
        @Nullable
        static final MethodHandle NEW_RESOURCE_LOCATION = MinecraftReflection.findConstructor(CLASS_RESOURCE_LOCATION, String.class, String.class);
        @Nullable
        static final MethodHandle RESOURCE_KEY_CREATE = MinecraftReflection.searchMethod(CLASS_RESOURCE_KEY, (Integer)9, "create", CLASS_RESOURCE_KEY, CLASS_RESOURCE_KEY, CLASS_RESOURCE_LOCATION);
        @Nullable
        static final MethodHandle SERVER_PLAYER_GET_LEVEL = MinecraftReflection.searchMethod(CraftBukkitFacet.CRAFT_PLAYER_GET_HANDLE.type().returnType(), (Integer)1, "getLevel", CLASS_SERVER_LEVEL, new Class[0]);
        @Nullable
        static final MethodHandle SERVER_LEVEL_GET_REGISTRY_ACCESS = MinecraftReflection.searchMethod(CLASS_SERVER_LEVEL, (Integer)1, "registryAccess", CLASS_REGISTRY_ACCESS, new Class[0]);
        @Nullable
        static final MethodHandle LEVEL_GET_REGISTRY_ACCESS = MinecraftReflection.searchMethod(CLASS_LEVEL, (Integer)1, "registryAccess", CLASS_REGISTRY_ACCESS, new Class[0]);
        @Nullable
        static final MethodHandle ACTUAL_GET_REGISTRY_ACCESS = SERVER_LEVEL_GET_REGISTRY_ACCESS == null ? LEVEL_GET_REGISTRY_ACCESS : SERVER_LEVEL_GET_REGISTRY_ACCESS;
        @Nullable
        static final MethodHandle REGISTRY_ACCESS_GET_REGISTRY_OPTIONAL = MinecraftReflection.searchMethod(CLASS_REGISTRY_ACCESS, (Integer)1, "registry", Optional.class, CLASS_RESOURCE_KEY);
        @Nullable
        static final MethodHandle REGISTRY_GET_OPTIONAL = MinecraftReflection.searchMethod(CLASS_REGISTRY, (Integer)1, "getOptional", Optional.class, CLASS_RESOURCE_LOCATION);
        @Nullable
        static final MethodHandle REGISTRY_GET_ID = MinecraftReflection.searchMethod(CLASS_REGISTRY, (Integer)1, "getId", Integer.TYPE, Object.class);
        @Nullable
        static final MethodHandle DISGUISED_CHAT_PACKET_CONSTRUCTOR;
        @Nullable
        static final MethodHandle CHAT_TYPE_BOUND_NETWORK_CONSTRUCTOR;
        static final Object CHAT_TYPE_RESOURCE_KEY;

        private Chat1_19_3() {
        }

        static boolean isSupported() {
            return ACTUAL_GET_REGISTRY_ACCESS != null && REGISTRY_ACCESS_GET_REGISTRY_OPTIONAL != null && REGISTRY_GET_OPTIONAL != null && CHAT_TYPE_BOUND_NETWORK_CONSTRUCTOR != null && DISGUISED_CHAT_PACKET_CONSTRUCTOR != null && CHAT_TYPE_RESOURCE_KEY != null;
        }

        static {
            MethodHandle boundNetworkConstructor = null;
            MethodHandle disguisedChatPacketConstructor = null;
            Object chatTypeResourceKey = null;
            try {
                MethodHandle createRegistryKey;
                Class<?> disguisedChatPacketClass;
                Class<?> parentClass;
                Class<?> classChatTypeBoundNetwork = MinecraftReflection.findClass(MinecraftReflection.findMcClassName("network.chat.ChatType$BoundNetwork"));
                if (classChatTypeBoundNetwork == null && (parentClass = MinecraftReflection.findClass(MinecraftReflection.findMcClassName("network.chat.ChatMessageType"))) != null) {
                    for (Class<?> childClass : parentClass.getClasses()) {
                        boundNetworkConstructor = MinecraftReflection.findConstructor(childClass, Integer.TYPE, CLASS_CHAT_COMPONENT, CLASS_CHAT_COMPONENT);
                        if (boundNetworkConstructor == null) continue;
                        classChatTypeBoundNetwork = childClass;
                        break;
                    }
                }
                if ((disguisedChatPacketClass = MinecraftReflection.findClass(MinecraftReflection.findMcClassName("network.protocol.game.ClientboundDisguisedChatPacket"))) != null && classChatTypeBoundNetwork != null) {
                    disguisedChatPacketConstructor = MinecraftReflection.findConstructor(disguisedChatPacketClass, CLASS_CHAT_COMPONENT, classChatTypeBoundNetwork);
                }
                if (NEW_RESOURCE_LOCATION != null && RESOURCE_KEY_CREATE != null && (createRegistryKey = MinecraftReflection.searchMethod(CLASS_RESOURCE_KEY, (Integer)9, "createRegistryKey", CLASS_RESOURCE_KEY, CLASS_RESOURCE_LOCATION)) != null) {
                    chatTypeResourceKey = createRegistryKey.invoke(NEW_RESOURCE_LOCATION.invoke("minecraft", "chat_type"));
                }
            } catch (Throwable error) {
                Knob.logError(error, "Failed to initialize 1.19.3 chat support", new Object[0]);
            }
            DISGUISED_CHAT_PACKET_CONSTRUCTOR = disguisedChatPacketConstructor;
            CHAT_TYPE_BOUND_NETWORK_CONSTRUCTOR = boundNetworkConstructor;
            CHAT_TYPE_RESOURCE_KEY = chatTypeResourceKey;
        }
    }
}

