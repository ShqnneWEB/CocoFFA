/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package hu.geri.libs.gui.components.nbt;

import hu.geri.libs.gui.components.nbt.NbtWrapper;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LegacyNbt
implements NbtWrapper {
    public static final String PACKAGE_NAME = Bukkit.getServer().getClass().getPackage().getName();
    public static final String NMS_VERSION = PACKAGE_NAME.substring(PACKAGE_NAME.lastIndexOf(46) + 1);
    private static Method getStringMethod;
    private static Method setStringMethod;
    private static Method setBooleanMethod;
    private static Method hasTagMethod;
    private static Method getTagMethod;
    private static Method setTagMethod;
    private static Method removeTagMethod;
    private static Method asNMSCopyMethod;
    private static Method asBukkitCopyMethod;
    private static Constructor<?> nbtCompoundConstructor;

    @Override
    public ItemStack setString(@NotNull ItemStack itemStack, String key, String value) {
        if (itemStack.getType() == Material.AIR) {
            return itemStack;
        }
        Object nmsItemStack = LegacyNbt.asNMSCopy(itemStack);
        Object itemCompound = LegacyNbt.hasTag(nmsItemStack) ? LegacyNbt.getTag(nmsItemStack) : LegacyNbt.newNBTTagCompound();
        LegacyNbt.setString(itemCompound, key, value);
        LegacyNbt.setTag(nmsItemStack, itemCompound);
        return LegacyNbt.asBukkitCopy(nmsItemStack);
    }

    @Override
    public ItemStack removeTag(@NotNull ItemStack itemStack, String key) {
        if (itemStack.getType() == Material.AIR) {
            return itemStack;
        }
        Object nmsItemStack = LegacyNbt.asNMSCopy(itemStack);
        Object itemCompound = LegacyNbt.hasTag(nmsItemStack) ? LegacyNbt.getTag(nmsItemStack) : LegacyNbt.newNBTTagCompound();
        LegacyNbt.remove(itemCompound, key);
        LegacyNbt.setTag(nmsItemStack, itemCompound);
        return LegacyNbt.asBukkitCopy(nmsItemStack);
    }

    @Override
    public ItemStack setBoolean(@NotNull ItemStack itemStack, String key, boolean value) {
        if (itemStack.getType() == Material.AIR) {
            return itemStack;
        }
        Object nmsItemStack = LegacyNbt.asNMSCopy(itemStack);
        Object itemCompound = LegacyNbt.hasTag(nmsItemStack) ? LegacyNbt.getTag(nmsItemStack) : LegacyNbt.newNBTTagCompound();
        LegacyNbt.setBoolean(itemCompound, key, value);
        LegacyNbt.setTag(nmsItemStack, itemCompound);
        return LegacyNbt.asBukkitCopy(nmsItemStack);
    }

    @Override
    @Nullable
    public String getString(@NotNull ItemStack itemStack, String key) {
        if (itemStack.getType() == Material.AIR) {
            return null;
        }
        Object nmsItemStack = LegacyNbt.asNMSCopy(itemStack);
        Object itemCompound = LegacyNbt.hasTag(nmsItemStack) ? LegacyNbt.getTag(nmsItemStack) : LegacyNbt.newNBTTagCompound();
        return LegacyNbt.getString(itemCompound, key);
    }

    private static void setString(Object itemCompound, String key, String value) {
        try {
            setStringMethod.invoke(itemCompound, key, value);
        } catch (IllegalAccessException | InvocationTargetException reflectiveOperationException) {
            // empty catch block
        }
    }

    private static void setBoolean(Object itemCompound, String key, boolean value) {
        try {
            setBooleanMethod.invoke(itemCompound, key, value);
        } catch (IllegalAccessException | InvocationTargetException reflectiveOperationException) {
            // empty catch block
        }
    }

    private static void remove(Object itemCompound, String key) {
        try {
            removeTagMethod.invoke(itemCompound, key);
        } catch (IllegalAccessException | InvocationTargetException reflectiveOperationException) {
            // empty catch block
        }
    }

    private static String getString(Object itemCompound, String key) {
        try {
            return (String)getStringMethod.invoke(itemCompound, key);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private static boolean hasTag(Object nmsItemStack) {
        try {
            return (Boolean)hasTagMethod.invoke(nmsItemStack, new Object[0]);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    public static Object getTag(Object nmsItemStack) {
        try {
            return getTagMethod.invoke(nmsItemStack, new Object[0]);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private static void setTag(Object nmsItemStack, Object itemCompound) {
        try {
            setTagMethod.invoke(nmsItemStack, itemCompound);
        } catch (IllegalAccessException | InvocationTargetException reflectiveOperationException) {
            // empty catch block
        }
    }

    private static Object newNBTTagCompound() {
        try {
            return nbtCompoundConstructor.newInstance(new Object[0]);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            return null;
        }
    }

    public static Object asNMSCopy(ItemStack itemStack) {
        try {
            return asNMSCopyMethod.invoke(null, itemStack);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    public static ItemStack asBukkitCopy(Object nmsItemStack) {
        try {
            return (ItemStack)asBukkitCopyMethod.invoke(null, nmsItemStack);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private static Class<?> getNMSClass(String className) {
        try {
            return Class.forName("net.minecraft.server." + NMS_VERSION + "." + className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static Class<?> getCraftItemStackClass() {
        try {
            return Class.forName("org.bukkit.craftbukkit." + NMS_VERSION + ".inventory.CraftItemStack");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    static {
        try {
            getStringMethod = Objects.requireNonNull(LegacyNbt.getNMSClass("NBTTagCompound")).getMethod("getString", String.class);
            removeTagMethod = Objects.requireNonNull(LegacyNbt.getNMSClass("NBTTagCompound")).getMethod("remove", String.class);
            setStringMethod = Objects.requireNonNull(LegacyNbt.getNMSClass("NBTTagCompound")).getMethod("setString", String.class, String.class);
            setBooleanMethod = Objects.requireNonNull(LegacyNbt.getNMSClass("NBTTagCompound")).getMethod("setBoolean", String.class, Boolean.TYPE);
            hasTagMethod = Objects.requireNonNull(LegacyNbt.getNMSClass("ItemStack")).getMethod("hasTag", new Class[0]);
            getTagMethod = Objects.requireNonNull(LegacyNbt.getNMSClass("ItemStack")).getMethod("getTag", new Class[0]);
            setTagMethod = Objects.requireNonNull(LegacyNbt.getNMSClass("ItemStack")).getMethod("setTag", LegacyNbt.getNMSClass("NBTTagCompound"));
            nbtCompoundConstructor = Objects.requireNonNull(LegacyNbt.getNMSClass("NBTTagCompound")).getDeclaredConstructor(new Class[0]);
            asNMSCopyMethod = Objects.requireNonNull(LegacyNbt.getCraftItemStackClass()).getMethod("asNMSCopy", ItemStack.class);
            asBukkitCopyMethod = Objects.requireNonNull(LegacyNbt.getCraftItemStackClass()).getMethod("asBukkitCopy", LegacyNbt.getNMSClass("ItemStack"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}

