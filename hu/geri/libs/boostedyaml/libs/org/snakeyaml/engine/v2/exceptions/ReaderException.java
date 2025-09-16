/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;

public class ReaderException
extends YamlEngineException {
    private final String name;
    private final int codePoint;
    private final int position;

    public ReaderException(String name, int position, int codePoint, String message) {
        super(message);
        this.name = name;
        this.codePoint = codePoint;
        this.position = position;
    }

    public String getName() {
        return this.name;
    }

    public int getCodePoint() {
        return this.codePoint;
    }

    public int getPosition() {
        return this.position;
    }

    @Override
    public String toString() {
        String s = new String(Character.toChars(this.codePoint));
        return "unacceptable code point '" + s + "' (0x" + Integer.toHexString(this.codePoint).toUpperCase() + ") " + this.getMessage() + "\nin \"" + this.name + "\", position " + this.position;
    }
}

