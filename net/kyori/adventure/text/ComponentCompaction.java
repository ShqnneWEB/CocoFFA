/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.kyori.adventure.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextComponentImpl;
import net.kyori.adventure.text.VirtualComponent;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

final class ComponentCompaction {
    @VisibleForTesting
    static final boolean SIMPLIFY_STYLE_FOR_BLANK_COMPONENTS = false;

    private ComponentCompaction() {
    }

    static Component compact(@NotNull Component self, @Nullable Style parentStyle) {
        Component child;
        int i;
        TextComponent textComponent;
        int childrenSize;
        List<Component> children = self.children();
        Component optimized = self.children(Collections.emptyList());
        if (parentStyle != null) {
            optimized = optimized.style(self.style().unmerge(parentStyle));
        }
        if ((childrenSize = children.size()) == 0) {
            if (ComponentCompaction.isBlank(optimized)) {
                optimized = optimized.style(ComponentCompaction.simplifyStyleForBlank(optimized.style(), parentStyle));
            }
            return optimized;
        }
        if (childrenSize == 1 && ComponentCompaction.isText(optimized) && (textComponent = (TextComponent)optimized).content().isEmpty()) {
            Component child2 = children.get(0);
            return child2.style(child2.style().merge(optimized.style(), Style.Merge.Strategy.IF_ABSENT_ON_TARGET)).compact();
        }
        Style childParentStyle = optimized.style();
        if (parentStyle != null) {
            childParentStyle = childParentStyle.merge(parentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
        }
        ArrayList<Component> childrenToAppend = new ArrayList<Component>(children.size());
        for (i = 0; i < children.size(); ++i) {
            TextComponent textComponent2;
            child = children.get(i);
            if ((child = ComponentCompaction.compact(child, childParentStyle)).children().isEmpty() && ComponentCompaction.isText(child) && (textComponent2 = (TextComponent)child).content().isEmpty()) continue;
            childrenToAppend.add(child);
        }
        if (ComponentCompaction.isText(optimized)) {
            while (!childrenToAppend.isEmpty()) {
                Component child3 = (Component)childrenToAppend.get(0);
                Style childStyle = child3.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
                if (!ComponentCompaction.isText(child3) || !Objects.equals(childStyle, childParentStyle)) break;
                optimized = ComponentCompaction.joinText((TextComponent)optimized, (TextComponent)child3);
                childrenToAppend.remove(0);
                childrenToAppend.addAll(0, child3.children());
            }
        }
        i = 0;
        while (i + 1 < childrenToAppend.size()) {
            Style neighborStyle;
            Style childStyle;
            child = (Component)childrenToAppend.get(i);
            Component neighbor = (Component)childrenToAppend.get(i + 1);
            if (child.children().isEmpty() && ComponentCompaction.isText(child) && ComponentCompaction.isText(neighbor) && (childStyle = child.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET)).equals(neighborStyle = neighbor.style().merge(childParentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET))) {
                TextComponent combined = ComponentCompaction.joinText((TextComponent)child, (TextComponent)neighbor);
                childrenToAppend.set(i, combined);
                childrenToAppend.remove(i + 1);
                continue;
            }
            ++i;
        }
        if (childrenToAppend.isEmpty() && ComponentCompaction.isBlank(optimized)) {
            optimized = optimized.style(ComponentCompaction.simplifyStyleForBlank(optimized.style(), parentStyle));
        }
        return optimized.children(childrenToAppend);
    }

    private static boolean isBlank(Component component) {
        if (ComponentCompaction.isText(component)) {
            TextComponent textComponent = (TextComponent)component;
            String content = textComponent.content();
            for (int i = 0; i < content.length(); ++i) {
                char c = content.charAt(i);
                if (c == ' ') continue;
                return false;
            }
            return true;
        }
        return false;
    }

    @NotNull
    private static Style simplifyStyleForBlank(@NotNull Style style, @Nullable Style parentStyle) {
        return style;
    }

    private static TextComponent joinText(TextComponent one, TextComponent two) {
        return TextComponentImpl.create(two.children(), one.style(), one.content() + two.content());
    }

    private static boolean isText(Component component) {
        return component instanceof TextComponent && !(component instanceof VirtualComponent);
    }
}

