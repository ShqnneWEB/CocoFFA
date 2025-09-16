/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.gui.builder.gui;

import hu.geri.libs.gui.builder.gui.BaseGuiBuilder;
import hu.geri.libs.gui.components.util.Legacy;
import hu.geri.libs.gui.guis.PaginatedGui;
import java.util.function.Consumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PaginatedBuilder
extends BaseGuiBuilder<PaginatedGui, PaginatedBuilder> {
    private int pageSize = 0;

    @NotNull
    @Contract(value="_ -> this")
    public PaginatedBuilder pageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    @Override
    @NotNull
    @Contract(value=" -> new")
    public PaginatedGui create() {
        PaginatedGui gui = new PaginatedGui(this.getRows(), this.pageSize, Legacy.SERIALIZER.serialize(this.getTitle()), this.getModifiers());
        Consumer consumer = this.getConsumer();
        if (consumer != null) {
            consumer.accept(gui);
        }
        return gui;
    }
}

