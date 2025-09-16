/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.stream;

import hu.geri.libs.revxrsal.commands.exception.InputParseException;
import hu.geri.libs.revxrsal.commands.exception.InvalidBooleanException;
import hu.geri.libs.revxrsal.commands.exception.InvalidDecimalException;
import hu.geri.libs.revxrsal.commands.exception.InvalidIntegerException;
import hu.geri.libs.revxrsal.commands.stream.BaseStringStream;
import hu.geri.libs.revxrsal.commands.stream.CharPredicate;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import java.util.Locale;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class MutableStringStreamImpl
extends BaseStringStream
implements MutableStringStream {
    private StringStreamView immutableView = null;

    MutableStringStreamImpl(String source) {
        super(source);
    }

    MutableStringStreamImpl(String source, int position) {
        super(source, position);
    }

    @Override
    public String read(int characters) {
        if (!this.canRead(characters)) {
            return this.consumeRemaining();
        }
        return this.source.substring(this.pos, this.pos += characters);
    }

    @Override
    public void moveForward() {
        this.moveForward(1);
    }

    @Override
    public void skipWhitespace() {
        while (this.hasRemaining() && this.peek() == ' ') {
            this.moveForward();
        }
    }

    @Override
    public void moveBackward() {
        this.moveBackward(1);
    }

    @Override
    public void moveForward(int by) {
        this.pos += by;
    }

    @Override
    public void moveBackward(int by) {
        this.pos = Math.max(0, this.pos - by);
    }

    @Override
    @NotNull
    public String consumeRemaining() {
        if (this.hasFinished()) {
            return "";
        }
        String v = this.source.substring(this.pos);
        this.skipToEnd();
        return v;
    }

    @Override
    public void skipToEnd() {
        this.pos = this.source.length();
    }

    @ApiStatus.Internal
    public void extend(@NotNull String str) {
        this.source = this.source + str;
    }

    @Override
    @NotNull
    public String readUntil(char delimiter) {
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
                this.moveBackward(1);
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
        throw new InputParseException(InputParseException.Cause.UNCLOSED_QUOTE);
    }

    @Override
    @NotNull
    public String readWhile(CharPredicate predicate) {
        int start = this.pos;
        while (this.hasRemaining() && predicate.test(this.peek())) {
            this.moveForward();
        }
        return this.source.substring(start, this.pos);
    }

    @Override
    public float readFloat() {
        String value = this.readUnquotedString();
        try {
            float v = Float.parseFloat(value);
            if (Float.isFinite(v)) {
                return v;
            }
            throw new InvalidDecimalException(value);
        } catch (NumberFormatException e) {
            throw new InvalidDecimalException(value);
        }
    }

    @Override
    public double readDouble() {
        String value = this.readUnquotedString();
        try {
            double v = Double.parseDouble(value);
            if (Double.isFinite(v)) {
                return v;
            }
            throw new InvalidDecimalException(value);
        } catch (NumberFormatException e) {
            throw new InvalidDecimalException(value);
        }
    }

    @Override
    public int readInt() {
        String value = this.readUnquotedString();
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidIntegerException(value);
        }
    }

    @Override
    public long readLong() {
        String value = this.readUnquotedString();
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new InvalidIntegerException(value);
        }
    }

    @Override
    public short readShort() {
        String value = this.readUnquotedString();
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException e) {
            throw new InvalidIntegerException(value);
        }
    }

    @Override
    public byte readByte() {
        String value = this.readUnquotedString();
        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException e) {
            throw new InvalidIntegerException(value);
        }
    }

    @Override
    public boolean readBoolean() {
        String value = this.readString();
        switch (value.toLowerCase(Locale.ENGLISH)) {
            case "true": 
            case "yes": {
                return true;
            }
            case "false": 
            case "no": 
            case "nope": {
                return false;
            }
        }
        throw new InvalidBooleanException(value);
    }

    @Override
    public void setPosition(int pos) {
        this.pos = pos;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    @NotNull
    public @Unmodifiable StringStream toImmutableView() {
        if (this.immutableView == null) {
            this.immutableView = new StringStreamView();
        }
        return this.immutableView;
    }

    private class StringStreamView
    implements StringStream {
        private StringStreamView() {
        }

        @Override
        @NotNull
        public String source() {
            return MutableStringStreamImpl.this.source();
        }

        @Override
        public int totalSize() {
            return MutableStringStreamImpl.this.totalSize();
        }

        @Override
        public int remaining() {
            return MutableStringStreamImpl.this.remaining();
        }

        @Override
        public char peek() {
            return MutableStringStreamImpl.this.peek();
        }

        @Override
        public String peek(int characters) {
            return MutableStringStreamImpl.this.peek(characters);
        }

        @Override
        public char peekOffset(int offset) {
            return MutableStringStreamImpl.this.peekOffset(offset);
        }

        @Override
        public boolean hasRemaining() {
            return MutableStringStreamImpl.this.hasRemaining();
        }

        @Override
        public boolean hasFinished() {
            return MutableStringStreamImpl.this.hasFinished();
        }

        @Override
        public boolean canRead(int characters) {
            return MutableStringStreamImpl.this.canRead(characters);
        }

        @Override
        public int position() {
            return MutableStringStreamImpl.this.position();
        }

        @Override
        @NotNull
        public String peekUnquotedString() {
            return MutableStringStreamImpl.this.peekUnquotedString();
        }

        @Override
        @NotNull
        public String peekString() {
            return MutableStringStreamImpl.this.peekString();
        }

        @Override
        @NotNull
        public String peekRemaining() {
            return MutableStringStreamImpl.this.peekRemaining();
        }

        @Override
        @NotNull
        public @Unmodifiable StringStream toImmutableCopy() {
            return MutableStringStreamImpl.this.toImmutableCopy();
        }

        @Override
        @NotNull
        public MutableStringStream toMutableCopy() {
            return MutableStringStreamImpl.this.toMutableCopy();
        }

        @Override
        public boolean isMutable() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return MutableStringStreamImpl.this.isEmpty();
        }
    }
}

