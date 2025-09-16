/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package hu.geri.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageProcessor {
    private static final char COLOR_CHAR = '\u00a7';
    private static final Pattern AMP_HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern HASH_HEX_PATTERN = Pattern.compile("(?<!&)#([A-Fa-f0-9]{6})");

    @NotNull
    public static String process(@Nullable String message) {
        if (message == null) {
            return "";
        }
        String processed = MessageProcessor.replaceHex(message, AMP_HEX_PATTERN);
        processed = MessageProcessor.replaceHex(processed, HASH_HEX_PATTERN);
        return ChatColor.translateAlternateColorCodes((char)'&', (String)processed);
    }

    @NotNull
    private static String replaceHex(@NotNull String input, @NotNull Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        StringBuilder builder = new StringBuilder(input.length() + 32);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(builder, new StringBuilder(14).append('\u00a7').append('x').append('\u00a7').append(group.charAt(0)).append('\u00a7').append(group.charAt(1)).append('\u00a7').append(group.charAt(2)).append('\u00a7').append(group.charAt(3)).append('\u00a7').append(group.charAt(4)).append('\u00a7').append(group.charAt(5)).toString());
        }
        return matcher.appendTail(builder).toString();
    }
}

