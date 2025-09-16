/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.annotation.dynamic;

import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public final class Annotations {
    @NotNull
    public static <T extends Annotation> T create(@NotNull Class<T> type) {
        return Annotations.create(type, Collections.emptyMap());
    }

    @NotNull
    public static <T extends Annotation> T create(@NotNull Class<T> type, @NotNull Map<String, Object> members) {
        Preconditions.notNull(type, "type");
        Preconditions.notNull(members, "members");
        return (T)((Annotation)type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new DynamicAnnotationHandler(type, members))));
    }

    @NotNull
    public static <T extends Annotation> T create(@NotNull Class<T> type, @NotNull Object ... members) {
        Preconditions.notNull(type, "type");
        Preconditions.notNull(members, "members");
        if (members.length % 2 != 0) {
            throw new IllegalArgumentException("Cannot have a non-even amount of members! Found " + members.length);
        }
        HashMap<String, Object> values = new HashMap<String, Object>();
        for (int i = 0; i < members.length; i += 2) {
            String key = String.valueOf(members[i]);
            Object value = members[i + 1];
            values.put(key, value);
        }
        return (T)((Annotation)type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new DynamicAnnotationHandler(type, values))));
    }

    private static int hashCode(Class<? extends Annotation> type, Map<String, Object> members) {
        int result = 0;
        for (Method method : type.getDeclaredMethods()) {
            String name = method.getName();
            Object value = members.get(name);
            result += 127 * name.hashCode() ^ Arrays.deepHashCode(new Object[]{value}) - 31;
        }
        return result;
    }

    private static boolean equals(Class<? extends Annotation> type, Map<String, Object> members, Object other) throws Exception {
        if (!type.isInstance(other)) {
            return false;
        }
        for (Method method : type.getDeclaredMethods()) {
            String name = method.getName();
            if (Arrays.deepEquals(new Object[]{method.invoke(other, new Object[0])}, new Object[]{members.get(name)})) continue;
            return false;
        }
        return true;
    }

    private static String toString(Class<? extends Annotation> type, Map<String, Object> members) {
        StringBuilder sb = new StringBuilder().append("@").append(type.getName()).append("(");
        StringJoiner joiner = new StringJoiner(", ");
        for (Map.Entry<String, Object> entry : members.entrySet()) {
            joiner.add(entry.getKey() + "=" + Annotations.deepToString(entry.getValue()));
        }
        sb.append(joiner);
        return sb.append(")").toString();
    }

    private static String deepToString(Object arg) {
        String s = Arrays.deepToString(new Object[]{arg});
        return s.substring(1, s.length() - 1);
    }

    private static final class DynamicAnnotationHandler
    implements InvocationHandler {
        private final Class<? extends Annotation> annotationType;
        private final Map<String, Object> annotationMembers;

        private DynamicAnnotationHandler(Class<? extends Annotation> annotationType, Map<String, Object> annotationMembers) {
            if (annotationType.isAnnotationPresent(CannotBeCreated.class)) {
                throw new IllegalArgumentException("Annotation @" + annotationType.getSimpleName() + " cannot be constructed using Annotations.create().");
            }
            this.annotationType = annotationType;
            this.annotationMembers = new HashMap<String, Object>(annotationMembers);
            for (Method method : annotationType.getDeclaredMethods()) {
                this.annotationMembers.putIfAbsent(method.getName(), method.getDefaultValue());
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Class<?> componentType;
            Object o;
            switch (method.getName()) {
                case "toString": {
                    return Annotations.toString(this.annotationType, this.annotationMembers);
                }
                case "hashCode": {
                    return Annotations.hashCode(this.annotationType, this.annotationMembers);
                }
                case "equals": {
                    return Annotations.equals(this.annotationType, this.annotationMembers, args[0]);
                }
                case "annotationType": {
                    return this.annotationType;
                }
            }
            Object v = this.annotationMembers.get(method.getName());
            if (v == null) {
                throw new AbstractMethodError(method.getName());
            }
            Object object = o = v instanceof Supplier ? ((Supplier)v).get() : v;
            if (o == null) {
                throw new IllegalArgumentException("Received null for " + method.getName() + "() in Annotations.create()!");
            }
            if (method.getReturnType().isInstance(v)) {
                return o;
            }
            if (method.getReturnType().isArray() && (componentType = method.getReturnType().getComponentType()).isInstance(o)) {
                Object array = Array.newInstance(componentType, 1);
                Array.set(array, 0, o);
                return array;
            }
            throw new IllegalArgumentException("Invalid value from Annotations.create(): Expected " + method.getReturnType().getSimpleName() + ", found '" + v + "' of type " + v.getClass().getSimpleName());
        }

        public Class<? extends Annotation> annotationType() {
            return this.annotationType;
        }

        public Map<String, Object> annotationMembers() {
            return this.annotationMembers;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            DynamicAnnotationHandler that = (DynamicAnnotationHandler)obj;
            return Objects.equals(this.annotationType, that.annotationType) && Objects.equals(this.annotationMembers, that.annotationMembers);
        }

        public int hashCode() {
            return Objects.hash(this.annotationType, this.annotationMembers);
        }

        public String toString() {
            return "DynamicAnnotationHandler[annotationType=" + this.annotationType + ", annotationMembers=" + this.annotationMembers + ']';
        }
    }

    @Target(value={ElementType.ANNOTATION_TYPE})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface CannotBeCreated {
    }
}

