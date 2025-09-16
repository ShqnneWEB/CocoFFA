/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.hook;

public interface CancelHandle {
    public boolean wasCancelled();

    public void cancel();
}

