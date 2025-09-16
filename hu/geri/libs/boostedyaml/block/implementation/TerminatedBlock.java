/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.block.implementation;

import hu.geri.libs.boostedyaml.block.Block;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import org.jetbrains.annotations.Nullable;

public class TerminatedBlock
extends Block<Object> {
    public TerminatedBlock(@Nullable Node keyNode, @Nullable Node valueNode, @Nullable Object value) {
        super(keyNode, valueNode, value);
    }

    public TerminatedBlock(@Nullable Block<?> previous, @Nullable Object value) {
        super(previous, value);
    }

    @Override
    public boolean isSection() {
        return false;
    }
}

