/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.LongArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 */
package hu.geri.libs.revxrsal.commands.brigadier.types;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import hu.geri.libs.revxrsal.commands.annotation.Range;
import hu.geri.libs.revxrsal.commands.brigadier.types.ArgumentTypeFactory;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.util.Classes;

final class DefaultTypeFactories {
    public static final ArgumentTypeFactory<CommandActor> STRING = parameter -> {
        if (parameter.type() == String.class) {
            return parameter.isGreedy() ? StringArgumentType.greedyString() : StringArgumentType.string();
        }
        return null;
    };
    public static final ArgumentTypeFactory<CommandActor> INTEGER = parameter -> {
        if (Classes.wrap(parameter.type()) == Integer.class) {
            Range range = parameter.annotations().get(Range.class);
            if (range == null) {
                return IntegerArgumentType.integer();
            }
            return IntegerArgumentType.integer((int)((int)range.min()), (int)((int)range.max()));
        }
        return null;
    };
    public static final ArgumentTypeFactory<CommandActor> SHORT = parameter -> {
        if (Classes.wrap(parameter.type()) == Short.class) {
            Range range = parameter.annotations().get(Range.class);
            if (range == null) {
                return IntegerArgumentType.integer((int)Short.MIN_VALUE, (int)Short.MAX_VALUE);
            }
            return IntegerArgumentType.integer((int)((short)range.min()), (int)((short)range.max()));
        }
        return null;
    };
    public static final ArgumentTypeFactory<CommandActor> BYTE = parameter -> {
        if (Classes.wrap(parameter.type()) == Byte.class) {
            Range range = parameter.annotations().get(Range.class);
            if (range == null) {
                return IntegerArgumentType.integer((int)-128, (int)127);
            }
            return IntegerArgumentType.integer((int)((byte)range.min()), (int)((byte)range.max()));
        }
        return null;
    };
    public static final ArgumentTypeFactory<CommandActor> LONG = parameter -> {
        if (Classes.wrap(parameter.type()) == Long.class) {
            Range range = parameter.annotations().get(Range.class);
            if (range == null) {
                return LongArgumentType.longArg();
            }
            return LongArgumentType.longArg((long)((long)range.min()), (long)((long)range.max()));
        }
        return null;
    };
    public static final ArgumentTypeFactory<CommandActor> DOUBLE = parameter -> {
        if (Classes.wrap(parameter.type()) == Double.class) {
            Range range = parameter.annotations().get(Range.class);
            if (range == null) {
                return DoubleArgumentType.doubleArg();
            }
            return DoubleArgumentType.doubleArg((double)range.min(), (double)range.max());
        }
        return null;
    };
    public static final ArgumentTypeFactory<CommandActor> FLOAT = parameter -> {
        if (Classes.wrap(parameter.type()) == Float.class) {
            Range range = parameter.annotations().get(Range.class);
            if (range == null) {
                return FloatArgumentType.floatArg();
            }
            return FloatArgumentType.floatArg((float)((float)range.min()), (float)((float)range.max()));
        }
        return null;
    };
    public static final ArgumentTypeFactory<CommandActor> BOOLEAN = ArgumentTypeFactory.forType(Boolean.TYPE, BoolArgumentType.bool());
    public static final ArgumentTypeFactory<CommandActor> CHAR = ArgumentTypeFactory.forType(Character.TYPE, StringArgumentType.string());

    private DefaultTypeFactories() {
    }
}

