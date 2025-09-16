/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.annotation.list;

import hu.geri.libs.revxrsal.commands.annotation.DistributeOnMethods;
import hu.geri.libs.revxrsal.commands.annotation.dynamic.AnnotationReplacer;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

final class AnnotationListFromMap
implements AnnotationList {
    private final Map<Class<? extends Annotation>, Annotation> annotations;

    public AnnotationListFromMap(Map<Class<? extends Annotation>, Annotation> annotations) {
        this.annotations = annotations;
    }

    @NotNull
    public static AnnotationList createFrom(@NotNull Collection<Annotation> annotations) {
        if (annotations.isEmpty()) {
            return AnnotationList.empty();
        }
        return new AnnotationListFromMap(AnnotationListFromMap.toMap(annotations));
    }

    @NotNull
    public static AnnotationList createFor(@NotNull AnnotatedElement element) {
        Map<Class<? extends Annotation>, Annotation> annotations = AnnotationListFromMap.toMap(element.getAnnotations());
        if (annotations.isEmpty()) {
            return AnnotationList.empty();
        }
        return new AnnotationListFromMap(annotations);
    }

    @NotNull
    public static Map<Class<? extends Annotation>, Annotation> toMap(@NotNull Iterable<Annotation> annotations) {
        LinkedHashMap<Class<? extends Annotation>, Annotation> map = new LinkedHashMap<Class<? extends Annotation>, Annotation>();
        for (Annotation annotation : annotations) {
            map.put(annotation.annotationType(), annotation);
        }
        return map;
    }

    @NotNull
    public static Map<Class<? extends Annotation>, Annotation> toMap(@NotNull Annotation[] annotations) {
        LinkedHashMap<Class<? extends Annotation>, Annotation> map = new LinkedHashMap<Class<? extends Annotation>, Annotation>();
        for (Annotation annotation : annotations) {
            map.put(annotation.annotationType(), annotation);
        }
        return map;
    }

    private static void distributeAnnotations(@NotNull Map<Class<? extends Annotation>, Annotation> annotations, @NotNull Method element, @NotNull Map<Class<? extends Annotation>, Set<AnnotationReplacer<?>>> replacers) {
        for (Class<?> top = element.getDeclaringClass(); top != null; top = top.getDeclaringClass()) {
            AnnotationList classAnnotations = AnnotationList.create(top).replaceAnnotations(top, replacers);
            for (Annotation annotation : classAnnotations) {
                if (!annotation.annotationType().isAnnotationPresent(DistributeOnMethods.class)) continue;
                annotations.putIfAbsent(annotation.annotationType(), annotation);
            }
        }
    }

    @Override
    @Nullable
    public <T extends Annotation> T get(@NotNull Class<T> type) {
        Classes.checkRetention(type);
        return (T)this.annotations.get(type);
    }

    @Override
    @Nullable
    public <R, T extends Annotation> R map(@NotNull Class<T> type, Function<T, R> function) {
        T annotation = this.get(type);
        if (annotation != null) {
            return function.apply(annotation);
        }
        return null;
    }

    @Override
    public <R, T extends Annotation> R mapOr(@NotNull Class<T> type, Function<T, R> function, R defaultValue) {
        T annotation = this.get(type);
        if (annotation != null) {
            return function.apply(annotation);
        }
        return defaultValue;
    }

    @Override
    public <R, T extends Annotation> R mapOrGet(@NotNull Class<T> type, @NotNull Function<T, R> function, @NotNull Supplier<R> defaultValue) {
        T annotation = this.get(type);
        if (annotation != null) {
            return function.apply(annotation);
        }
        return defaultValue.get();
    }

    @Override
    @NotNull
    public <T extends Annotation> T require(@NotNull Class<T> type, @NotNull String errorMessage) {
        T annotation = this.get(type);
        if (annotation == null) {
            throw new IllegalStateException(errorMessage);
        }
        return annotation;
    }

    @Override
    public <T extends Annotation> boolean contains(@NotNull Class<T> type) {
        Classes.checkRetention(type);
        return this.annotations.containsKey(type);
    }

    @Override
    @NotNull
    public AnnotationList replaceAnnotations(@NotNull AnnotatedElement element, @NotNull Map<Class<? extends Annotation>, Set<AnnotationReplacer<?>>> replacers) {
        LinkedHashMap<Class<? extends Annotation>, Annotation> annotations = new LinkedHashMap<Class<? extends Annotation>, Annotation>(this.annotations);
        for (Annotation annotation : this.annotations.values()) {
            for (AnnotationReplacer replacer : replacers.getOrDefault(annotation.annotationType(), Collections.emptySet())) {
                Collection<Annotation> newAnnotations = replacer.replaceAnnotation(element, annotation);
                if (newAnnotations == null) continue;
                annotations.putAll(AnnotationListFromMap.toMap(newAnnotations));
            }
        }
        if (element instanceof Method) {
            Method method = (Method)element;
            AnnotationListFromMap.distributeAnnotations(annotations, method, replacers);
        }
        return new AnnotationListFromMap(annotations);
    }

    @Override
    @NotNull
    public Map<Class<?>, Annotation> toMutableMap() {
        return new LinkedHashMap(this.annotations);
    }

    @Override
    public boolean isEmpty() {
        return this.annotations.isEmpty();
    }

    @Override
    @NotNull
    public @UnmodifiableView Iterator<Annotation> iterator() {
        return hu.geri.libs.revxrsal.commands.util.Collections.unmodifiableIterator(this.annotations.values().iterator());
    }

    @Override
    public boolean any(@NotNull Predicate<Annotation> predicate) {
        for (Annotation value : this.annotations.values()) {
            if (!predicate.test(value)) continue;
            return true;
        }
        return false;
    }

    @Override
    @NotNull
    public AnnotationList withAnnotations(boolean overrideExisting, @NotNull Annotation ... annotations) {
        LinkedHashMap<Class<? extends Annotation>, Annotation> map = new LinkedHashMap<Class<? extends Annotation>, Annotation>(this.annotations);
        for (Annotation annotation : annotations) {
            if (overrideExisting) {
                map.put(annotation.annotationType(), annotation);
                continue;
            }
            map.putIfAbsent(annotation.annotationType(), annotation);
        }
        return new AnnotationListFromMap(map);
    }
}

