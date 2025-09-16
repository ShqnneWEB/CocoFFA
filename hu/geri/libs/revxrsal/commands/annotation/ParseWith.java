/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.annotation;

import hu.geri.libs.revxrsal.commands.annotation.NotSender;
import hu.geri.libs.revxrsal.commands.parameter.BaseParameterType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
@NotSender.ImpliesNotSender
public @interface ParseWith {
    public Class<? extends BaseParameterType> value();
}

