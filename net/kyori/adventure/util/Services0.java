/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.util;

import java.util.ServiceLoader;

final class Services0 {
    private Services0() {
    }

    static <S> ServiceLoader<S> loader(Class<S> type) {
        return ServiceLoader.load(type, type.getClassLoader());
    }
}

