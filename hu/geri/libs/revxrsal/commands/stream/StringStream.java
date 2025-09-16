/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.stream;

import hu.geri.libs.revxrsal.commands.stream.BaseStringStream;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStreamImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public interface StringStream {
    @NotNull
    public static StringStream create(@NotNull String source, int position) {
        return new BaseStringStream(source, position);
    }

    @NotNull
    public static StringStream create(@NotNull String source) {
        return new BaseStringStream(source);
    }

    @NotNull
    public static MutableStringStream createMutable(@NotNull String source, int position) {
        return new MutableStringStreamImpl(source, position);
    }

    @NotNull
    public static MutableStringStream createMutable(@NotNull String source) {
        return new MutableStringStreamImpl(source);
    }

    @NotNull
    public String source();

    public int totalSize();

    public int remaining();

    public char peek();

    public String peek(int var1);

    public char peekOffset(int var1);

    public boolean hasRemaining();

    public boolean hasFinished();

    public boolean canRead(int var1);

    public int position();

    @NotNull
    public String peekUnquotedString();

    @NotNull
    public String peekString();

    @NotNull
    public String peekRemaining();

    @NotNull
    public @Unmodifiable StringStream toImmutableCopy();

    @NotNull
    public MutableStringStream toMutableCopy();

    public boolean isMutable();

    public boolean isEmpty();
}

