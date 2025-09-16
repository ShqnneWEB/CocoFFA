/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node.parser;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.Command;
import hu.geri.libs.revxrsal.commands.annotation.CommandPlaceholder;
import hu.geri.libs.revxrsal.commands.annotation.Dependency;
import hu.geri.libs.revxrsal.commands.annotation.Subcommand;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandFunction;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.command.Potential;
import hu.geri.libs.revxrsal.commands.exception.UnknownCommandException;
import hu.geri.libs.revxrsal.commands.exception.context.ErrorContext;
import hu.geri.libs.revxrsal.commands.node.CommandRegistry;
import hu.geri.libs.revxrsal.commands.node.parser.CommandFunctionImpl;
import hu.geri.libs.revxrsal.commands.node.parser.TreeParser;
import hu.geri.libs.revxrsal.commands.reflect.MethodCaller;
import hu.geri.libs.revxrsal.commands.reflect.MethodCallerFactory;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import hu.geri.libs.revxrsal.commands.util.CommandPaths;
import hu.geri.libs.revxrsal.commands.util.Reflections;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

@ApiStatus.Internal
public final class BaseCommandRegistry<A extends CommandActor>
implements CommandRegistry<A> {
    private final List<ExecutableCommand<A>> children;
    private final List<ExecutableCommand<A>> unmodifiableChildren;
    private final Lamp<A> lamp;

    public BaseCommandRegistry(Lamp<A> lamp, List<ExecutableCommand<A>> children) {
        this.children = children;
        this.lamp = lamp;
        this.unmodifiableChildren = Collections.unmodifiableList(children);
    }

    public BaseCommandRegistry(Lamp<A> lamp) {
        this(lamp, new ArrayList<ExecutableCommand<A>>());
    }

    public List<ExecutableCommand<A>> register(@NotNull Class<?> containerClass, Object instance) {
        return this.register(containerClass, instance, null);
    }

    @NotNull
    public @Unmodifiable List<ExecutableCommand<A>> register(@NotNull Class<?> containerClass, Object instance, @Nullable List<String> orphanPaths) {
        this.injectDependencies(containerClass, instance);
        ArrayList<ExecutableCommand<A>> registered = new ArrayList<ExecutableCommand<A>>();
        for (Method method : Reflections.getAllMethods(containerClass, true)) {
            AnnotationList annotations = AnnotationList.create(method).replaceAnnotations(method, this.lamp.annotationReplacers());
            if (annotations.isEmpty() || !this.isCommandMethod(annotations)) continue;
            if (orphanPaths != null && !annotations.isEmpty()) {
                if (orphanPaths.isEmpty()) {
                    throw new IllegalArgumentException("Cannot have an OrphanCommand with no paths (supplied from .path())");
                }
                String[] values = orphanPaths.toArray(new String[0]);
                annotations = annotations.withAnnotations(false, new DynamicCommand(values));
            }
            MethodCaller.BoundMethodCaller caller = MethodCallerFactory.defaultFactory().createFor(method).bindTo(instance);
            CommandFunction fn = CommandFunctionImpl.create(method, annotations, this.lamp, caller);
            for (String path : CommandPaths.parseCommandAnnotations(containerClass, fn)) {
                MutableStringStream stream = StringStream.createMutable(path);
                ExecutableCommand<A> target = TreeParser.parse(fn, this.lamp, stream);
                if (!this.lamp.hooks().onCommandRegistered(target)) continue;
                this.add(target);
                registered.add(target);
            }
        }
        return hu.geri.libs.revxrsal.commands.util.Collections.copyList(registered);
    }

    private boolean isCommandMethod(AnnotationList annotations) {
        return annotations.contains(Command.class) || annotations.contains(Subcommand.class) || annotations.contains(CommandPlaceholder.class);
    }

    private void injectDependencies(Class<?> commandClass, Object instance) {
        for (Field field : commandClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Dependency.class)) continue;
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            Object dependency = this.lamp.dependency(field.getType());
            try {
                field.set(instance, dependency);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Unable to inject dependency value into field " + field.getName(), e);
            }
        }
    }

    private void add(@NotNull ExecutableCommand<A> command) {
        this.children.add(command);
        Collections.sort(this.children);
    }

    @Override
    public void execute(@NotNull A actor, @NotNull ExecutableCommand<A> command, @NotNull MutableStringStream input) {
        Potential<A> potential = command.test(actor, input);
        if (potential.failed()) {
            potential.handleException();
        } else {
            potential.execute();
        }
    }

    @Override
    @NotNull
    public Lamp<A> lamp() {
        return this.lamp;
    }

    @Override
    public void execute(@NotNull A actor, @NotNull StringStream input) {
        LinkedList<Potential<A>> conflicts = new LinkedList<Potential<A>>();
        LinkedList<Potential<A>> failed = new LinkedList<Potential<A>>();
        String firstWord = input.peekUnquotedString();
        for (ExecutableCommand<A> execution : this.children) {
            if (!execution.firstNode().name().equalsIgnoreCase(firstWord)) continue;
            MutableStringStream in = input.toMutableCopy();
            Potential<A> potential = execution.test(actor, in);
            if (conflicts.size() >= this.lamp.dispatcherSettings().maximumFailedAttempts()) break;
            if (potential.successful()) {
                conflicts.add(potential);
                continue;
            }
            failed.add(potential);
        }
        if (conflicts.isEmpty()) {
            if (failed.isEmpty()) {
                this.lamp.handleException(new UnknownCommandException(firstWord), ErrorContext.unknownCommand(actor));
                return;
            }
            this.lamp.dispatcherSettings().failureHandler().handleFailedAttempts(actor, Collections.unmodifiableList(failed), input);
            return;
        }
        Collections.sort(conflicts);
        ((Potential)conflicts.getFirst()).execute();
    }

    @Override
    @NotNull
    public @UnmodifiableView List<ExecutableCommand<A>> commands() {
        return this.unmodifiableChildren;
    }

    @Override
    public void unregister(@NotNull ExecutableCommand<A> execution) {
        this.children.remove(execution);
    }

    @Override
    public boolean any(@NotNull @NotNull Predicate<@NotNull ExecutableCommand<A>> matches) {
        return hu.geri.libs.revxrsal.commands.util.Collections.any(this.children, matches);
    }

    @Override
    @NotNull
    public List<ExecutableCommand<A>> filter(@NotNull @NotNull Predicate<@NotNull ExecutableCommand<A>> filterPredicate) {
        return hu.geri.libs.revxrsal.commands.util.Collections.filter(this.children, filterPredicate);
    }

    @Override
    public void unregisterIf(@NotNull Predicate<ExecutableCommand<A>> matches) {
        this.children.removeIf(matches);
    }

    @Override
    @NotNull
    public Iterator<ExecutableCommand<A>> iterator() {
        return hu.geri.libs.revxrsal.commands.util.Collections.unmodifiableIterator(this.children.iterator());
    }

    private static final class DynamicCommand
    implements Command {
        private final String[] value;

        private DynamicCommand(String[] value) {
            this.value = value;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Command.class;
        }

        @Override
        public String[] value() {
            return this.value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            DynamicCommand that = (DynamicCommand)obj;
            return Objects.equals(this.value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(new Object[]{this.value});
        }

        @Override
        public String toString() {
            return "DynamicCommand[value=" + Arrays.toString(this.value) + ']';
        }
    }
}

