/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ParameterFactory;
import hu.geri.libs.revxrsal.commands.parameter.builtins.ClassContextParameterFactory;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ContextParameter<A extends CommandActor, T> {
    public T resolve(@NotNull CommandParameter var1, @NotNull ExecutionContext<A> var2);

    @FunctionalInterface
    public static interface Factory<A extends CommandActor>
    extends ParameterFactory {
        @NotNull
        public static <A extends CommandActor, T> Factory<A> forType(@NotNull Class<T> type, @NotNull ContextParameter<A, T> parameterType) {
            return new ClassContextParameterFactory<A, T>(type, parameterType, false);
        }

        @NotNull
        public static <A extends CommandActor, T> Factory<A> forTypeAndSubclasses(@NotNull Class<T> type, @NotNull ContextParameter<A, T> parameterType) {
            return new ClassContextParameterFactory<A, T>(type, parameterType, true);
        }

        @Nullable
        public <T> ContextParameter<A, T> create(@NotNull Type var1, @NotNull AnnotationList var2, @NotNull Lamp<A> var3);
    }
}

