/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.gui.components;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum InteractionModifier {
    PREVENT_ITEM_PLACE,
    PREVENT_ITEM_TAKE,
    PREVENT_ITEM_SWAP,
    PREVENT_ITEM_DROP,
    PREVENT_OTHER_ACTIONS;

    public static final Set<InteractionModifier> VALUES;

    static {
        VALUES = Collections.unmodifiableSet(EnumSet.allOf(InteractionModifier.class));
    }
}

