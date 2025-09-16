/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.reflect.ktx;

import hu.geri.libs.revxrsal.commands.reflect.MethodCaller;
import hu.geri.libs.revxrsal.commands.reflect.ktx.CallableMethod;
import hu.geri.libs.revxrsal.commands.reflect.ktx.KotlinSingletons;
import hu.geri.libs.revxrsal.commands.util.Collections;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DefaultFunctionFinder {
    private DefaultFunctionFinder() {
    }

    @Nullable
    public static CallableMethod findDefaultFunction(@NotNull Method method) {
        if (method.getParameterCount() == 0) {
            throw new IllegalArgumentException("Method has no parameters!");
        }
        List<Class<?>> syntheticParams = DefaultFunctionFinder.getSyntheticParameters(method);
        String name = DefaultFunctionFinder.getDefaultMethodName(method);
        Method defaultMethod = DefaultFunctionFinder.getDeclaredMethodOrNull(name, method.getDeclaringClass(), syntheticParams);
        if (defaultMethod != null) {
            return new CallableMethod(defaultMethod, KotlinSingletons.wrapMethod(defaultMethod));
        }
        syntheticParams.add(0, method.getDeclaringClass());
        defaultMethod = DefaultFunctionFinder.getDeclaredMethodOrNull(name, method.getDeclaringClass(), syntheticParams);
        if (defaultMethod != null) {
            MethodCaller callerForDefault = DefaultFunctionFinder.bindInstanceParameter(defaultMethod);
            return new CallableMethod(defaultMethod, callerForDefault);
        }
        Object companion = KotlinSingletons.findCompanion(method.getDeclaringClass());
        if (companion == null) {
            return null;
        }
        syntheticParams.set(0, companion.getClass());
        defaultMethod = DefaultFunctionFinder.getDeclaredMethodOrNull(name, companion.getClass(), syntheticParams);
        if (defaultMethod == null) {
            return null;
        }
        MethodCaller callerForDefault = DefaultFunctionFinder.bindInstanceParameter(defaultMethod);
        return new CallableMethod(defaultMethod, callerForDefault);
    }

    @NotNull
    private static MethodCaller bindInstanceParameter(Method method) {
        MethodCaller caller = KotlinSingletons.wrapMethod(method);
        return (instance, arguments) -> {
            Object[] boundArgs = Collections.insertAtBeginning(arguments, instance);
            return caller.call(instance, boundArgs);
        };
    }

    @Nullable
    private static Method getDeclaredMethodOrNull(@NotNull String name, @NotNull Class<?> type, @NotNull List<Class<?>> parameterTypes) {
        try {
            return type.getDeclaredMethod(name, parameterTypes.toArray(new Class[0]));
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @NotNull
    private static String getDefaultMethodName(@NotNull Method method) {
        return method.getName() + "$default";
    }

    @NotNull
    private static List<Class<?>> getSyntheticParameters(Method method) {
        ArrayList parameters = new ArrayList(method.getParameterCount() + 3);
        java.util.Collections.addAll(parameters, method.getParameterTypes());
        parameters.add(Integer.TYPE);
        parameters.add(Object.class);
        return parameters;
    }
}

