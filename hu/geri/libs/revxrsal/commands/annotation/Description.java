/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.annotation;

import hu.geri.libs.revxrsal.commands.annotation.DistributeOnMethods;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@DistributeOnMethods
@Target(value={ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Description {
    public String value();
}

