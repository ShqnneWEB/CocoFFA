/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.annotation.dynamic;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface AnnotationReplacer<T extends Annotation> {
    @Nullable
    public Collection<Annotation> replaceAnnotation(@NotNull AnnotatedElement var1, @NotNull T var2);
}

