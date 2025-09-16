/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.stream;

import hu.geri.libs.revxrsal.commands.exception.InputParseException;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStreamImpl;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

class BaseStringStream
implements StringStream {
    protected static final char ESCAPE = '\\';
    protected static final char DOUBLE_QUOTE = '\"';
    protected String source;
    protected int pos;

    BaseStringStream(@NotNull String source) {
        this(source, 0);
    }

    BaseStringStream(@NotNull String source, int position) {
        this.source = source;
        this.pos = position;
    }

    @Override
    @NotNull
    public String source() {
        return this.source;
    }

    @Override
    public int totalSize() {
        return this.source.length();
    }

    @Override
    public int remaining() {
        return this.source.length() - this.pos;
    }

    @Override
    @Contract(pure=true)
    public char peek() {
        return this.source.charAt(this.pos);
    }

    @Override
    public String peek(int characters) {
        if (!this.canRead(characters)) {
            return this.peekRemaining();
        }
        return this.source.substring(this.pos, this.pos + characters);
    }

    @Override
    @Contract(pure=true)
    public char peekOffset(int offset) {
        return this.source.charAt(this.pos + offset);
    }

    @Override
    public boolean hasRemaining() {
        return this.canRead(1);
    }

    @Override
    public boolean hasFinished() {
        return !this.hasRemaining();
    }

    @Override
    public boolean canRead(int characters) {
        return this.pos + characters <= this.source.length();
    }

    @Override
    public int position() {
        return this.pos;
    }

    @NotNull
    public String readUnquotedString() {
        int start = this.pos;
        while (this.hasRemaining() && !Character.isWhitespace(this.peek())) {
            ++this.pos;
        }
        return this.source.substring(start, this.pos);
    }

    @NotNull
    public String readString() {
        if (!this.hasRemaining()) {
            return "";
        }
        char next = this.peek();
        if (next == '\"') {
            ++this.pos;
            return this.readUntil('\"');
        }
        return this.readUnquotedString();
    }

    public char read() {
        return this.source.charAt(this.pos++);
    }

    @NotNull
    public String readUntil(char delimiter) {
        return this.readUntil(delimiter, false);
    }

    @NotNull
    public String readUntil(char delimiter, boolean allowUnclosed) {
        StringBuilder result = new StringBuilder();
        boolean escaped = false;
        while (this.hasRemaining()) {
            char c = this.read();
            if (escaped) {
                if (c == delimiter || c == '\\') {
                    result.append(c);
                    escaped = false;
                    continue;
                }
                --this.pos;
                throw new InputParseException(InputParseException.Cause.INVALID_ESCAPE_CHARACTER);
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == delimiter) {
                return result.toString();
            }
            result.append(c);
        }
        if (allowUnclosed) {
            return result.toString();
        }
        throw new InputParseException(InputParseException.Cause.UNCLOSED_QUOTE);
    }

    @Override
    @NotNull
    public String peekString() {
        if (!this.hasRemaining()) {
            return "";
        }
        int cursor = this.pos++;
        char next = this.peek();
        if (next == '\"') {
            String result = this.readUntil('\"', true);
            this.pos = cursor;
            return result;
        }
        return this.peekUnquotedString();
    }

    @Override
    @NotNull
    public String peekUnquotedString() {
        int cursor = this.pos;
        String value = this.readUnquotedString();
        this.pos = cursor;
        return value;
    }

    @Override
    @NotNull
    public String peekRemaining() {
        if (this.hasFinished()) {
            return "";
        }
        return this.source.substring(this.pos);
    }

    @Override
    @NotNull
    public @Unmodifiable StringStream toImmutableCopy() {
        return new BaseStringStream(this.source, this.pos);
    }

    @Override
    @NotNull
    @Contract(value="-> new", pure=true)
    public MutableStringStream toMutableCopy() {
        return new MutableStringStreamImpl(this.source, this.pos);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.source.isEmpty();
    }
}

