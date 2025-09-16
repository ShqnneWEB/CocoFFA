/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.block;

import hu.geri.libs.boostedyaml.block.Block;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.comments.CommentLine;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.comments.CommentType;
import hu.geri.libs.boostedyaml.utils.format.NodeRole;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Comments {
    public static final CommentLine BLANK_LINE = new CommentLine(Optional.empty(), Optional.empty(), "", CommentType.BLANK_LINE);

    @Nullable
    public static List<CommentLine> get(@NotNull Block<?> block, @NotNull NodeRole node, @NotNull Position position) {
        switch (position) {
            case BEFORE: {
                return node == NodeRole.KEY ? block.beforeKeyComments : block.beforeValueComments;
            }
            case INLINE: {
                return node == NodeRole.KEY ? block.inlineKeyComments : block.inlineValueComments;
            }
            case AFTER: {
                return node == NodeRole.KEY ? block.afterKeyComments : block.afterValueComments;
            }
        }
        return null;
    }

    @Deprecated
    @Nullable
    public static List<CommentLine> get(@NotNull Block<?> block, @NotNull NodeType node, @NotNull Position position) {
        return Comments.get(block, node.toRole(), position);
    }

    public static void set(@NotNull Block<?> block, @NotNull NodeRole node, @NotNull Position position, @Nullable List<CommentLine> comments) {
        if (comments != null) {
            comments = new ArrayList<CommentLine>(comments);
        }
        switch (position) {
            case BEFORE: {
                if (node == NodeRole.KEY) {
                    block.beforeKeyComments = comments;
                    break;
                }
                block.beforeValueComments = comments;
                break;
            }
            case INLINE: {
                if (node == NodeRole.KEY) {
                    block.inlineKeyComments = comments;
                    break;
                }
                block.inlineValueComments = comments;
                break;
            }
            case AFTER: {
                if (node == NodeRole.KEY) {
                    block.afterKeyComments = comments;
                    break;
                }
                block.afterValueComments = comments;
            }
        }
    }

    @Deprecated
    public static void set(@NotNull Block<?> block, @NotNull NodeType node, @NotNull Position position, @Nullable List<CommentLine> comments) {
        Comments.set(block, node.toRole(), position, comments);
    }

    public static void remove(@NotNull Block<?> block, @NotNull NodeRole node, @NotNull Position position) {
        Comments.set(block, node, position, null);
    }

    @Deprecated
    public static void remove(@NotNull Block<?> block, @NotNull NodeType node, @NotNull Position position) {
        Comments.set(block, node.toRole(), position, null);
    }

    public static void add(@NotNull Block<?> block, @NotNull NodeRole node, @NotNull Position position, @NotNull List<CommentLine> comments) {
        comments.forEach(comment -> Comments.add(block, node, position, comment));
    }

    @Deprecated
    public static void add(@NotNull Block<?> block, @NotNull NodeType node, @NotNull Position position, @NotNull List<CommentLine> comments) {
        comments.forEach(comment -> Comments.add(block, node.toRole(), position, comment));
    }

    public static void add(@NotNull Block<?> block, @NotNull NodeRole node, @NotNull Position position, @NotNull CommentLine comment) {
        switch (position) {
            case BEFORE: {
                if (node == NodeRole.KEY) {
                    if (block.beforeKeyComments == null) {
                        block.beforeKeyComments = new ArrayList<CommentLine>();
                    }
                    block.beforeKeyComments.add(comment);
                    break;
                }
                if (block.beforeValueComments == null) {
                    block.beforeValueComments = new ArrayList<CommentLine>();
                }
                block.beforeValueComments.add(comment);
                break;
            }
            case INLINE: {
                if (node == NodeRole.KEY) {
                    if (block.inlineKeyComments == null) {
                        block.inlineKeyComments = new ArrayList<CommentLine>();
                    }
                    block.inlineKeyComments.add(comment);
                    break;
                }
                if (block.inlineValueComments == null) {
                    block.inlineValueComments = new ArrayList<CommentLine>();
                }
                block.inlineValueComments.add(comment);
                break;
            }
            case AFTER: {
                if (node == NodeRole.KEY) {
                    if (block.afterKeyComments == null) {
                        block.afterKeyComments = new ArrayList<CommentLine>();
                    }
                    block.afterKeyComments.add(comment);
                    break;
                }
                if (block.afterValueComments == null) {
                    block.afterValueComments = new ArrayList<CommentLine>();
                }
                block.afterValueComments.add(comment);
            }
        }
    }

    @Deprecated
    public static void add(@NotNull Block<?> block, @NotNull NodeType node, @NotNull Position position, @NotNull CommentLine comment) {
        Comments.add(block, node.toRole(), position, comment);
    }

    @NotNull
    public static CommentLine create(@NotNull String comment, @NotNull Position position) {
        return new CommentLine(Optional.empty(), Optional.empty(), comment, position == Position.INLINE ? CommentType.IN_LINE : CommentType.BLOCK);
    }

    public static enum NodeType {
        KEY,
        VALUE;


        public NodeRole toRole() {
            return this == KEY ? NodeRole.KEY : NodeRole.VALUE;
        }
    }

    public static enum Position {
        BEFORE,
        INLINE,
        AFTER;

    }
}

