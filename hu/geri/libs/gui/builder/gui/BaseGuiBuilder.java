/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.gui.builder.gui;

import hu.geri.libs.gui.components.InteractionModifier;
import hu.geri.libs.gui.components.exception.GuiException;
import hu.geri.libs.gui.guis.BaseGui;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseGuiBuilder<G extends BaseGui, B extends BaseGuiBuilder<G, B>> {
    private Component title = null;
    private int rows = 1;
    private final EnumSet<InteractionModifier> interactionModifiers = EnumSet.noneOf(InteractionModifier.class);
    private Consumer<G> consumer;

    @NotNull
    @Contract(value="_ -> this")
    public B rows(int rows) {
        this.rows = rows;
        return (B)this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public B title(@NotNull Component title) {
        this.title = title;
        return (B)this;
    }

    @NotNull
    @Contract(value=" -> this")
    public B disableItemPlace() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_PLACE);
        return (B)this;
    }

    @NotNull
    @Contract(value=" -> this")
    public B disableItemTake() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_TAKE);
        return (B)this;
    }

    @NotNull
    @Contract(value=" -> this")
    public B disableItemSwap() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_SWAP);
        return (B)this;
    }

    @NotNull
    @Contract(value=" -> this")
    public B disableItemDrop() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_DROP);
        return (B)this;
    }

    @NotNull
    @Contract(value=" -> this")
    public B disableOtherActions() {
        this.interactionModifiers.add(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return (B)this;
    }

    @NotNull
    @Contract(value=" -> this")
    public B disableAllInteractions() {
        this.interactionModifiers.addAll(InteractionModifier.VALUES);
        return (B)this;
    }

    @NotNull
    @Contract(value=" -> this")
    public B enableItemPlace() {
        this.interactionModifiers.remove((Object)InteractionModifier.PREVENT_ITEM_PLACE);
        return (B)this;
    }

    @NotNull
    @Contract(value=" -> this")
    public B enableItemTake() {
        this.interactionModifiers.remove((Object)InteractionModifier.PREVENT_ITEM_TAKE);
        return (B)this;
    }

    @NotNull
    @Contract(value=" -> this")
    public B enableItemSwap() {
        this.interactionModifiers.remove((Object)InteractionModifier.PREVENT_ITEM_SWAP);
        return (B)this;
    }

    @NotNull
    @Contract(value=" -> this")
    public B enableItemDrop() {
        this.interactionModifiers.remove((Object)InteractionModifier.PREVENT_ITEM_DROP);
        return (B)this;
    }

    @NotNull
    @Contract(value=" -> this")
    public B enableOtherActions() {
        this.interactionModifiers.remove((Object)InteractionModifier.PREVENT_OTHER_ACTIONS);
        return (B)this;
    }

    @NotNull
    @Contract(value=" -> this")
    public B enableAllInteractions() {
        this.interactionModifiers.clear();
        return (B)this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public B apply(@NotNull Consumer<G> consumer) {
        this.consumer = consumer;
        return (B)this;
    }

    @NotNull
    @Contract(value=" -> new")
    public abstract G create();

    @NotNull
    protected Component getTitle() {
        if (this.title == null) {
            throw new GuiException("GUI title is missing!");
        }
        return this.title;
    }

    protected int getRows() {
        return this.rows;
    }

    @Nullable
    protected Consumer<G> getConsumer() {
        return this.consumer;
    }

    @NotNull
    protected Set<InteractionModifier> getModifiers() {
        return this.interactionModifiers;
    }
}

