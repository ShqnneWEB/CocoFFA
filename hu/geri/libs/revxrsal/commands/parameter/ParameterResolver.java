/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.parameter.ContextParameter;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;

public final class ParameterResolver<A extends CommandActor, T> {
    private final Object resolver;

    private ParameterResolver(Object resolver) {
        if (resolver instanceof ParameterType && resolver instanceof ContextParameter) {
            throw new IllegalArgumentException("A ParameterResolver cannot wrap an object that is both a ParameterType and a ContextParameter.");
        }
        if (!(resolver instanceof ParameterType) && !(resolver instanceof ContextParameter)) {
            throw new IllegalArgumentException("A ParameterResolver cannot wrap an object that is not a ParameterType or a ContextParameter.");
        }
        this.resolver = resolver;
    }

    @NotNull
    public static <A extends CommandActor, T> ParameterResolver<A, T> parameterType(@NotNull ParameterType<A, T> type) {
        return new ParameterResolver<A, T>(type);
    }

    @NotNull
    public static <A extends CommandActor, T> ParameterResolver<A, T> contextParameter(@NotNull ContextParameter<A, T> type) {
        return new ParameterResolver<A, T>(type);
    }

    public boolean consumesInput() {
        return this.isParameterType();
    }

    public boolean isParameterType() {
        return this.resolver instanceof ParameterType;
    }

    public boolean isContextParameter() {
        return this.resolver instanceof ContextParameter;
    }

    @NotNull
    public ParameterType<A, T> requireParameterType() {
        return this.requireParameterType("Expected a ParameterType, received a ContextResolver (resolver: " + this.resolver + ")");
    }

    @NotNull
    public ParameterType<A, T> requireParameterType(Type typeHint) {
        return this.requireParameterType("Expected a ParameterType, received a ContextResolver (resolver: " + this.resolver + ", type: " + typeHint + ")");
    }

    @NotNull
    public ContextParameter<A, T> requireContextParameter() {
        return this.requireContextParameter("Expected a ContextResolver, received a ParameterType (resolver: " + this.resolver + ")");
    }

    @NotNull
    public ContextParameter<A, T> requireContextParameter(Type typeHint) {
        return this.requireContextParameter("Expected a ContextResolver, received a ParameterType (resolver: " + this.resolver + ", type: " + typeHint + ")");
    }

    @NotNull
    public ParameterType<A, T> requireParameterType(@NotNull String errorMessage) {
        if (!this.isParameterType()) {
            throw new IllegalStateException(errorMessage);
        }
        return (ParameterType)this.resolver;
    }

    @NotNull
    public ContextParameter<A, T> requireContextParameter(@NotNull String errorMessage) {
        if (!this.isContextParameter()) {
            throw new IllegalStateException(errorMessage);
        }
        return (ContextParameter)this.resolver;
    }
}

