/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.reflect.ktx;

import hu.geri.libs.revxrsal.commands.reflect.MethodCaller;
import java.lang.reflect.Method;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class CallableMethod {
    @NotNull
    private final Method method;
    @NotNull
    private final MethodCaller caller;

    public CallableMethod(@NotNull Method method, @NotNull MethodCaller caller) {
        this.method = method;
        this.caller = caller;
    }

    @NotNull
    public Method method() {
        return this.method;
    }

    @NotNull
    public MethodCaller caller() {
        return this.caller;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        CallableMethod that = (CallableMethod)obj;
        return Objects.equals(this.method, that.method) && Objects.equals(this.caller, that.caller);
    }

    public int hashCode() {
        return Objects.hash(this.method, this.caller);
    }

    public String toString() {
        return "CallableMethod[method=" + this.method + ", caller=" + this.caller + ']';
    }
}

