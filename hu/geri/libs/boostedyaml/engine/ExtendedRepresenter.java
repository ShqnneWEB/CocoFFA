/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.engine;

import hu.geri.libs.boostedyaml.YamlDocument;
import hu.geri.libs.boostedyaml.block.Block;
import hu.geri.libs.boostedyaml.block.Comments;
import hu.geri.libs.boostedyaml.block.implementation.Section;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.DumpSettings;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.RepresentToNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.comments.CommentLine;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.comments.CommentType;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.NodeTuple;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.ScalarNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.SequenceNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.representer.StandardRepresenter;
import hu.geri.libs.boostedyaml.settings.dumper.DumperSettings;
import hu.geri.libs.boostedyaml.settings.general.GeneralSettings;
import hu.geri.libs.boostedyaml.utils.format.NodeRole;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExtendedRepresenter
extends StandardRepresenter {
    private final GeneralSettings generalSettings;
    private final DumperSettings dumperSettings;
    private NodeRole nodeRole = NodeRole.KEY;

    public ExtendedRepresenter(@NotNull GeneralSettings generalSettings, @NotNull DumperSettings dumperSettings, @NotNull DumpSettings engineSettings) {
        super(engineSettings);
        this.generalSettings = generalSettings;
        this.dumperSettings = dumperSettings;
        RepresentSection representSection = new RepresentSection();
        RepresentSerializable representSerializable = new RepresentSerializable();
        this.representers.put(Section.class, representSection);
        this.representers.put(YamlDocument.class, representSection);
        this.representers.put(Enum.class, new RepresentEnum());
        this.representers.put(String.class, new RepresentString((RepresentToNode)this.representers.get(String.class)));
        for (Class<?> clazz : generalSettings.getSerializer().getSupportedClasses()) {
            this.representers.put(clazz, representSerializable);
        }
        for (Class<?> clazz : generalSettings.getSerializer().getSupportedParentClasses()) {
            this.parentClassRepresenters.put(clazz, representSerializable);
        }
    }

    public ExtendedRepresenter(@NotNull GeneralSettings generalSettings, @NotNull DumperSettings dumperSettings) {
        this(generalSettings, dumperSettings, dumperSettings.buildEngineSettings());
    }

    @Override
    protected Node representScalar(Tag tag, String value, ScalarStyle scalarStyle) {
        return super.representScalar(tag, value, this.dumperSettings.getScalarFormatter().format(tag, value, this.nodeRole, scalarStyle));
    }

    @Override
    protected Node representSequence(Tag tag, Iterable<?> sequence, FlowStyle flowStyle) {
        return super.representSequence(tag, sequence, this.dumperSettings.getSequenceFormatter().format(tag, sequence, this.nodeRole, flowStyle));
    }

    @Override
    protected Node representMapping(Tag tag, Map<?, ?> mapping, FlowStyle flowStyle) {
        return super.representMapping(tag, mapping, this.dumperSettings.getMappingFormatter().format(tag, mapping, this.nodeRole, flowStyle));
    }

    private Node applyComments(@Nullable Block<?> block, @NotNull NodeRole nodeRole, @NotNull Node node, boolean isRoot) {
        List<CommentLine> inline;
        if (block == null) {
            return node;
        }
        if (this.allowBlockComments(isRoot)) {
            node.setBlockComments(Comments.get(block, nodeRole, Comments.Position.BEFORE));
            node.setEndComments(Comments.get(block, nodeRole, Comments.Position.AFTER));
        }
        if ((inline = Comments.get(block, nodeRole, Comments.Position.INLINE)) != null && !inline.isEmpty()) {
            if (this.allowInlineComments(node)) {
                node.setInLineComments(inline);
            } else if (this.allowBlockComments(isRoot)) {
                ArrayList<CommentLine> before = node.getBlockComments() == null ? new ArrayList<CommentLine>(inline.size()) : new ArrayList<CommentLine>(node.getBlockComments());
                for (CommentLine line : inline) {
                    before.add(new CommentLine(line.getStartMark(), line.getEndMark(), line.getValue(), line.getCommentType() == CommentType.IN_LINE ? CommentType.BLOCK : line.getCommentType()));
                }
                node.setBlockComments(before);
            }
        }
        return node;
    }

    @Override
    protected NodeTuple representMappingEntry(Map.Entry<?, ?> entry) {
        Block block = entry.getValue() instanceof Block ? (Block)entry.getValue() : null;
        this.nodeRole = NodeRole.KEY;
        Node key = this.applyComments(block, this.nodeRole, this.representData(entry.getKey()), false);
        this.nodeRole = NodeRole.VALUE;
        Node value = this.applyComments(block, this.nodeRole, this.representData(block == null ? entry.getValue() : block.getStoredValue()), false);
        return new NodeTuple(key, value);
    }

    private boolean allowBlockComments(boolean isRoot) {
        return isRoot || this.settings.getDefaultFlowStyle() == FlowStyle.BLOCK;
    }

    private boolean allowInlineComments(@NotNull Node node) {
        return this.settings.getDefaultFlowStyle() == FlowStyle.BLOCK && node instanceof ScalarNode || this.settings.getDefaultFlowStyle() == FlowStyle.FLOW && (node instanceof SequenceNode || node instanceof MappingNode);
    }

    private class RepresentString
    implements RepresentToNode {
        private final RepresentToNode previous;

        private RepresentString(RepresentToNode previous) {
            this.previous = previous;
        }

        @Override
        public Node representData(Object data) {
            ScalarStyle previousStyle = ExtendedRepresenter.this.defaultScalarStyle;
            ExtendedRepresenter.this.defaultScalarStyle = ExtendedRepresenter.this.dumperSettings.getStringStyle();
            Node node = this.previous.representData(data);
            ExtendedRepresenter.this.defaultScalarStyle = previousStyle;
            return node;
        }
    }

    private class RepresentEnum
    implements RepresentToNode {
        private RepresentEnum() {
        }

        @Override
        public Node representData(Object data) {
            return ExtendedRepresenter.this.representData(((Enum)data).name());
        }
    }

    private class RepresentSection
    implements RepresentToNode {
        private RepresentSection() {
        }

        @Override
        public Node representData(Object data) {
            Section section = (Section)data;
            return ExtendedRepresenter.this.applyComments(section, NodeRole.VALUE, ExtendedRepresenter.this.representData(section.getStoredValue()), section.isRoot());
        }
    }

    private class RepresentSerializable
    implements RepresentToNode {
        private RepresentSerializable() {
        }

        @Override
        public Node representData(Object data) {
            Map<Object, Object> serialized = ExtendedRepresenter.this.generalSettings.getSerializer().serialize(data, ExtendedRepresenter.this.generalSettings.getDefaultMapSupplier());
            return ExtendedRepresenter.this.representData(serialized == null ? data : serialized);
        }
    }
}

