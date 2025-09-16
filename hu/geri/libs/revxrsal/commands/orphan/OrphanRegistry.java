/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.orphan;

import hu.geri.libs.revxrsal.commands.orphan.OrphanCommand;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class OrphanRegistry {
    @NotNull
    private final @Unmodifiable List<String> paths;
    @NotNull
    private final OrphanCommand handler;

    public OrphanRegistry(@NotNull @Unmodifiable List<String> paths, @NotNull OrphanCommand handler) {
        this.paths = paths;
        this.handler = handler;
    }

    @NotNull
    public @Unmodifiable List<String> paths() {
        return this.paths;
    }

    @NotNull
    public OrphanCommand handler() {
        return this.handler;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        OrphanRegistry that = (OrphanRegistry)obj;
        return Objects.equals(this.paths, that.paths) && Objects.equals(this.handler, that.handler);
    }

    public int hashCode() {
        return Objects.hash(this.paths, this.handler);
    }

    public String toString() {
        return "OrphanRegistry[paths=" + this.paths + ", handler=" + this.handler + ']';
    }
}

