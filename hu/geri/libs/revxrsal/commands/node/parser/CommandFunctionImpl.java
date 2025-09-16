/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node.parser;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandFunction;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import hu.geri.libs.revxrsal.commands.node.parser.FunctionParameter;
import hu.geri.libs.revxrsal.commands.reflect.MethodCaller;
import hu.geri.libs.revxrsal.commands.response.ResponseHandler;
import hu.geri.libs.revxrsal.commands.util.Strings;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

final class CommandFunctionImpl
implements CommandFunction {
    @NotNull
    private final Lamp<?> lamp;
    @NotNull
    private final Method method;
    private final @Unmodifiable Map<String, CommandParameter> parameters;
    @NotNull
    private final AnnotationList annotations;
    @NotNull
    private final MethodCaller.BoundMethodCaller caller;
    @NotNull
    private final ResponseHandler<?, ?> responseHandler;

    @NotNull
    public static CommandFunction create(@NotNull Method method, @NotNull AnnotationList annotations, @NotNull Lamp<?> lamp, @NotNull MethodCaller.BoundMethodCaller caller) {
        Parameter[] pArray = method.getParameters();
        LinkedHashMap<String, CommandParameter> parameters = new LinkedHashMap(pArray.length);
        for (int methodIndex = 0; methodIndex < pArray.length; ++methodIndex) {
            Parameter parameter = pArray[methodIndex];
            AnnotationList parameterAnnotations = AnnotationList.create(parameter).replaceAnnotations(parameter, lamp.annotationReplacers());
            String name = Strings.getOverriddenName(parameterAnnotations).orElseGet(() -> lamp.parameterNamingStrategy().getName(parameter));
            FunctionParameter fnParameter = new FunctionParameter(parameter, name, parameterAnnotations, methodIndex);
            parameters.put(fnParameter.name(), fnParameter);
        }
        parameters = Collections.unmodifiableMap(parameters);
        ResponseHandler handler = lamp.responseHandler(method.getGenericReturnType(), annotations);
        return new CommandFunctionImpl(lamp, method, parameters, annotations, caller, handler);
    }

    @Override
    public <A extends CommandActor> Lamp<A> lamp() {
        return this.lamp;
    }

    @Override
    @NotNull
    public String name() {
        return this.method.getName();
    }

    @Override
    @NotNull
    public AnnotationList annotations() {
        return this.annotations;
    }

    @Override
    @NotNull
    public Method method() {
        return this.method;
    }

    @Override
    @NotNull
    public @Unmodifiable Map<String, CommandParameter> parametersByName() {
        return this.parameters;
    }

    @Override
    @NotNull
    public CommandParameter parameter(String name) {
        CommandParameter parameter = this.parameters.get(name);
        if (parameter == null) {
            throw new NoSuchElementException("No such parameter with name '" + name + "'");
        }
        return parameter;
    }

    @Override
    @NotNull
    public MethodCaller.BoundMethodCaller caller() {
        return this.caller;
    }

    @Override
    public <T> T call(@NotNull Object ... arguments) {
        return (T)this.caller.call(arguments);
    }

    @Override
    @NotNull
    public <T> ResponseHandler<?, T> responseHandler() {
        return this.responseHandler;
    }

    public String toString() {
        return "CommandFunction(" + this.method.toGenericString() + ")";
    }

    public CommandFunctionImpl(@NotNull Lamp<?> lamp, @NotNull Method method, Map<String, CommandParameter> parameters, @NotNull AnnotationList annotations, @NotNull MethodCaller.BoundMethodCaller caller, @NotNull ResponseHandler<?, ?> responseHandler) {
        if (lamp == null) {
            throw new NullPointerException("lamp is marked non-null but is null");
        }
        if (method == null) {
            throw new NullPointerException("method is marked non-null but is null");
        }
        if (annotations == null) {
            throw new NullPointerException("annotations is marked non-null but is null");
        }
        if (caller == null) {
            throw new NullPointerException("caller is marked non-null but is null");
        }
        if (responseHandler == null) {
            throw new NullPointerException("responseHandler is marked non-null but is null");
        }
        this.lamp = lamp;
        this.method = method;
        this.parameters = parameters;
        this.annotations = annotations;
        this.caller = caller;
        this.responseHandler = responseHandler;
    }
}

