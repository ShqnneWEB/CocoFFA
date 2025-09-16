/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.permissions.PermissionDefault
 */
package hu.geri.libs.revxrsal.commands.bukkit.annotation;

import hu.geri.libs.revxrsal.commands.annotation.DistributeOnMethods;
import hu.geri.libs.revxrsal.commands.annotation.NotSender;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.bukkit.permissions.PermissionDefault;

@DistributeOnMethods
@NotSender.ImpliesNotSender
@Target(value={ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface CommandPermission {
    public String value();

    public PermissionDefault defaultAccess() default PermissionDefault.OP;
}

