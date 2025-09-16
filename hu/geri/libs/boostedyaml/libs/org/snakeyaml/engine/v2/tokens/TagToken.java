/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.tokens;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.tokens.TagTuple;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.tokens.Token;
import java.util.Objects;
import java.util.Optional;

public final class TagToken
extends Token {
    private final TagTuple value;

    public TagToken(TagTuple value, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
        Objects.requireNonNull(value);
        this.value = value;
    }

    public TagTuple getValue() {
        return this.value;
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.Tag;
    }
}

