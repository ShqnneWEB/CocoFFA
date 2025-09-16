/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.composer;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.LoadSettings;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.comments.CommentEventsCollector;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.comments.CommentLine;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.comments.CommentType;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.Anchor;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.AliasEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.Event;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.MappingStartEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.NodeEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.ScalarEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.SequenceStartEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.ComposerException;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.NodeTuple;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.NodeType;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.ScalarNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.SequenceNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.parser.Parser;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.resolver.ScalarResolver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Composer
implements Iterator<Node> {
    protected final Parser parser;
    private final ScalarResolver scalarResolver;
    private final Map<Anchor, Node> anchors;
    private final Set<Node> recursiveNodes;
    private final LoadSettings settings;
    private final CommentEventsCollector blockCommentsCollector;
    private final CommentEventsCollector inlineCommentsCollector;
    private int nonScalarAliasesCount = 0;

    @Deprecated
    public Composer(Parser parser, LoadSettings settings) {
        this(settings, parser);
    }

    public Composer(LoadSettings settings, Parser parser) {
        this.parser = parser;
        this.scalarResolver = settings.getSchema().getScalarResolver();
        this.settings = settings;
        this.anchors = new HashMap<Anchor, Node>();
        this.recursiveNodes = new HashSet<Node>();
        this.blockCommentsCollector = new CommentEventsCollector(parser, CommentType.BLANK_LINE, CommentType.BLOCK);
        this.inlineCommentsCollector = new CommentEventsCollector(parser, CommentType.IN_LINE);
    }

    @Override
    public boolean hasNext() {
        if (this.parser.checkEvent(Event.ID.StreamStart)) {
            this.parser.next();
        }
        return !this.parser.checkEvent(Event.ID.StreamEnd);
    }

    public Optional<Node> getSingleNode() {
        this.parser.next();
        Optional<Node> document = Optional.empty();
        if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
            document = Optional.of(this.next());
        }
        if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
            Event event = this.parser.next();
            Optional<Mark> previousDocMark = document.flatMap(Node::getStartMark);
            throw new ComposerException("expected a single document in the stream", previousDocMark, "but found another document", event.getStartMark());
        }
        this.parser.next();
        return document;
    }

    @Override
    public Node next() {
        this.blockCommentsCollector.collectEvents();
        if (this.parser.checkEvent(Event.ID.StreamEnd)) {
            List<CommentLine> commentLines = this.blockCommentsCollector.consume();
            Optional<Mark> startMark = commentLines.get(0).getStartMark();
            List<NodeTuple> children = Collections.emptyList();
            MappingNode node = new MappingNode(Tag.COMMENT, false, children, FlowStyle.BLOCK, startMark, Optional.empty());
            node.setBlockComments(commentLines);
            return node;
        }
        this.parser.next();
        Node node = this.composeNode(Optional.empty());
        this.blockCommentsCollector.collectEvents();
        if (!this.blockCommentsCollector.isEmpty()) {
            node.setEndComments(this.blockCommentsCollector.consume());
        }
        this.parser.next();
        this.anchors.clear();
        this.recursiveNodes.clear();
        this.nonScalarAliasesCount = 0;
        return node;
    }

    private Node composeNode(Optional<Node> parent) {
        Node node;
        this.blockCommentsCollector.collectEvents();
        parent.ifPresent(this.recursiveNodes::add);
        if (this.parser.checkEvent(Event.ID.Alias)) {
            AliasEvent event = (AliasEvent)this.parser.next();
            Anchor anchor = event.getAlias();
            if (!this.anchors.containsKey(anchor)) {
                throw new ComposerException("found undefined alias " + anchor, event.getStartMark());
            }
            node = this.anchors.get(anchor);
            if (node.getNodeType() != NodeType.SCALAR) {
                ++this.nonScalarAliasesCount;
                if (this.nonScalarAliasesCount > this.settings.getMaxAliasesForCollections()) {
                    throw new YamlEngineException("Number of aliases for non-scalar nodes exceeds the specified max=" + this.settings.getMaxAliasesForCollections());
                }
            }
            if (this.recursiveNodes.remove(node)) {
                node.setRecursive(true);
            }
            this.blockCommentsCollector.consume();
            this.inlineCommentsCollector.collectEvents().consume();
        } else {
            NodeEvent event = (NodeEvent)this.parser.peekEvent();
            Optional<Anchor> anchor = event.getAnchor();
            node = this.parser.checkEvent(Event.ID.Scalar) ? this.composeScalarNode(anchor, this.blockCommentsCollector.consume()) : (this.parser.checkEvent(Event.ID.SequenceStart) ? this.composeSequenceNode(anchor) : this.composeMappingNode(anchor));
        }
        parent.ifPresent(this.recursiveNodes::remove);
        return node;
    }

    private void registerAnchor(Anchor anchor, Node node) {
        this.anchors.put(anchor, node);
        node.setAnchor(Optional.of(anchor));
    }

    protected Node composeScalarNode(Optional<Anchor> anchor, List<CommentLine> blockComments) {
        Tag nodeTag;
        ScalarEvent ev = (ScalarEvent)this.parser.next();
        Optional<String> tag = ev.getTag();
        boolean resolved = false;
        if (!tag.isPresent() || tag.get().equals("!")) {
            nodeTag = this.scalarResolver.resolve(ev.getValue(), ev.getImplicit().canOmitTagInPlainScalar());
            resolved = true;
        } else {
            nodeTag = new Tag(tag.get());
        }
        ScalarNode node = new ScalarNode(nodeTag, resolved, ev.getValue(), ev.getScalarStyle(), ev.getStartMark(), ev.getEndMark());
        anchor.ifPresent(a -> this.registerAnchor((Anchor)a, node));
        node.setBlockComments(blockComments);
        node.setInLineComments(this.inlineCommentsCollector.collectEvents().consume());
        return node;
    }

    protected SequenceNode composeSequenceNode(Optional<Anchor> anchor) {
        Tag nodeTag;
        SequenceStartEvent startEvent = (SequenceStartEvent)this.parser.next();
        Optional<String> tag = startEvent.getTag();
        boolean resolved = false;
        if (!tag.isPresent() || tag.get().equals("!")) {
            nodeTag = Tag.SEQ;
            resolved = true;
        } else {
            nodeTag = new Tag(tag.get());
        }
        ArrayList<Node> children = new ArrayList<Node>();
        SequenceNode node = new SequenceNode(nodeTag, resolved, children, startEvent.getFlowStyle(), startEvent.getStartMark(), Optional.empty());
        if (startEvent.isFlow()) {
            node.setBlockComments(this.blockCommentsCollector.consume());
        }
        anchor.ifPresent(a -> this.registerAnchor((Anchor)a, node));
        while (!this.parser.checkEvent(Event.ID.SequenceEnd)) {
            this.blockCommentsCollector.collectEvents();
            if (this.parser.checkEvent(Event.ID.SequenceEnd)) break;
            children.add(this.composeNode(Optional.of(node)));
        }
        if (startEvent.isFlow()) {
            node.setInLineComments(this.inlineCommentsCollector.collectEvents().consume());
        }
        Event endEvent = this.parser.next();
        node.setEndMark(endEvent.getEndMark());
        this.inlineCommentsCollector.collectEvents();
        if (!this.inlineCommentsCollector.isEmpty()) {
            node.setInLineComments(this.inlineCommentsCollector.consume());
        }
        return node;
    }

    protected Node composeMappingNode(Optional<Anchor> anchor) {
        Tag nodeTag;
        MappingStartEvent startEvent = (MappingStartEvent)this.parser.next();
        Optional<String> tag = startEvent.getTag();
        boolean resolved = false;
        if (!tag.isPresent() || tag.get().equals("!")) {
            nodeTag = Tag.MAP;
            resolved = true;
        } else {
            nodeTag = new Tag(tag.get());
        }
        ArrayList<NodeTuple> children = new ArrayList<NodeTuple>();
        MappingNode node = new MappingNode(nodeTag, resolved, children, startEvent.getFlowStyle(), startEvent.getStartMark(), Optional.empty());
        if (startEvent.isFlow()) {
            node.setBlockComments(this.blockCommentsCollector.consume());
        }
        anchor.ifPresent(a -> this.registerAnchor((Anchor)a, node));
        while (!this.parser.checkEvent(Event.ID.MappingEnd)) {
            this.blockCommentsCollector.collectEvents();
            if (this.parser.checkEvent(Event.ID.MappingEnd)) break;
            this.composeMappingChildren(children, node);
        }
        if (startEvent.isFlow()) {
            node.setInLineComments(this.inlineCommentsCollector.collectEvents().consume());
        }
        Event endEvent = this.parser.next();
        node.setEndMark(endEvent.getEndMark());
        this.inlineCommentsCollector.collectEvents();
        if (!this.inlineCommentsCollector.isEmpty()) {
            node.setInLineComments(this.inlineCommentsCollector.consume());
        }
        return node;
    }

    protected void composeMappingChildren(List<NodeTuple> children, MappingNode node) {
        Node itemKey = this.composeKeyNode(node);
        Node itemValue = this.composeValueNode(node);
        children.add(new NodeTuple(itemKey, itemValue));
    }

    protected Node composeKeyNode(MappingNode node) {
        return this.composeNode(Optional.of(node));
    }

    protected Node composeValueNode(MappingNode node) {
        return this.composeNode(Optional.of(node));
    }
}

