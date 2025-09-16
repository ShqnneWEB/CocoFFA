/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.gui.builder.gui;

import hu.geri.libs.gui.builder.gui.BaseGuiBuilder;
import hu.geri.libs.gui.components.ScrollType;
import hu.geri.libs.gui.components.util.Legacy;
import hu.geri.libs.gui.guis.ScrollingGui;
import java.util.function.Consumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class ScrollingBuilder
extends BaseGuiBuilder<ScrollingGui, ScrollingBuilder> {
    private ScrollType scrollType;
    private int pageSize = 0;

    public ScrollingBuilder(@NotNull ScrollType scrollType) {
        this.scrollType = scrollType;
    }

    @NotNull
    @Contract(value="_ -> this")
    public ScrollingBuilder scrollType(@NotNull ScrollType scrollType) {
        this.scrollType = scrollType;
        return this;
    }

    @NotNull
    @Contract(value="_ -> this")
    public ScrollingBuilder pageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    @Override
    @NotNull
    @Contract(value=" -> new")
    public ScrollingGui create() {
        ScrollingGui gui = new ScrollingGui(this.getRows(), this.pageSize, Legacy.SERIALIZER.serialize(this.getTitle()), this.scrollType, this.getModifiers());
        Consumer consumer = this.getConsumer();
        if (consumer != null) {
            consumer.accept(gui);
        }
        return gui;
    }
}

