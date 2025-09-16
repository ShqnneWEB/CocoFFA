/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 */
package hu.geri.libs.revxrsal.commands.bukkit.util;

import hu.geri.libs.revxrsal.commands.util.Classes;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BukkitVersion {
    private static final boolean IS_PAPER;
    private static final boolean SUPPORTS_ASYNC_COMPLETION;
    private static final int MAJOR_VERSION;
    private static final int MINOR_VERSION;
    private static final int PATCH_NUMBER;
    private static final String VERSION;
    private static final int UNVERSION_NMS = 17;
    private static final String CB_PACKAGE;

    private BukkitVersion() {
        Preconditions.cannotInstantiate(BukkitVersion.class);
    }

    @NotNull
    private static String fetchVersion() {
        Server server = Bukkit.getServer();
        try {
            String packageName = server.getClass().getPackage().getName();
            return packageName.substring(packageName.lastIndexOf(46) + 1);
        } catch (Throwable throwable) {
            return "";
        }
    }

    public static boolean supports(int major, int minor) {
        return BukkitVersion.supports(major, minor, 0);
    }

    public static boolean supports(int major, int minor, int patch) {
        if (MAJOR_VERSION > major) {
            return true;
        }
        if (MAJOR_VERSION == major) {
            if (MINOR_VERSION > minor) {
                return true;
            }
            if (MINOR_VERSION == minor) {
                return PATCH_NUMBER >= patch;
            }
        }
        return false;
    }

    public static int minorVersion() {
        return MINOR_VERSION;
    }

    public static int patchNumber() {
        return PATCH_NUMBER;
    }

    @NotNull
    public static String version() {
        return VERSION;
    }

    @NotNull
    public static Class<?> findNmsClass(@NotNull String ... names) {
        for (String name : names) {
            Class<?> c = BukkitVersion.classOrNull("net.minecraft.server." + name);
            if (c != null) {
                return c;
            }
            c = BukkitVersion.classOrNull("net.minecraft.server." + VERSION + "." + name);
            if (c != null) {
                return c;
            }
            c = BukkitVersion.classOrNull("net.minecraft." + name);
            if (c == null) continue;
            return c;
        }
        throw new IllegalStateException("Class not found. Names searched: " + Arrays.toString(names) + ".");
    }

    @NotNull
    public static Class<?> findOcbClass(@NotNull String name) {
        return Class.forName(CB_PACKAGE + '.' + name);
    }

    public static boolean isPaper() {
        return IS_PAPER;
    }

    public static boolean supportsAsyncCompletion() {
        return SUPPORTS_ASYNC_COMPLETION;
    }

    public static boolean isBrigadierSupported() {
        if (BukkitVersion.supports(1, 19, 1)) {
            return BukkitVersion.isPaper();
        }
        return BukkitVersion.supports(1, 13);
    }

    @Nullable
    private static Class<?> classOrNull(@NotNull String name) {
        try {
            return Class.forName(name);
        } catch (Throwable t) {
            return null;
        }
    }

    static {
        VERSION = BukkitVersion.fetchVersion();
        CB_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();
        Pattern dot = Pattern.compile(".", 16);
        String bukkitVersion = Bukkit.getServer().getBukkitVersion();
        String[] version = bukkitVersion.indexOf(45) == -1 ? dot.split(bukkitVersion) : dot.split(bukkitVersion.substring(0, bukkitVersion.indexOf(45)));
        if (version.length == 2) {
            version = new String[]{version[0], version[1], "0"};
        }
        MAJOR_VERSION = Integer.parseInt(version[0]);
        MINOR_VERSION = Integer.parseInt(version[1]);
        String minorSlice = version[2];
        if (minorSlice.indexOf(45) != -1) {
            minorSlice = minorSlice.substring(0, minorSlice.indexOf(45));
        }
        PATCH_NUMBER = Integer.parseInt(minorSlice);
        IS_PAPER = Classes.isClassPresent("com.destroystokyo.paper.PaperConfig");
        SUPPORTS_ASYNC_COMPLETION = Classes.isClassPresent("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent");
    }
}

