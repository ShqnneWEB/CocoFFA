/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.permissions.Permission
 */
package hu.geri.libs.revxrsal.commands.bukkit.sender;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.bukkit.sender.BukkitCommandPermission;
import hu.geri.libs.revxrsal.commands.command.CommandPermission;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum BukkitPermissionFactory implements CommandPermission.Factory<BukkitCommandActor>
{
    INSTANCE;


    @Override
    @Nullable
    public CommandPermission<BukkitCommandActor> create(@NotNull AnnotationList annotations, @NotNull Lamp<BukkitCommandActor> lamp) {
        hu.geri.libs.revxrsal.commands.bukkit.annotation.CommandPermission permissionAnn = annotations.get(hu.geri.libs.revxrsal.commands.bukkit.annotation.CommandPermission.class);
        if (permissionAnn == null) {
            return null;
        }
        return new BukkitCommandPermission(new Permission(permissionAnn.value(), permissionAnn.defaultAccess()));
    }
}

