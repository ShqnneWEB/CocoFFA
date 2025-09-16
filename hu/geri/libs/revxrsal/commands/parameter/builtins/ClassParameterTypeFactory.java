/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter.builtins;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.lang.reflect.Type;
import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class ClassParameterTypeFactory<A extends CommandActor, T>
implements ParameterType.Factory<A> {
    private final Class<?> type;
    private final ParameterType<A, T> parameterType;
    private final boolean allowSubclasses;

    public ClassParameterTypeFactory(Class<?> type, ParameterType<A, T> parameterType, boolean allowSubclasses) {
        this.type = Classes.wrap(type);
        this.parameterType = parameterType;
        this.allowSubclasses = allowSubclasses;
    }

    @Override
    public <L> ParameterType<A, L> create(@NotNull Type parameterType, @NotNull AnnotationList annotations, @NotNull Lamp<A> lamp) {
        Class<?> pType = Classes.wrap(Classes.getRawType(parameterType));
        if (this.allowSubclasses && this.type.isAssignableFrom(pType)) {
            return this.parameterType;
        }
        if (this.type == pType) {
            return this.parameterType;
        }
        return null;
    }

    public Class<?> type() {
        return this.type;
    }

    public ParameterType<A, T> parameterType() {
        return this.parameterType;
    }

    public boolean allowSubclasses() {
        return this.allowSubclasses;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        ClassParameterTypeFactory that = (ClassParameterTypeFactory)obj;
        return Objects.equals(this.type, that.type) && Objects.equals(this.parameterType, that.parameterType) && this.allowSubclasses == that.allowSubclasses;
    }

    public int hashCode() {
        return Objects.hash(this.type, this.parameterType, this.allowSubclasses);
    }

    public String toString() {
        return "ClassParameterTypeFactory[type=" + this.type + ", parameterType=" + this.parameterType + ", allowSubclasses=" + this.allowSubclasses + ']';
    }
}

