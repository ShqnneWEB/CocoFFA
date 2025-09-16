/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.BaseParameterType;
import hu.geri.libs.revxrsal.commands.parameter.ParameterFactory;
import hu.geri.libs.revxrsal.commands.parameter.PrioritySpec;
import hu.geri.libs.revxrsal.commands.parameter.builtins.ClassParameterTypeFactory;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ParameterType<A extends CommandActor, T>
extends BaseParameterType {
    public T parse(@NotNull MutableStringStream var1, @NotNull @NotNull ExecutionContext<@NotNull A> var2);

    @NotNull
    default public @NotNull SuggestionProvider<@NotNull A> defaultSuggestions() {
        return SuggestionProvider.empty();
    }

    @NotNull
    default public PrioritySpec parsePriority() {
        return PrioritySpec.defaultPriority();
    }

    default public boolean isGreedy() {
        return false;
    }

    public static interface Factory<A extends CommandActor>
    extends ParameterFactory,
    BaseParameterType {
        @NotNull
        public static <A extends CommandActor, T> Factory<A> forType(@NotNull Class<T> type, @NotNull ParameterType<A, T> parameterType) {
            return new ClassParameterTypeFactory<A, T>(type, parameterType, false);
        }

        @NotNull
        public static <A extends CommandActor, T> Factory<A> forTypeAndSubclasses(@NotNull Class<T> type, @NotNull ParameterType<A, T> parameterType) {
            return new ClassParameterTypeFactory<A, T>(type, parameterType, true);
        }

        @Nullable
        public <T> ParameterType<A, T> create(@NotNull Type var1, @NotNull AnnotationList var2, @NotNull Lamp<A> var3);
    }
}

