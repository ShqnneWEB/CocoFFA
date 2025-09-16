/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.NotNull;

@Target(value={ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Values {
    @NotNull
    public String[] value();

    public boolean caseSensitive() default true;
}

