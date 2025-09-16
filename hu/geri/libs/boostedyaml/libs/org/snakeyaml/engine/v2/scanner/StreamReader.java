/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.scanner;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.LoadSettings;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.CharConstants;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.ReaderException;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Optional;

public final class StreamReader {
    private final String name;
    private final Reader stream;
    private final int bufferSize;
    private final char[] buffer;
    private final boolean useMarks;
    private int[] codePointsWindow;
    private int dataLength;
    private int pointer = 0;
    private boolean eof;
    private int index = 0;
    private int documentIndex = 0;
    private int line = 0;
    private int column = 0;

    @Deprecated
    public StreamReader(Reader reader, LoadSettings loadSettings) {
        this(loadSettings, reader);
    }

    public StreamReader(LoadSettings loadSettings, Reader reader) {
        this.name = loadSettings.getLabel();
        this.codePointsWindow = new int[0];
        this.dataLength = 0;
        this.stream = reader;
        this.eof = false;
        this.bufferSize = loadSettings.getBufferSize();
        this.buffer = new char[this.bufferSize];
        this.useMarks = loadSettings.getUseMarks();
    }

    @Deprecated
    public StreamReader(String stream, LoadSettings loadSettings) {
        this(loadSettings, new StringReader(stream));
    }

    public StreamReader(LoadSettings loadSettings, String stream) {
        this(loadSettings, new StringReader(stream));
    }

    public static boolean isPrintable(String data) {
        int codePoint;
        int length = data.length();
        for (int offset = 0; offset < length; offset += Character.charCount(codePoint)) {
            codePoint = data.codePointAt(offset);
            if (StreamReader.isPrintable(codePoint)) continue;
            return false;
        }
        return true;
    }

    public static boolean isPrintable(int c) {
        return c >= 32 && c <= 126 || c == 9 || c == 10 || c == 13 || c == 133 || c >= 160 && c <= 55295 || c >= 57344 && c <= 65533 || c >= 65536 && c <= 0x10FFFF;
    }

    public Optional<Mark> getMark() {
        if (this.useMarks) {
            return Optional.of(new Mark(this.name, this.index, this.line, this.column, this.codePointsWindow, this.pointer));
        }
        return Optional.empty();
    }

    public void forward() {
        this.forward(1);
    }

    public void forward(int length) {
        for (int i = 0; i < length && this.ensureEnoughData(); ++i) {
            int c = this.codePointsWindow[this.pointer++];
            this.moveIndices(1);
            if (CharConstants.LINEBR.has(c) || c == 13 && this.ensureEnoughData() && this.codePointsWindow[this.pointer] != 10) {
                ++this.line;
                this.column = 0;
                continue;
            }
            if (c == 65279) continue;
            ++this.column;
        }
    }

    public int peek() {
        return this.ensureEnoughData() ? this.codePointsWindow[this.pointer] : 0;
    }

    public int peek(int index) {
        return this.ensureEnoughData(index) ? this.codePointsWindow[this.pointer + index] : 0;
    }

    public String prefix(int length) {
        if (length == 0) {
            return "";
        }
        if (this.ensureEnoughData(length)) {
            return new String(this.codePointsWindow, this.pointer, length);
        }
        return new String(this.codePointsWindow, this.pointer, Math.min(length, this.dataLength - this.pointer));
    }

    public String prefixForward(int length) {
        String prefix = this.prefix(length);
        this.pointer += length;
        this.moveIndices(length);
        this.column += length;
        return prefix;
    }

    private boolean ensureEnoughData() {
        return this.ensureEnoughData(0);
    }

    private boolean ensureEnoughData(int size) {
        if (!this.eof && this.pointer + size >= this.dataLength) {
            this.update();
        }
        return this.pointer + size < this.dataLength;
    }

    private void update() {
        try {
            int read = this.stream.read(this.buffer, 0, this.bufferSize);
            if (read > 0) {
                int cpIndex = this.dataLength - this.pointer;
                this.codePointsWindow = Arrays.copyOfRange(this.codePointsWindow, this.pointer, this.dataLength + read);
                if (Character.isHighSurrogate(this.buffer[read - 1])) {
                    if (this.stream.read(this.buffer, read, 1) == -1) {
                        this.eof = true;
                    } else {
                        ++read;
                    }
                }
                Optional<Object> nonPrintable = Optional.empty();
                int i = 0;
                while (i < read) {
                    int codePoint;
                    this.codePointsWindow[cpIndex] = codePoint = Character.codePointAt(this.buffer, i);
                    if (StreamReader.isPrintable(codePoint)) {
                        i += Character.charCount(codePoint);
                    } else {
                        nonPrintable = Optional.of(codePoint);
                        i = read;
                    }
                    ++cpIndex;
                }
                this.dataLength = cpIndex;
                this.pointer = 0;
                if (nonPrintable.isPresent()) {
                    throw new ReaderException(this.name, cpIndex - 1, (Integer)nonPrintable.get(), "special characters are not allowed");
                }
            } else {
                this.eof = true;
            }
        } catch (IOException ioe) {
            throw new YamlEngineException(ioe);
        }
    }

    public int getColumn() {
        return this.column;
    }

    private void moveIndices(int length) {
        this.index += length;
        this.documentIndex += length;
    }

    public int getDocumentIndex() {
        return this.documentIndex;
    }

    public void resetDocumentIndex() {
        this.documentIndex = 0;
    }

    public int getIndex() {
        return this.index;
    }

    public int getLine() {
        return this.line;
    }
}

