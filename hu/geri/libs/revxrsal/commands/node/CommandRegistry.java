/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.CheckReturnValue
 */
package hu.geri.libs.revxrsal.commands.node;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public interface CommandRegistry<A extends CommandActor>
extends Iterable<ExecutableCommand<A>> {
    @NotNull
    @Contract(pure=true)
    public Lamp<A> lamp();

    default public void execute(@NotNull A actor, @NotNull String input) {
        this.execute(actor, StringStream.create(input));
    }

    public void execute(@NotNull A var1, @NotNull StringStream var2);

    public void execute(@NotNull A var1, @NotNull ExecutableCommand<A> var2, @NotNull MutableStringStream var3);

    @NotNull
    public @UnmodifiableView List<ExecutableCommand<A>> commands();

    public void unregister(@NotNull ExecutableCommand<A> var1);

    public void unregisterIf(@NotNull @NotNull Predicate<@NotNull ExecutableCommand<A>> var1);

    public boolean any(@NotNull @NotNull Predicate<@NotNull ExecutableCommand<A>> var1);

    @NotNull
    @CheckReturnValue
    @Contract(value="_ -> new")
    public List<ExecutableCommand<A>> filter(@NotNull @NotNull Predicate<@NotNull ExecutableCommand<A>> var1);

    @Override
    @NotNull
    public @UnmodifiableView Iterator<ExecutableCommand<A>> iterator();
}

