/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.nbt;

import java.io.BufferedOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.IOStreamUtil;
import org.jetbrains.annotations.NotNull;

final class BinaryTagWriterImpl
implements BinaryTagIO.Writer {
    static final BinaryTagIO.Writer INSTANCE = new BinaryTagWriterImpl();

    BinaryTagWriterImpl() {
    }

    @Override
    public void write(@NotNull CompoundBinaryTag tag, @NotNull Path path, @NotNull BinaryTagIO.Compression compression) throws IOException {
        try (OutputStream os = Files.newOutputStream(path, new OpenOption[0]);){
            this.write(tag, os, compression);
        }
    }

    @Override
    public void write(@NotNull CompoundBinaryTag tag, @NotNull OutputStream output, @NotNull BinaryTagIO.Compression compression) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(compression.compress(IOStreamUtil.closeShield(output))));){
            this.write(tag, dos);
        }
    }

    @Override
    public void write(@NotNull CompoundBinaryTag tag, @NotNull DataOutput output) throws IOException {
        output.writeByte(BinaryTagTypes.COMPOUND.id());
        output.writeUTF("");
        BinaryTagTypes.COMPOUND.write(tag, output);
    }

    @Override
    public void writeNamed( @NotNull Map.Entry<String, CompoundBinaryTag> tag, @NotNull Path path, @NotNull BinaryTagIO.Compression compression) throws IOException {
        try (OutputStream os = Files.newOutputStream(path, new OpenOption[0]);){
            this.writeNamed(tag, os, compression);
        }
    }

    @Override
    public void writeNamed( @NotNull Map.Entry<String, CompoundBinaryTag> tag, @NotNull OutputStream output, @NotNull BinaryTagIO.Compression compression) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(compression.compress(IOStreamUtil.closeShield(output))));){
            this.writeNamed(tag, dos);
        }
    }

    @Override
    public void writeNamed( @NotNull Map.Entry<String, CompoundBinaryTag> tag, @NotNull DataOutput output) throws IOException {
        output.writeByte(BinaryTagTypes.COMPOUND.id());
        output.writeUTF(tag.getKey());
        BinaryTagTypes.COMPOUND.write(tag.getValue(), output);
    }
}

