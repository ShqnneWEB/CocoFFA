/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.serializer;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.Anchor;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;

public interface AnchorGenerator {
    public Anchor nextAnchor(Node var1);
}

