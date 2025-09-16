/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.reflect;

import hu.geri.libs.revxrsal.commands.reflect.MethodCaller;
import hu.geri.libs.revxrsal.commands.reflect.MethodCallerFactory;
import hu.geri.libs.revxrsal.commands.reflect.ktx.KotlinConstants;
import java.lang.reflect.Method;
import org.jetbrains.annotations.NotNull;

final class DefaultMethodCallerFactory
implements MethodCallerFactory {
    public static final DefaultMethodCallerFactory INSTANCE = new DefaultMethodCallerFactory();

    DefaultMethodCallerFactory() {
    }

    @Override
    @NotNull
    public MethodCaller createFor(@NotNull Method method) throws Throwable {
        if (KotlinConstants.isKotlinClass(method.getDeclaringClass())) {
            return MethodCallerFactory.kotlinFunctions().createFor(method);
        }
        return MethodCallerFactory.methodHandles().createFor(method);
    }
}

