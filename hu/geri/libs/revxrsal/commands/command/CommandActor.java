/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.command;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.process.MessageSender;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public interface CommandActor {
    @NotNull
    public String name();

    @NotNull
    public UUID uniqueId();

    default public void reply(@NotNull String message) {
        MessageSender<?, String> messageSender = this.lamp().messageSender();
        messageSender.send(this, message);
    }

    public void sendRawMessage(@NotNull String var1);

    default public void error(@NotNull String message) {
        MessageSender<?, String> messageSender = this.lamp().errorSender();
        messageSender.send(this, message);
    }

    public void sendRawError(@NotNull String var1);

    public Lamp<?> lamp();
}

