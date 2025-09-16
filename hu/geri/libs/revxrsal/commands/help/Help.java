/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.help;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.exception.InvalidHelpPageException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

public interface Help {
    @NotNull
    public static <A extends CommandActor> @Unmodifiable List<ExecutableCommand<A>> paginate(@NotNull List<ExecutableCommand<A>> commands, @Range(from=1L, to=0x7FFFFFFFL) int page, @Range(from=1L, to=0x7FFFFFFFL) int elementsPerPage) throws InvalidHelpPageException {
        if (commands.isEmpty()) {
            return Collections.emptyList();
        }
        int size = Help.numberOfPages(commands.size(), elementsPerPage);
        if (page <= 0) {
            throw new InvalidHelpPageException(commands, page, elementsPerPage, size);
        }
        ArrayList<ExecutableCommand<A>> list = new ArrayList<ExecutableCommand<A>>();
        if (page > size) {
            throw new InvalidHelpPageException(commands, page, elementsPerPage, size);
        }
        int listIndex = page - 1;
        int l = Math.min(page * elementsPerPage, commands.size());
        for (int i = listIndex * elementsPerPage; i < l; ++i) {
            list.add(commands.get(i));
        }
        return Collections.unmodifiableList(list);
    }

    public static @Range(from=1L, to=0x7FFFFFFFFFFFFFFFL) int numberOfPages(@Range(from=0L, to=0x7FFFFFFFL) int numberOfEntries, @Range(from=1L, to=0x7FFFFFFFL) int elementsPerPage) {
        if (elementsPerPage < 1) {
            throw new IllegalArgumentException("Elements per page cannot be less than 1! (Found " + elementsPerPage + ")");
        }
        return numberOfEntries / elementsPerPage + (numberOfEntries % elementsPerPage == 0 ? 0 : 1);
    }

    public static interface RelatedCommands<A extends CommandActor>
    extends CommandList<A> {
    }

    public static interface SiblingCommands<A extends CommandActor>
    extends CommandList<A> {
    }

    public static interface ChildrenCommands<A extends CommandActor>
    extends CommandList<A> {
    }

    public static interface CommandList<A extends CommandActor>
    extends Iterable<ExecutableCommand<A>> {
        public @Unmodifiable List<ExecutableCommand<A>> all();

        public @Range(from=1L, to=0x7FFFFFFFL) int numberOfPages(@Range(from=1L, to=0x7FFFFFFFL) int var1);

        public @Unmodifiable List<ExecutableCommand<A>> paginate(@Range(from=1L, to=0x7FFFFFFFL) int var1, @Range(from=1L, to=0x7FFFFFFFL) int var2) throws InvalidHelpPageException;
    }
}

