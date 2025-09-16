/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.tokens;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.tokens.Token;
import java.util.Optional;

public final class FlowMappingEndToken
extends Token {
    public FlowMappingEndToken(Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.FlowMappingEnd;
    }
}

