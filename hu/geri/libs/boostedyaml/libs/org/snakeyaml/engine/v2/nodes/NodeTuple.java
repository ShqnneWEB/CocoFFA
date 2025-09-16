/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import java.util.Objects;

public final class NodeTuple {
    private final Node keyNode;
    private final Node valueNode;

    public NodeTuple(Node keyNode, Node valueNode) {
        Objects.requireNonNull(keyNode, "keyNode must be provided.");
        Objects.requireNonNull(valueNode, "value Node must be provided");
        this.keyNode = keyNode;
        this.valueNode = valueNode;
    }

    public Node getKeyNode() {
        return this.keyNode;
    }

    public Node getValueNode() {
        return this.valueNode;
    }

    public String toString() {
        return "<NodeTuple keyNode=" + this.keyNode + "; valueNode=" + this.valueNode + ">";
    }
}

