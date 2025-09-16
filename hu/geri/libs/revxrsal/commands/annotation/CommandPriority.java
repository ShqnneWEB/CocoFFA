/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.annotation;

import hu.geri.libs.revxrsal.commands.annotation.dynamic.Annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Annotations.CannotBeCreated
@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface CommandPriority {
    public int value();

    @Annotations.CannotBeCreated
    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Low {
    }
}

