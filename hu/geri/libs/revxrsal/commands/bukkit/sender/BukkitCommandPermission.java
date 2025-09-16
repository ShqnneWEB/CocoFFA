/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.permissions.Permission
 */
package hu.geri.libs.revxrsal.commands.bukkit.sender;

import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandPermission;
import java.util.Objects;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

public final class BukkitCommandPermission
implements CommandPermission<BukkitCommandActor> {
    @NotNull
    private final Permission permission;

    public BukkitCommandPermission(@NotNull Permission permission) {
        this.permission = permission;
    }

    @Override
    public boolean isExecutableBy(@NotNull BukkitCommandActor actor) {
        return actor.sender().hasPermission(this.permission);
    }

    @NotNull
    public Permission permission() {
        return this.permission;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        BukkitCommandPermission that = (BukkitCommandPermission)obj;
        return Objects.equals(this.permission, that.permission);
    }

    public int hashCode() {
        return Objects.hash(this.permission);
    }

    public String toString() {
        return "BukkitCommandPermission[permission=" + this.permission.getName() + "]";
    }
}

