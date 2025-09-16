/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.annotation.list;

import hu.geri.libs.revxrsal.commands.annotation.dynamic.AnnotationReplacer;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationListFromMap;
import hu.geri.libs.revxrsal.commands.annotation.list.EmptyAnnotationList;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public interface AnnotationList
extends Iterable<Annotation> {
    @NotNull
    public static AnnotationList create(@NotNull AnnotatedElement element) {
        return AnnotationListFromMap.createFor(element);
    }

    @NotNull
    public static AnnotationList create(@NotNull Collection<Annotation> annotations) {
        return AnnotationListFromMap.createFrom(annotations);
    }

    @NotNull
    public static AnnotationList create(@NotNull Map<Class<? extends Annotation>, Annotation> annotations) {
        if (annotations.isEmpty()) {
            return AnnotationList.empty();
        }
        return new AnnotationListFromMap(annotations);
    }

    @Contract(pure=true)
    @NotNull
    public static AnnotationList empty() {
        return EmptyAnnotationList.INSTANCE;
    }

    @Contract(pure=true)
    @Nullable
    public <T extends Annotation> T get(@NotNull Class<T> var1);

    @Contract(pure=true)
    @Nullable
    public <R, T extends Annotation> R map(@NotNull Class<T> var1, Function<T, @Nullable R> var2);

    @Contract(pure=true)
    public <R, T extends Annotation> R mapOr(@NotNull Class<T> var1, Function<T, R> var2, R var3);

    @Contract(pure=true)
    public <R, T extends Annotation> R mapOrGet(@NotNull Class<T> var1, @NotNull Function<T, R> var2, @NotNull Supplier<R> var3);

    @Contract(pure=true)
    @NotNull
    public <T extends Annotation> T require(@NotNull Class<T> var1, @NotNull String var2);

    @Contract(pure=true)
    public <T extends Annotation> boolean contains(@NotNull Class<T> var1);

    public boolean isEmpty();

    @NotNull
    @Contract(value="-> new", pure=true)
    public Map<Class<?>, Annotation> toMutableMap();

    @NotNull
    @Contract(pure=true)
    public AnnotationList replaceAnnotations(@NotNull AnnotatedElement var1, @NotNull Map<Class<? extends Annotation>, Set<AnnotationReplacer<?>>> var2);

    @Override
    @NotNull
    @Contract(pure=true)
    public @Unmodifiable Iterator<Annotation> iterator();

    @NotNull
    @Contract(pure=true, value="_, _ -> new")
    public AnnotationList withAnnotations(boolean var1, @NotNull Annotation ... var2);

    @NotNull
    @Contract(pure=true, value="_ -> new")
    default public AnnotationList withAnnotations(@NotNull Annotation ... annotations) {
        return this.withAnnotations(true, annotations);
    }

    @Contract(pure=true)
    public boolean any(@NotNull Predicate<Annotation> var1);
}

