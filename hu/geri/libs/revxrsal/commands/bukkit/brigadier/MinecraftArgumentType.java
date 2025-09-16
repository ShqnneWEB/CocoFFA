/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package hu.geri.libs.revxrsal.commands.bukkit.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import hu.geri.libs.revxrsal.commands.bukkit.util.BukkitVersion;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MinecraftArgumentType {
    ENTITY(new String[]{"ArgumentEntity", "EntityArgument"}, Boolean.TYPE, Boolean.TYPE),
    GAME_PROFILE("ArgumentProfile", "GameProfileArgument"),
    COLOR("ArgumentChatFormat", "ColorArgument"),
    COMPONENT("ArgumentChatComponent", "ComponentArgument"),
    MESSAGE("ArgumentChat", "MessageArgument"),
    NBT("ArgumentNBTTag", "CompoundTagArgument"),
    NBT_TAG("ArgumentNBTBase", "NbtTagArgument"),
    NBT_PATH("ArgumentNBTKey", "NbtPathArgument"),
    SCOREBOARD_OBJECTIVE("ArgumentScoreboardObjective", "ObjectiveArgument"),
    OBJECTIVE_CRITERIA("ArgumentScoreboardCriteria", "ObjectiveCriteriaArgument"),
    SCOREBOARD_SLOT("ArgumentScoreboardSlot", "SlotArgument"),
    SCORE_HOLDER("ArgumentScoreholder", "ScoreHolderArgument"),
    TEAM("ArgumentScoreboardTeam", "TeamArgument"),
    OPERATION("ArgumentMathOperation", "OperationArgument"),
    PARTICLE("ArgumentParticle", "ParticleArgument"),
    ANGLE("ArgumentAngle", "AngleArgument"),
    ITEM_SLOT("ArgumentInventorySlot", "SlotArgument"),
    RESOURCE_LOCATION("ArgumentMinecraftKeyRegistered", "ResourceLocationArgument"),
    POTION_EFFECT("ArgumentMobEffect"),
    ENCHANTMENT("ArgumentEnchantment"),
    ENTITY_SUMMON("ArgumentEntitySummon"),
    DIMENSION("ArgumentDimension", "DimensionArgument"),
    TIME("ArgumentTime", "TimeArgument"),
    UUID("ArgumentUUID", "UuidArgument"),
    BLOCK_POS("coordinates.ArgumentPosition", "coordinates.BlockPosArgument"),
    COLUMN_POS("coordinates.ArgumentVec2I", "coordinates.ColumnPosArgument"),
    VECTOR_3("coordinates.ArgumentVec3", "coordinates.Vec3Argument"),
    VECTOR_2("coordinates.ArgumentVec2", "coordinates.Vec2Argument"),
    ROTATION("coordinates.ArgumentRotation", "coordinates.RotationArgument"),
    SWIZZLE("coordinates.ArgumentRotationAxis", "coordinates.SwizzleArgument"),
    BLOCK_STATE("blocks.ArgumentTile", "blocks.BlockStateArgument"),
    BLOCK_PREDICATE("blocks.ArgumentBlockPredicate", "blocks.BlockPredicateArgument"),
    ITEM_STACK("item.ArgumentItemStack", "item.ItemArgument"),
    ITEM_PREDICATE("item.ArgumentItemPredicate", "item.ItemPredicateArgument"),
    FUNCTION("item.ArgumentTag", "item.FunctionArgument"),
    ENTITY_ANCHOR("ArgumentAnchor", "EntityAnchorArgument"),
    INT_RANGE("ArgumentCriterionValue$b", "RangeArgument$Ints"),
    FLOAT_RANGE("ArgumentCriterionValue$a", "RangeArgument$Floats"),
    TEMPLATE_MIRROR("TemplateMirrorArgument"),
    TEMPLATE_ROTATION("TemplateRotationArgument");

    private final Class<?>[] parameters;
    @Nullable
    private ArgumentType<?> argumentType;
    @Nullable
    private Constructor<? extends ArgumentType> argumentConstructor;

    private MinecraftArgumentType(String ... names) {
        this(names, new Class[0]);
    }

    private MinecraftArgumentType(String[] names, Class<?> ... parameters) {
        String name;
        Class<?> argumentClass = null;
        String[] stringArray = names;
        int n2 = stringArray.length;
        for (int i = 0; i < n2 && (argumentClass = MinecraftArgumentType.resolveArgumentClass(name = stringArray[i])) == null; ++i) {
        }
        this.parameters = parameters;
        if (argumentClass == null) {
            this.argumentType = null;
            this.argumentConstructor = null;
            return;
        }
        try {
            this.argumentConstructor = argumentClass.asSubclass(ArgumentType.class).getDeclaredConstructor(parameters);
            if (!this.argumentConstructor.isAccessible()) {
                this.argumentConstructor.setAccessible(true);
            }
            this.argumentType = parameters.length == 0 ? this.argumentConstructor.newInstance(new Object[0]) : null;
        } catch (Throwable e) {
            this.argumentType = null;
            this.argumentConstructor = null;
        }
    }

    @Nullable
    private static Class<?> resolveArgumentClass(String name) {
        String strippedName = name.lastIndexOf(46) != -1 ? name.substring(name.lastIndexOf(46) + 1) : name;
        for (String s : Data.POSSIBLE_CLASS_NAMES) {
            String className = s.replace("{version}", BukkitVersion.version()).replace("{name}", name).replace("{stripped_name}", strippedName);
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException classNotFoundException) {
            }
        }
        return null;
    }

    public boolean isSupported() {
        return this.argumentConstructor != null;
    }

    public boolean requiresParameters() {
        return this.parameters.length != 0;
    }

    @NotNull
    public <T> ArgumentType<T> get() {
        if (this.argumentConstructor == null) {
            throw new IllegalArgumentException("Argument type '" + this.name().toLowerCase() + "' is not available on this version.");
        }
        if (this.argumentType != null) {
            return this.argumentType;
        }
        throw new IllegalArgumentException("This argument type requires " + this.parameters.length + " parameter(s) of type(s) " + Arrays.stream(this.parameters).map(Class::getName).collect(Collectors.joining(", ")) + ". Use #create() instead.");
    }

    @NotNull
    public <T> ArgumentType<T> create(Object ... arguments) {
        if (this.argumentConstructor == null) {
            throw new IllegalArgumentException("Argument type '" + this.name().toLowerCase() + "' is not available on this version.");
        }
        if (this.argumentType != null && arguments.length == 0) {
            return this.argumentType;
        }
        return this.argumentConstructor.newInstance(arguments);
    }

    @NotNull
    public <T> Optional<ArgumentType<T>> getIfPresent() {
        if (this.argumentConstructor == null) {
            return Optional.empty();
        }
        if (this.argumentType != null) {
            return Optional.of(this.argumentType);
        }
        throw new IllegalArgumentException("This argument type requires " + this.parameters.length + " parameter(s) of type(s) " + Arrays.stream(this.parameters).map(Class::getName).collect(Collectors.joining(", ")) + ". Use #create() instead.");
    }

    @NotNull
    public <T> Optional<ArgumentType<T>> createIfPresent(Object ... arguments) {
        if (this.argumentConstructor == null) {
            return Optional.empty();
        }
        if (this.argumentType != null && arguments.length == 0) {
            return Optional.of(this.argumentType);
        }
        return Optional.of(this.argumentConstructor.newInstance(arguments));
    }

    static class Data {
        private static final List<String> POSSIBLE_CLASS_NAMES = Arrays.asList("net.minecraft.server.{name}", "net.minecraft.server.{version}.{name}", "net.minecraft.commands.arguments.{name}", "net.minecraft.server.{version}.{stripped_name}");

        Data() {
        }
    }
}

