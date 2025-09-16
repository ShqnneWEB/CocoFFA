/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter.builtins;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ContextParameter;
import hu.geri.libs.revxrsal.commands.process.SenderResolver;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class SenderContextParameter<A extends CommandActor, T>
implements ContextParameter<A, T> {
    private final SenderResolver<A> resolver;

    public SenderContextParameter(SenderResolver<A> resolver) {
        this.resolver = resolver;
    }

    @Override
    public T resolve(@NotNull CommandParameter parameter, @NotNull ExecutionContext<A> context) {
        A actor = context.actor();
        Object sender = this.resolver.getSender(parameter.type(), actor, context.command());
        Preconditions.notNull(sender, "SenderResolver#getSender()");
        return (T)sender;
    }

    public SenderResolver<A> resolver() {
        return this.resolver;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        SenderContextParameter that = (SenderContextParameter)obj;
        return Objects.equals(this.resolver, that.resolver);
    }

    public int hashCode() {
        return Objects.hash(this.resolver);
    }

    public String toString() {
        return "SenderContextParameter[resolver=" + this.resolver + ']';
    }
}

