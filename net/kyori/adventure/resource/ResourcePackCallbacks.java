/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.resource;

import net.kyori.adventure.resource.ResourcePackCallback;

final class ResourcePackCallbacks {
    static final ResourcePackCallback NO_OP = (uuid, status, audience) -> {};

    private ResourcePackCallbacks() {
    }
}

