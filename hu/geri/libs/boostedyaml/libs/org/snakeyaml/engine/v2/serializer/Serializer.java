/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.serializer;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.DumpSettings;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.comments.CommentLine;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.Anchor;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.emitter.Emitable;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.AliasEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.CommentEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.DocumentEndEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.DocumentStartEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.ImplicitTuple;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.MappingEndEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.MappingStartEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.ScalarEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.SequenceEndEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.SequenceStartEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.StreamEndEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.StreamStartEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.AnchorNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.NodeTuple;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.NodeType;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.ScalarNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.SequenceNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Serializer {
    private final DumpSettings settings;
    private final Emitable emitable;
    private final Set<Node> serializedNodes;
    private final Map<Node, Anchor> anchors;
    private final boolean dereferenceAliases;
    private final Set<Node> recursive;

    public Serializer(DumpSettings settings, Emitable emitable) {
        this.settings = settings;
        this.emitable = emitable;
        this.serializedNodes = new HashSet<Node>();
        this.anchors = new HashMap<Node, Anchor>();
        this.dereferenceAliases = settings.isDereferenceAliases();
        this.recursive = Collections.newSetFromMap(new IdentityHashMap());
    }

    public void serializeDocument(Node node) {
        this.emitable.emit(new DocumentStartEvent(this.settings.isExplicitStart(), this.settings.getYamlDirective(), this.settings.getTagDirective()));
        this.anchorNode(node);
        this.settings.getExplicitRootTag().ifPresent(node::setTag);
        this.serializeNode(node);
        this.emitable.emit(new DocumentEndEvent(this.settings.isExplicitEnd()));
        this.serializedNodes.clear();
        this.anchors.clear();
        this.recursive.clear();
    }

    public void emitStreamStart() {
        this.emitable.emit(new StreamStartEvent());
    }

    public void emitStreamEnd() {
        this.emitable.emit(new StreamEndEvent());
    }

    private void anchorNode(Node node) {
        Node realNode = node.getNodeType() == NodeType.ANCHOR ? ((AnchorNode)node).getRealNode() : node;
        if (this.anchors.containsKey(realNode)) {
            this.anchors.computeIfAbsent(realNode, a -> this.settings.getAnchorGenerator().nextAnchor(realNode));
        } else {
            this.anchors.put(realNode, realNode.getAnchor().isPresent() ? this.settings.getAnchorGenerator().nextAnchor(realNode) : null);
            switch (realNode.getNodeType()) {
                case SEQUENCE: {
                    SequenceNode seqNode = (SequenceNode)realNode;
                    List<Node> list = seqNode.getValue();
                    for (Node item : list) {
                        this.anchorNode(item);
                    }
                    break;
                }
                case MAPPING: {
                    MappingNode mappingNode = (MappingNode)realNode;
                    List<NodeTuple> map = mappingNode.getValue();
                    for (NodeTuple object : map) {
                        Node key = object.getKeyNode();
                        Node value = object.getValueNode();
                        this.anchorNode(key);
                        this.anchorNode(value);
                    }
                    break;
                }
            }
        }
    }

    private void serializeNode(Node node) {
        if (node.getNodeType() == NodeType.ANCHOR) {
            node = ((AnchorNode)node).getRealNode();
        }
        if (this.dereferenceAliases && this.recursive.contains(node)) {
            throw new YamlEngineException("Cannot dereferenceAliases for recursive structures.");
        }
        this.recursive.add(node);
        Optional<Anchor> tAlias = !this.dereferenceAliases ? Optional.ofNullable(this.anchors.get(node)) : Optional.empty();
        if (!this.dereferenceAliases && this.serializedNodes.contains(node)) {
            this.emitable.emit(new AliasEvent(tAlias));
        } else {
            this.serializedNodes.add(node);
            switch (node.getNodeType()) {
                case SCALAR: {
                    ScalarNode scalarNode = (ScalarNode)node;
                    this.serializeComments(node.getBlockComments());
                    Tag detectedTag = this.settings.getSchema().getScalarResolver().resolve(scalarNode.getValue(), true);
                    Tag defaultTag = this.settings.getSchema().getScalarResolver().resolve(scalarNode.getValue(), false);
                    ImplicitTuple tuple = new ImplicitTuple(node.getTag().equals(detectedTag), node.getTag().equals(defaultTag));
                    ScalarEvent event = new ScalarEvent(tAlias, Optional.of(node.getTag().getValue()), tuple, scalarNode.getValue(), scalarNode.getScalarStyle());
                    this.emitable.emit(event);
                    this.serializeComments(node.getInLineComments());
                    this.serializeComments(node.getEndComments());
                    break;
                }
                case SEQUENCE: {
                    SequenceNode seqNode = (SequenceNode)node;
                    this.serializeComments(node.getBlockComments());
                    boolean implicitS = node.getTag().equals(Tag.SEQ);
                    this.emitable.emit(new SequenceStartEvent(tAlias, Optional.of(node.getTag().getValue()), implicitS, seqNode.getFlowStyle()));
                    List<Node> list = seqNode.getValue();
                    for (Node item : list) {
                        this.serializeNode(item);
                    }
                    this.emitable.emit(new SequenceEndEvent());
                    this.serializeComments(node.getInLineComments());
                    this.serializeComments(node.getEndComments());
                    break;
                }
                default: {
                    this.serializeComments(node.getBlockComments());
                    boolean implicitM = node.getTag().equals(Tag.MAP);
                    MappingNode mappingNode = (MappingNode)node;
                    List<NodeTuple> map = mappingNode.getValue();
                    if (mappingNode.getTag() == Tag.COMMENT) break;
                    this.emitable.emit(new MappingStartEvent(tAlias, Optional.of(mappingNode.getTag().getValue()), implicitM, mappingNode.getFlowStyle(), Optional.empty(), Optional.empty()));
                    for (NodeTuple entry : map) {
                        Node key = entry.getKeyNode();
                        Node value = entry.getValueNode();
                        this.serializeNode(key);
                        this.serializeNode(value);
                    }
                    this.emitable.emit(new MappingEndEvent());
                    this.serializeComments(node.getInLineComments());
                    this.serializeComments(node.getEndComments());
                }
            }
        }
        this.recursive.remove(node);
    }

    private void serializeComments(List<CommentLine> comments) {
        if (comments == null) {
            return;
        }
        for (CommentLine line : comments) {
            CommentEvent commentEvent = new CommentEvent(line.getCommentType(), line.getValue(), line.getStartMark(), line.getEndMark());
            this.emitable.emit(commentEvent);
        }
    }
}

