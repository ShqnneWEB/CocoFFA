/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node.parser;

import hu.geri.libs.revxrsal.commands.annotation.Default;
import hu.geri.libs.revxrsal.commands.annotation.Length;
import hu.geri.libs.revxrsal.commands.annotation.Sized;
import hu.geri.libs.revxrsal.commands.annotation.Switch;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

final class FunctionParameter
implements CommandParameter {
    @NotNull
    private final Parameter parameter;
    @NotNull
    private final String name;
    @NotNull
    private final AnnotationList annotations;
    private final int methodIndex;

    FunctionParameter(@NotNull Parameter parameter, @NotNull String name, @NotNull AnnotationList annotations, int methodIndex) {
        this.parameter = parameter;
        this.name = name;
        this.annotations = annotations;
        this.methodIndex = methodIndex;
    }

    @Override
    public boolean isLastInMethod() {
        return this.method().getParameterCount() == this.methodIndex + 1;
    }

    @Override
    @NotNull
    public Method method() {
        return (Method)this.parameter.getDeclaringExecutable();
    }

    @Override
    @NotNull
    public Class<?> type() {
        return this.parameter.getType();
    }

    @Override
    @NotNull
    public Type fullType() {
        return this.parameter.getParameterizedType();
    }

    @Override
    @NotNull
    public List<Type> generics() {
        Type type = this.parameter.getParameterizedType();
        if (type instanceof ParameterizedType) {
            return Arrays.asList(((ParameterizedType)type).getActualTypeArguments());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isOptional() {
        if (this.type() == Optional.class) {
            return true;
        }
        if (this.annotations.contains(hu.geri.libs.revxrsal.commands.annotation.Optional.class) || this.annotations.contains(Default.class) || this.annotations.contains(Switch.class)) {
            return true;
        }
        Sized sized = this.annotations.get(Sized.class);
        if (sized != null) {
            return sized.min() == 0;
        }
        Length length = this.annotations.get(Length.class);
        if (length != null) {
            return length.min() == 0;
        }
        return false;
    }

    @Override
    @NotNull
    public Parameter parameter() {
        return this.parameter;
    }

    @Override
    @NotNull
    public String name() {
        return this.name;
    }

    @Override
    @NotNull
    public AnnotationList annotations() {
        return this.annotations;
    }

    @Override
    public int methodIndex() {
        return this.methodIndex;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        FunctionParameter that = (FunctionParameter)obj;
        return Objects.equals(this.parameter, that.parameter) && Objects.equals(this.name, that.name) && Objects.equals(this.annotations, that.annotations) && this.methodIndex == that.methodIndex;
    }

    public int hashCode() {
        return Objects.hash(this.parameter, this.name, this.annotations, this.methodIndex);
    }

    public String toString() {
        return "FunctionParameter[parameter=" + this.parameter + ", name=" + this.name + ", annotations=" + this.annotations + ", methodIndex=" + this.methodIndex + ']';
    }
}

