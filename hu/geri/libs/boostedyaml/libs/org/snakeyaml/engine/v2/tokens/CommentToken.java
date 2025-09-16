/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.tokens;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.comments.CommentType;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.tokens.Token;
import java.util.Objects;
import java.util.Optional;

public final class CommentToken
extends Token {
    private final CommentType type;
    private final String value;

    public CommentToken(CommentType type, String value, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
        Objects.requireNonNull(type);
        this.type = type;
        Objects.requireNonNull(value);
        this.value = value;
    }

    public CommentType getCommentType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.Comment;
    }
}

