/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common;

import java.util.Optional;

public enum ScalarStyle {
    DOUBLE_QUOTED(Optional.of(Character.valueOf('\"'))),
    SINGLE_QUOTED(Optional.of(Character.valueOf('\''))),
    LITERAL(Optional.of(Character.valueOf('|'))),
    FOLDED(Optional.of(Character.valueOf('>'))),
    JSON_SCALAR_STYLE(Optional.of(Character.valueOf('J'))),
    PLAIN(Optional.empty());

    private final Optional<Character> styleOpt;

    private ScalarStyle(Optional<Character> style) {
        this.styleOpt = style;
    }

    public String toString() {
        return String.valueOf(this.styleOpt.orElse(Character.valueOf(':')));
    }
}

