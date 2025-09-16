/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.resource;

public enum ResourcePackStatus {
    ACCEPTED(true),
    DECLINED(false),
    INVALID_URL(false),
    FAILED_DOWNLOAD(false),
    DOWNLOADED(true),
    FAILED_RELOAD(false),
    DISCARDED(false),
    SUCCESSFULLY_LOADED(false);

    private final boolean intermediate;

    private ResourcePackStatus(boolean intermediate) {
        this.intermediate = intermediate;
    }

    public boolean intermediate() {
        return this.intermediate;
    }
}

