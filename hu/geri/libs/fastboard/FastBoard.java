/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package hu.geri.libs.fastboard;

import hu.geri.libs.fastboard.FastBoardBase;
import hu.geri.libs.fastboard.FastReflection;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.util.Objects;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FastBoard
extends FastBoardBase<String> {
    private static final MethodHandle MESSAGE_FROM_STRING;
    private static final Object EMPTY_MESSAGE;

    public FastBoard(Player player) {
        super(player);
    }

    @Override
    public void updateTitle(String title) {
        Objects.requireNonNull(title, "title");
        if (!FastBoardBase.VersionType.V1_13.isHigherOrEqual() && title.length() > 32) {
            throw new IllegalArgumentException("Title is longer than 32 chars");
        }
        super.updateTitle(title);
    }

    public void updateLines(String ... lines) {
        Objects.requireNonNull(lines, "lines");
        if (!FastBoardBase.VersionType.V1_13.isHigherOrEqual()) {
            int lineCount = 0;
            for (String s : lines) {
                if (s != null && s.length() > 30) {
                    throw new IllegalArgumentException("Line " + lineCount + " is longer than 30 chars");
                }
                ++lineCount;
            }
        }
        super.updateLines(lines);
    }

    @Override
    protected void sendLineChange(int score) throws Throwable {
        String prefix;
        int maxLength = this.hasLinesMaxLength() ? 16 : 1024;
        String line = (String)this.getLineByScore(score);
        String suffix = "";
        if (line == null || line.isEmpty()) {
            prefix = COLOR_CODES[score] + ChatColor.RESET;
        } else if (line.length() <= maxLength) {
            prefix = line;
        } else {
            int index = line.charAt(maxLength - 1) == '\u00a7' ? maxLength - 1 : maxLength;
            prefix = line.substring(0, index);
            String suffixTmp = line.substring(index);
            ChatColor chatColor = null;
            if (suffixTmp.length() >= 2 && suffixTmp.charAt(0) == '\u00a7') {
                chatColor = ChatColor.getByChar((char)suffixTmp.charAt(1));
            }
            String color = ChatColor.getLastColors((String)prefix);
            boolean addColor = chatColor == null || chatColor.isFormat();
            suffix = (addColor ? (color.isEmpty() ? ChatColor.RESET.toString() : color) : "") + suffixTmp;
        }
        if (prefix.length() > maxLength || suffix.length() > maxLength) {
            prefix = prefix.substring(0, Math.min(maxLength, prefix.length()));
            suffix = suffix.substring(0, Math.min(maxLength, suffix.length()));
        }
        this.sendTeamPacket(score, FastBoardBase.TeamMode.UPDATE, prefix, suffix);
    }

    @Override
    protected Object toMinecraftComponent(String line) throws Throwable {
        if (line == null || line.isEmpty()) {
            return EMPTY_MESSAGE;
        }
        return Array.get(MESSAGE_FROM_STRING.invoke(line), 0);
    }

    @Override
    protected String serializeLine(String value) {
        return value;
    }

    @Override
    protected String emptyLine() {
        return "";
    }

    protected boolean hasLinesMaxLength() {
        return !FastBoardBase.VersionType.V1_13.isHigherOrEqual();
    }

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            Class<?> craftChatMessageClass = FastReflection.obcClass("util.CraftChatMessage");
            MESSAGE_FROM_STRING = lookup.unreflect(craftChatMessageClass.getMethod("fromString", String.class));
            EMPTY_MESSAGE = Array.get(MESSAGE_FROM_STRING.invoke(""), 0);
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }
}

