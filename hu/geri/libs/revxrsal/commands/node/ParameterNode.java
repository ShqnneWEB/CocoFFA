/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node;

import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import hu.geri.libs.revxrsal.commands.node.CommandNode;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.node.HasDescription;
import hu.geri.libs.revxrsal.commands.node.LiteralNode;
import hu.geri.libs.revxrsal.commands.node.RequiresPermission;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import java.lang.reflect.Type;
import java.util.Collection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ParameterNode<A extends CommandActor, T>
extends CommandNode<A>,
RequiresPermission<A>,
HasDescription {
    @Nullable
    public T parse(MutableStringStream var1, ExecutionContext<A> var2);

    @NotNull
    public AnnotationList annotations();

    public boolean isOptional();

    default public boolean isRequired() {
        return !this.isOptional();
    }

    @NotNull
    public ParameterType<A, T> parameterType();

    @NotNull
    public SuggestionProvider<A> suggestions();

    @NotNull
    public CommandParameter parameter();

    public boolean isGreedy();

    @Contract(pure=true)
    @NotNull
    public Collection<String> complete(@NotNull ExecutionContext<A> var1);

    @NotNull
    default public Class<?> type() {
        return this.parameter().type();
    }

    @NotNull
    default public Type fullType() {
        return this.parameter().fullType();
    }

    @Override
    default public boolean isLiteral() {
        return false;
    }

    @Override
    default public boolean isParameter() {
        return true;
    }

    @Override
    @Contract(value="-> fail")
    @NotNull
    default public LiteralNode<A> requireLiteralNode() {
        throw new IllegalStateException("Expected a LiteralNode, found a ParameterNode");
    }

    public boolean isFlag();

    public boolean isSwitch();

    @Nullable
    @Contract(pure=true)
    public Character shorthand();

    @Nullable
    @Contract(pure=true)
    public String switchName();

    @Nullable
    @Contract(pure=true)
    public String flagName();
}

