/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.SendableException;
import org.jetbrains.annotations.NotNull;

public class SendMessageException
extends SendableException {
    public SendMessageException() {
    }

    public SendMessageException(String message) {
        super(message);
    }

    @Override
    public void sendTo(@NotNull CommandActor actor) {
        if (this.getMessage().isEmpty()) {
            return;
        }
        actor.reply(this.getMessage());
    }
}

