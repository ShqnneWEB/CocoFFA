/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.stream;

import hu.geri.libs.revxrsal.commands.stream.CharPredicate;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public interface MutableStringStream
extends StringStream {
    public char read();

    public String read(int var1);

    public void moveForward();

    public void skipWhitespace();

    public void moveBackward();

    public void moveForward(int var1);

    public void moveBackward(int var1);

    @NotNull
    public String consumeRemaining();

    public void skipToEnd();

    @NotNull
    public String readUnquotedString();

    @NotNull
    public String readString();

    @NotNull
    public String readUntil(char var1);

    @NotNull
    public String readWhile(CharPredicate var1);

    public float readFloat();

    public double readDouble();

    public int readInt();

    public long readLong();

    public short readShort();

    public byte readByte();

    public boolean readBoolean();

    public void setPosition(int var1);

    @Override
    @NotNull
    @Contract(pure=true, value="-> new")
    public @Unmodifiable StringStream toImmutableCopy();

    @Override
    @NotNull
    @Contract(pure=true, value="-> new")
    public MutableStringStream toMutableCopy();

    @NotNull
    @Contract(pure=true)
    public @Unmodifiable StringStream toImmutableView();
}

