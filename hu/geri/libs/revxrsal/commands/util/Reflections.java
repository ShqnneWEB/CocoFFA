/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.util;

import hu.geri.libs.revxrsal.commands.annotation.CommandPriority;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Reflections {
    private Reflections() {
        Preconditions.cannotInstantiate(Reflections.class);
    }

    public static List<Method> getAllMethods(Class<?> c) {
        return Reflections.getAllMethods(c, false);
    }

    public static List<Method> getAllMethods(Class<?> c, boolean sort) {
        ArrayList<Method> methods = new ArrayList<Method>();
        for (Class<?> current = c; current != null && current != Object.class; current = current.getSuperclass()) {
            Collections.addAll(methods, current.getDeclaredMethods());
        }
        if (sort) {
            methods.sort((o1, o2) -> {
                CommandPriority a1 = o1.getAnnotation(CommandPriority.class);
                CommandPriority a2 = o2.getAnnotation(CommandPriority.class);
                if (a1 != null && a2 != null) {
                    return Integer.compare(a1.value(), a2.value());
                }
                CommandPriority.Low l1 = o1.getAnnotation(CommandPriority.Low.class);
                CommandPriority.Low l2 = o2.getAnnotation(CommandPriority.Low.class);
                if (l1 != null) {
                    return 1;
                }
                if (l2 != null) {
                    return -1;
                }
                return 0;
            });
        }
        return methods;
    }

    public static List<Class<?>> getTopClasses(Class<?> c) {
        ArrayList classes = new ArrayList();
        classes.add(c);
        Class<?> enclosingClass = c.getEnclosingClass();
        while (enclosingClass != null) {
            c = enclosingClass;
            classes.add(c);
            enclosingClass = c.getEnclosingClass();
        }
        Collections.reverse(classes);
        return classes;
    }
}

