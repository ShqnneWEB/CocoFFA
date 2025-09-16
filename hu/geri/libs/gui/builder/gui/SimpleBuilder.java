/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.gui.builder.gui;

import hu.geri.libs.gui.builder.gui.BaseGuiBuilder;
import hu.geri.libs.gui.components.GuiType;
import hu.geri.libs.gui.components.util.Legacy;
import hu.geri.libs.gui.guis.Gui;
import java.util.function.Consumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class SimpleBuilder
extends BaseGuiBuilder<Gui, SimpleBuilder> {
    private GuiType guiType;

    public SimpleBuilder(@NotNull GuiType guiType) {
        this.guiType = guiType;
    }

    @NotNull
    @Contract(value="_ -> this")
    public SimpleBuilder type(@NotNull GuiType guiType) {
        this.guiType = guiType;
        return this;
    }

    @Override
    @NotNull
    @Contract(value=" -> new")
    public Gui create() {
        String title = Legacy.SERIALIZER.serialize(this.getTitle());
        Gui gui = this.guiType == null || this.guiType == GuiType.CHEST ? new Gui(this.getRows(), title, this.getModifiers()) : new Gui(this.guiType, title, this.getModifiers());
        Consumer consumer = this.getConsumer();
        if (consumer != null) {
            consumer.accept(gui);
        }
        return gui;
    }
}

