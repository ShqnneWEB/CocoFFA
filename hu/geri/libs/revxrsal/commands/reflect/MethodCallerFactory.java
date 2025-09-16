/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.reflect;

import hu.geri.libs.revxrsal.commands.reflect.DefaultMethodCallerFactory;
import hu.geri.libs.revxrsal.commands.reflect.KotlinMethodCallerFactory;
import hu.geri.libs.revxrsal.commands.reflect.MethodCaller;
import hu.geri.libs.revxrsal.commands.reflect.MethodHandlesCallerFactory;
import java.lang.reflect.Method;
import org.jetbrains.annotations.NotNull;

public interface MethodCallerFactory {
    @NotNull
    public static MethodCallerFactory methodHandles() {
        return MethodHandlesCallerFactory.INSTANCE;
    }

    @NotNull
    public static MethodCallerFactory kotlinFunctions() {
        return KotlinMethodCallerFactory.INSTANCE;
    }

    @NotNull
    public static MethodCallerFactory defaultFactory() {
        return DefaultMethodCallerFactory.INSTANCE;
    }

    @NotNull
    public MethodCaller createFor(@NotNull Method var1) throws Throwable;
}

