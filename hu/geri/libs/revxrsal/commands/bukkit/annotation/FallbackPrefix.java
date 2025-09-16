/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.bukkit.annotation;

import hu.geri.libs.revxrsal.commands.annotation.DistributeOnMethods;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.NotNull;

@DistributeOnMethods
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.TYPE})
public @interface FallbackPrefix {
    @NotNull
    public String value();
}

