/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.reflect.ktx;

import hu.geri.libs.revxrsal.commands.reflect.MethodCaller;
import hu.geri.libs.revxrsal.commands.reflect.MethodCallerFactory;
import hu.geri.libs.revxrsal.commands.reflect.ktx.KotlinConstants;
import hu.geri.libs.revxrsal.commands.util.Collections;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class KotlinSingletons {
    private static final String COMPANION_NAME = "Companion";

    private KotlinSingletons() {
    }

    @NotNull
    public static MethodCaller getCallerForNonDefault(@NotNull Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        MethodCaller caller = KotlinSingletons.wrapMethod(method);
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        if (isStatic) {
            return caller;
        }
        if (parameterTypes.length > 0 && parameterTypes[0] == method.getDeclaringClass()) {
            return (instance, arguments) -> {
                Object[] boundArgs = Collections.insertAtBeginning(arguments, instance);
                return caller.call(instance, boundArgs);
            };
        }
        return caller;
    }

    @Nullable
    public static Object findCompanion(Class<?> type) {
        Map<String, Field> fields = Arrays.stream(type.getDeclaredFields()).filter(field -> Modifier.isPublic(field.getModifiers()) && KotlinConstants.isStaticFinal(field.getModifiers())).collect(Collectors.toMap(Field::getName, f -> f));
        try {
            Class<?> companion = Class.forName(type.getName() + "$" + COMPANION_NAME);
            Field companionField = fields.get(companion.getSimpleName());
            if (companionField != null) {
                return KotlinSingletons.fetch(companionField);
            }
        } catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        for (Class<?> declaredClass : type.getDeclaredClasses()) {
            Object singleton;
            String name = declaredClass.getSimpleName();
            Field companionField = fields.get(name);
            if (companionField == null || (singleton = KotlinSingletons.fetch(companionField)) == null) continue;
            return singleton;
        }
        return null;
    }

    private static void makeAccessible(@NotNull AccessibleObject accessibleObject) {
        if (!accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
    }

    private static Object fetch(@NotNull Field field) {
        try {
            KotlinSingletons.makeAccessible(field);
            return field.get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to access the field", e);
        }
    }

    static MethodCaller wrapMethod(@NotNull Method method) {
        Preconditions.notNull(method, "method");
        return MethodCallerFactory.methodHandles().createFor(method);
    }
}

