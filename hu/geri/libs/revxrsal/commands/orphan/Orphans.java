/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.orphan;

import hu.geri.libs.revxrsal.commands.orphan.OrphanCommand;
import hu.geri.libs.revxrsal.commands.orphan.OrphanRegistry;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class Orphans {
    private final List<String> paths;

    public Orphans(List<String> paths) {
        this.paths = paths;
    }

    public static Orphans path(@NotNull String ... paths) {
        Preconditions.notNull(paths, "paths");
        return new Orphans(Arrays.asList(paths));
    }

    public OrphanRegistry handler(OrphanCommand handler) {
        Preconditions.notNull(handler, "orphan command");
        return new OrphanRegistry(this.paths, handler);
    }

    public List<String> paths() {
        return this.paths;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Orphans that = (Orphans)obj;
        return Objects.equals(this.paths, that.paths);
    }

    public int hashCode() {
        return Objects.hash(this.paths);
    }

    public String toString() {
        return "Orphans[paths=" + this.paths + ']';
    }
}

