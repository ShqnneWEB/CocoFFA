/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.NodeType;

public class AnchorNode
extends Node {
    private final Node realNode;

    public AnchorNode(Node realNode) {
        super(realNode.getTag(), realNode.getStartMark(), realNode.getEndMark());
        this.realNode = realNode;
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.ANCHOR;
    }

    public Node getRealNode() {
        return this.realNode;
    }
}

