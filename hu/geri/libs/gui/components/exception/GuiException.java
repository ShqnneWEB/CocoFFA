/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.gui.components.exception;

public final class GuiException
extends RuntimeException {
    public GuiException(String message) {
        super(message);
    }

    public GuiException(String message, Exception cause) {
        super(message, cause);
    }
}

