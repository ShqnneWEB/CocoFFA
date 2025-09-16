/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.util;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.reflect.MethodCaller;
import hu.geri.libs.revxrsal.commands.util.Collections;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class StackTraceSanitizer {
    private static final StackTraceSanitizer DEFAULT_SANITIZER = StackTraceSanitizer.builder().ignoreClasses(Lamp.class).ignoreClasses(MethodHandles.class, MethodHandle.class).ignorePackage(MethodCaller.class.getPackage()).build();
    private static final StackTraceSanitizer EMPTY = new StackTraceSanitizer(java.util.Collections.emptyList());
    private final @Unmodifiable List<Predicate<StackTraceElement>> filters;

    private StackTraceSanitizer(@Unmodifiable List<Predicate<StackTraceElement>> filters) {
        this.filters = filters;
    }

    @Contract(pure=true)
    @NotNull
    public static StackTraceSanitizer defaultSanitizer() {
        return DEFAULT_SANITIZER;
    }

    @Contract(pure=true)
    @NotNull
    public static StackTraceSanitizer none() {
        return EMPTY;
    }

    @Contract(value="-> new")
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    public void sanitize(@NotNull Throwable throwable) {
        if (this.filters.isEmpty()) {
            return;
        }
        if (throwable.getCause() != null) {
            this.sanitize(throwable.getCause());
        }
        ArrayList trace = new ArrayList();
        java.util.Collections.addAll(trace, throwable.getStackTrace());
        int stripIndex = trace.size();
        for (int i = 0; i < trace.size(); ++i) {
            StackTraceElement stackTraceElement = (StackTraceElement)trace.get(i);
            if (!this.filters.stream().anyMatch(f -> f.test(stackTraceElement))) continue;
            stripIndex = i;
            break;
        }
        trace.subList(stripIndex, trace.size()).clear();
        throwable.setStackTrace(trace.toArray(new StackTraceElement[0]));
    }

    public static class Builder {
        private final List<Predicate<StackTraceElement>> filters = new ArrayList<Predicate<StackTraceElement>>();

        public Builder ignoreClasses(@NotNull Class<?> ... classes) {
            for (Class<?> clazz : classes) {
                this.filters.add(c -> c.getClassName().equals(clazz.getName()));
            }
            return this;
        }

        public Builder ignorePackage(@NotNull String packageName) {
            this.filters.add(c -> c.getClassName().startsWith(packageName));
            return this;
        }

        public Builder ignorePackage(@NotNull Package pkg) {
            this.filters.add(c -> c.getClassName().startsWith(pkg.getName()));
            return this;
        }

        public Builder ignoreMethod(@NotNull String methodName) {
            this.filters.add(c -> c.getMethodName().equals(methodName));
            return this;
        }

        public Builder ignoreNativeMethods() {
            this.filters.add(StackTraceElement::isNativeMethod);
            return this;
        }

        public StackTraceSanitizer build() {
            return new StackTraceSanitizer(Collections.copyList(this.filters));
        }
    }
}

