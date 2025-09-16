/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package hu.geri.libs.gui.components.util;

import com.google.common.primitives.Ints;
import hu.geri.libs.gui.components.exception.GuiException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class VersionHelper {
    private static final String CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();
    private static final int V1_11 = 1110;
    private static final int V1_13 = 1130;
    private static final int V1_14 = 1140;
    private static final int V1_16_5 = 1165;
    private static final int V1_12_1 = 1121;
    private static final int V1_20_1 = 1201;
    private static final int V1_20_5 = 1205;
    private static final int CURRENT_VERSION = VersionHelper.getCurrentVersion();
    public static final boolean IS_COMPONENT_LEGACY = CURRENT_VERSION < 1165;
    public static final boolean IS_ITEM_LEGACY = CURRENT_VERSION < 1130;
    public static final boolean IS_UNBREAKABLE_LEGACY = CURRENT_VERSION < 1110;
    public static final boolean IS_PDC_VERSION = CURRENT_VERSION >= 1140;
    public static final boolean IS_SKULL_OWNER_LEGACY = CURRENT_VERSION < 1121;
    public static final boolean IS_CUSTOM_MODEL_DATA = CURRENT_VERSION >= 1140;
    public static final boolean IS_PLAYER_PROFILE_API = CURRENT_VERSION >= 1201;
    public static final boolean IS_ITEM_NAME_COMPONENT = CURRENT_VERSION >= 1205;
    private static final boolean IS_PAPER = VersionHelper.checkPaper();
    public static final boolean IS_FOLIA = VersionHelper.checkFolia();

    private static boolean checkPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private static boolean checkFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private static int getCurrentVersion() {
        Integer version;
        Matcher matcher = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?").matcher(Bukkit.getBukkitVersion());
        StringBuilder stringBuilder = new StringBuilder();
        if (matcher.find()) {
            stringBuilder.append(matcher.group("version").replace(".", ""));
            String patch = matcher.group("patch");
            if (patch == null) {
                stringBuilder.append("0");
            } else {
                stringBuilder.append(patch.replace(".", ""));
            }
        }
        if ((version = Ints.tryParse(stringBuilder.toString())) == null) {
            throw new GuiException("Could not retrieve server version!");
        }
        return version;
    }

    public static Class<?> craftClass(@NotNull String name) throws ClassNotFoundException {
        return Class.forName(CRAFTBUKKIT_PACKAGE + "." + name);
    }
}

