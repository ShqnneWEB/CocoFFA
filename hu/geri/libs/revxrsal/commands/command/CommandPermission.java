/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.command;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.lang.annotation.Annotation;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface CommandPermission<A extends CommandActor>
extends Predicate<A> {
    public static <A extends CommandActor> CommandPermission<A> alwaysTrue() {
        return actor -> true;
    }

    public boolean isExecutableBy(@NotNull A var1);

    @Override
    default public boolean test(A a) {
        return this.isExecutableBy(a);
    }

    @FunctionalInterface
    public static interface Factory<A extends CommandActor> {
        public static <A extends CommandActor, T extends Annotation> Factory<A> forAnnotation(@NotNull Class<T> annotationType, @NotNull Function<T, @Nullable CommandPermission<A>> permissionCreator) {
            Classes.checkRetention(annotationType);
            return (annotations, lamp) -> {
                Object annotation = annotations.get(annotationType);
                if (annotation != null) {
                    return (CommandPermission)permissionCreator.apply(annotation);
                }
                return null;
            };
        }

        @Nullable
        public CommandPermission<A> create(@NotNull AnnotationList var1, @NotNull Lamp<A> var2);
    }
}

