/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.reflect;

import hu.geri.libs.revxrsal.commands.annotation.Optional;
import hu.geri.libs.revxrsal.commands.annotation.Switch;
import hu.geri.libs.revxrsal.commands.reflect.MethodCaller;
import hu.geri.libs.revxrsal.commands.reflect.MethodCallerFactory;
import hu.geri.libs.revxrsal.commands.reflect.ktx.KotlinConstants;
import hu.geri.libs.revxrsal.commands.reflect.ktx.KotlinFunction;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;

final class KotlinMethodCallerFactory
implements MethodCallerFactory {
    public static final KotlinMethodCallerFactory INSTANCE = new KotlinMethodCallerFactory();

    KotlinMethodCallerFactory() {
    }

    @Override
    @NotNull
    public MethodCaller createFor(@NotNull Method method) {
        KotlinFunction function = KotlinFunction.wrap(method);
        return (instance, arguments) -> {
            ArrayList<Object> list = new ArrayList<Object>();
            Collections.addAll(list, arguments);
            list.replaceAll(v -> v == KotlinConstants.ABSENT_VALUE ? null : v);
            return function.call(instance, list, parameter -> parameter.isAnnotationPresent(Optional.class) || parameter.isAnnotationPresent(Switch.class));
        };
    }
}

