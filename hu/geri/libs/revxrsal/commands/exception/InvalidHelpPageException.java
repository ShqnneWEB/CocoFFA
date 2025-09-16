/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.exception.ThrowableFromCommand;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

@ThrowableFromCommand
public class InvalidHelpPageException
extends RuntimeException {
    private final List<ExecutableCommand<CommandActor>> commands;
    private final int page;
    private final int elementsPerPage;
    private final int numberOfPages;

    public <A extends CommandActor> InvalidHelpPageException(List<ExecutableCommand<A>> commands, int page, int elementsPerPage, int numberOfPages) {
        this.commands = commands;
        this.page = page;
        this.elementsPerPage = elementsPerPage;
        this.numberOfPages = numberOfPages;
    }

    public int elementsPerPage() {
        return this.elementsPerPage;
    }

    public int page() {
        return this.page;
    }

    public @Range(from=1L, to=0x7FFFFFFFL) int numberOfPages() {
        return this.numberOfPages;
    }

    @NotNull
    public <A extends CommandActor> @Unmodifiable List<ExecutableCommand<A>> commands() {
        return this.commands;
    }
}

