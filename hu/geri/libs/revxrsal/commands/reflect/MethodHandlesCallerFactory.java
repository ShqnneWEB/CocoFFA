/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.reflect;

import hu.geri.libs.revxrsal.commands.reflect.MethodCaller;
import hu.geri.libs.revxrsal.commands.reflect.MethodCallerFactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class MethodHandlesCallerFactory
implements MethodCallerFactory {
    public static final MethodHandlesCallerFactory INSTANCE = new MethodHandlesCallerFactory();

    MethodHandlesCallerFactory() {
    }

    @Override
    @NotNull
    public MethodCaller createFor(@NotNull Method method) throws Throwable {
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        final MethodHandle handle = MethodHandles.lookup().unreflect(method);
        final String methodString = method.toString();
        final boolean isStatic = Modifier.isStatic(method.getModifiers());
        return new MethodCaller(){

            @Override
            public Object call(@Nullable Object instance, Object ... arguments) {
                if (!isStatic) {
                    ArrayList<Object> args = new ArrayList<Object>();
                    args.add(instance);
                    Collections.addAll(args, arguments);
                    return handle.invokeWithArguments(args);
                }
                return handle.invokeWithArguments(arguments);
            }

            public String toString() {
                return "MethodHandlesCaller(" + methodString + ")";
            }
        };
    }

    public String toString() {
        return "MethodHandlesCallerFactory";
    }
}

