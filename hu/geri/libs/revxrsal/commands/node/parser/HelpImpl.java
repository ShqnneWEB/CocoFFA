/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node.parser;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.help.Help;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

final class HelpImpl {
    HelpImpl() {
    }

    static final class SiblingCommandsImpl<A extends CommandActor>
    extends CommandListImpl<A>
    implements Help.SiblingCommands<A> {
        public SiblingCommandsImpl(@Unmodifiable List<ExecutableCommand<A>> commands) {
            super(commands);
        }
    }

    static final class ChildrenCommandsImpl<A extends CommandActor>
    extends CommandListImpl<A>
    implements Help.ChildrenCommands<A> {
        public ChildrenCommandsImpl(@Unmodifiable List<ExecutableCommand<A>> commands) {
            super(commands);
        }
    }

    static final class RelatedCommandsImpl<A extends CommandActor>
    extends CommandListImpl<A>
    implements Help.RelatedCommands<A> {
        public RelatedCommandsImpl(@Unmodifiable List<ExecutableCommand<A>> commands) {
            super(commands);
        }
    }

    static abstract class CommandListImpl<A extends CommandActor>
    implements Help.CommandList<A> {
        private final @Unmodifiable List<ExecutableCommand<A>> commands;

        public CommandListImpl(@Unmodifiable List<ExecutableCommand<A>> commands) {
            this.commands = commands;
        }

        @Override
        public @Range(from=1L, to=0x7FFFFFFFL) int numberOfPages(int elementsPerPage) {
            return Help.numberOfPages(this.commands.size(), elementsPerPage);
        }

        @Override
        public @Unmodifiable List<ExecutableCommand<A>> all() {
            return this.commands;
        }

        @Override
        public @Unmodifiable List<ExecutableCommand<A>> paginate(int pageNumber, int elementsPerPage) {
            return Help.paginate(this.commands, pageNumber, elementsPerPage);
        }

        @Override
        @NotNull
        public Iterator<ExecutableCommand<A>> iterator() {
            return this.commands.iterator();
        }

        public String toString() {
            return this.getClass().getSimpleName() + "(commands=" + this.commands + ')';
        }
    }
}

