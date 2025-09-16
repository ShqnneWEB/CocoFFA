/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;

public interface ConstructNode {
    public Object construct(Node var1);

    default public void constructRecursive(Node node, Object object) {
        if (node.isRecursive()) {
            throw new IllegalStateException("Not implemented in " + this.getClass().getName());
        }
        throw new YamlEngineException("Unexpected recursive structure for Node: " + node);
    }
}

