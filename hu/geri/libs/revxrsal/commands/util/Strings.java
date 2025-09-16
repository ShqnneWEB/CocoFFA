/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.util;

import hu.geri.libs.revxrsal.commands.annotation.Named;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Strings {
    public static final Pattern SPACE = Pattern.compile(" ", 16);
    public static final Pattern SNOWFLAKE = Pattern.compile("<(@!|@|@&|#)(?<snowflake>\\d{18})>");

    private Strings() {
        Preconditions.cannotInstantiate(Strings.class);
    }

    @Nullable
    public static String getSnowflake(String mention) {
        Matcher matcher = SNOWFLAKE.matcher(mention);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return null;
    }

    public static Optional<String> getOverriddenName(@NotNull AnnotationList parameter) {
        Named named = parameter.get(Named.class);
        if (named != null) {
            return Optional.of(named.value());
        }
        return Optional.empty();
    }

    @NotNull
    public static String stripNamespace(String namespace, @NotNull String command) {
        int colon = command.indexOf(namespace + ':');
        if (colon == -1) {
            return command;
        }
        return command.substring(namespace.length() + 1);
    }

    @NotNull
    public static String stripNamespace(@NotNull String command) {
        int colon = command.indexOf(58);
        if (colon == -1) {
            return command;
        }
        return command.substring(colon + 1);
    }

    @NotNull
    public static String removeRanges(@NotNull String input, @NotNull List<StringRange> ranges) {
        ranges.sort(Comparator.comparingInt(StringRange::start));
        StringBuilder builder = new StringBuilder();
        int currentIndex = 0;
        for (StringRange range : ranges) {
            if (currentIndex < range.start()) {
                builder.append(input, currentIndex, range.start());
            }
            currentIndex = range.end();
        }
        if (currentIndex < input.length()) {
            builder.append(input, currentIndex, input.length());
        }
        return builder.toString();
    }

    public static final class StringRange {
        private final int start;
        private final int end;

        public StringRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int start() {
            return this.start;
        }

        public int end() {
            return this.end;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            StringRange that = (StringRange)obj;
            return this.start == that.start && this.end == that.end;
        }

        public int hashCode() {
            return Objects.hash(this.start, this.end);
        }

        public String toString() {
            return "StringRange[start=" + this.start + ", end=" + this.end + ']';
        }
    }
}

