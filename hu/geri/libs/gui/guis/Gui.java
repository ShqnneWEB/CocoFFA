/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.gui.guis;

import hu.geri.libs.gui.builder.gui.PaginatedBuilder;
import hu.geri.libs.gui.builder.gui.ScrollingBuilder;
import hu.geri.libs.gui.builder.gui.SimpleBuilder;
import hu.geri.libs.gui.builder.gui.StorageBuilder;
import hu.geri.libs.gui.components.GuiType;
import hu.geri.libs.gui.components.InteractionModifier;
import hu.geri.libs.gui.components.ScrollType;
import hu.geri.libs.gui.guis.BaseGui;
import java.util.Set;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Gui
extends BaseGui {
    public Gui(int rows, @NotNull String title, @NotNull Set<InteractionModifier> interactionModifiers) {
        super(rows, title, interactionModifiers);
    }

    public Gui(@NotNull GuiType guiType, @NotNull String title, @NotNull Set<InteractionModifier> interactionModifiers) {
        super(guiType, title, interactionModifiers);
    }

    @Deprecated
    public Gui(int rows, @NotNull String title) {
        super(rows, title);
    }

    @Deprecated
    public Gui(@NotNull String title) {
        super(1, title);
    }

    @Deprecated
    public Gui(@NotNull GuiType guiType, @NotNull String title) {
        super(guiType, title);
    }

    @NotNull
    @Contract(value="_ -> new")
    public static SimpleBuilder gui(@NotNull GuiType type) {
        return new SimpleBuilder(type);
    }

    @NotNull
    @Contract(value=" -> new")
    public static SimpleBuilder gui() {
        return Gui.gui(GuiType.CHEST);
    }

    @NotNull
    @Contract(value=" -> new")
    public static StorageBuilder storage() {
        return new StorageBuilder();
    }

    @NotNull
    @Contract(value=" -> new")
    public static PaginatedBuilder paginated() {
        return new PaginatedBuilder();
    }

    @NotNull
    @Contract(value="_ -> new")
    public static ScrollingBuilder scrolling(@NotNull ScrollType scrollType) {
        return new ScrollingBuilder(scrollType);
    }

    @NotNull
    @Contract(value=" -> new")
    public static ScrollingBuilder scrolling() {
        return Gui.scrolling(ScrollType.VERTICAL);
    }
}

