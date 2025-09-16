/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.gui.builder.gui;

import hu.geri.libs.gui.builder.gui.BaseGuiBuilder;
import hu.geri.libs.gui.components.util.Legacy;
import hu.geri.libs.gui.guis.StorageGui;
import java.util.function.Consumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class StorageBuilder
extends BaseGuiBuilder<StorageGui, StorageBuilder> {
    @Override
    @NotNull
    @Contract(value=" -> new")
    public StorageGui create() {
        StorageGui gui = new StorageGui(this.getRows(), Legacy.SERIALIZER.serialize(this.getTitle()), this.getModifiers());
        Consumer consumer = this.getConsumer();
        if (consumer != null) {
            consumer.accept(gui);
        }
        return gui;
    }
}

