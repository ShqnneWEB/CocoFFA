/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.annotation.list;

import hu.geri.libs.revxrsal.commands.annotation.dynamic.AnnotationReplacer;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationListFromMap;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

final class EmptyAnnotationList
implements AnnotationList {
    public static final EmptyAnnotationList INSTANCE = new EmptyAnnotationList();

    EmptyAnnotationList() {
    }

    @Override
    @Nullable
    public <T extends Annotation> T get(@NotNull Class<T> type) {
        Classes.checkRetention(type);
        return null;
    }

    @Override
    @Nullable
    public <R, T extends Annotation> R map(@NotNull Class<T> type, Function<T, R> function) {
        Classes.checkRetention(type);
        return null;
    }

    @Override
    public <R, T extends Annotation> R mapOr(@NotNull Class<T> type, Function<T, R> function, R defaultValue) {
        Classes.checkRetention(type);
        return defaultValue;
    }

    @Override
    public <R, T extends Annotation> R mapOrGet(@NotNull Class<T> type, @NotNull Function<T, R> function, @NotNull Supplier<R> defaultValue) {
        Classes.checkRetention(type);
        return defaultValue.get();
    }

    @Override
    @NotNull
    public <T extends Annotation> T require(@NotNull Class<T> type, @NotNull String errorMessage) {
        Classes.checkRetention(type);
        throw new IllegalStateException(errorMessage);
    }

    @Override
    public <T extends Annotation> boolean contains(@NotNull Class<T> type) {
        Classes.checkRetention(type);
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    @NotNull
    public AnnotationList replaceAnnotations(@NotNull AnnotatedElement element, @NotNull Map<Class<? extends Annotation>, Set<AnnotationReplacer<?>>> replacers) {
        return this;
    }

    @Override
    @NotNull
    public @UnmodifiableView Iterator<Annotation> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    @NotNull
    public Map<Class<?>, Annotation> toMutableMap() {
        return new HashMap();
    }

    @Override
    @NotNull
    public AnnotationList withAnnotations(boolean overrideExisting, @NotNull Annotation ... annotations) {
        Map<Class<? extends Annotation>, Annotation> map = AnnotationListFromMap.toMap(annotations);
        return new AnnotationListFromMap(map);
    }

    @Override
    public boolean any(@NotNull Predicate<Annotation> predicate) {
        return false;
    }
}

