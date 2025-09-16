/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.stream.token;

import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.stream.token.LiteralToken;
import hu.geri.libs.revxrsal.commands.stream.token.ParameterToken;
import hu.geri.libs.revxrsal.commands.stream.token.Token;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class TokenParser {
    @Contract(mutates="param1")
    @NotNull
    public static Token parseNextToken(@NotNull MutableStringStream stream) {
        if (stream.peek() == '<') {
            stream.moveForward();
            String name = stream.readUntil('>');
            if (name.isEmpty()) {
                throw new ParseException("Cannot have <> for an argument name!");
            }
            return new ParameterToken(name);
        }
        String name = stream.readUnquotedString();
        return new LiteralToken(name);
    }

    public static class ParseException
    extends RuntimeException {
        public ParseException(String message) {
            super(message);
        }
    }
}

