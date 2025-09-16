/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.annotation;

import hu.geri.libs.revxrsal.commands.annotation.NotSender;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.Range;

@Target(value={ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
@NotSender.ImpliesNotSender
public @interface Sized {
    public @Range(from=0L, to=0x7FFFFFFFL) int min() default 0;

    public @Range(from=0L, to=0x7FFFFFFFL) int max() default 0x7FFFFFFF;
}

