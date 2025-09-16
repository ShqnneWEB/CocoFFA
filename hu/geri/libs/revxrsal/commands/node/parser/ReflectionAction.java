/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node.parser;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandFunction;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import hu.geri.libs.revxrsal.commands.exception.context.ErrorContext;
import hu.geri.libs.revxrsal.commands.node.CommandAction;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ContextParameter;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public final class ReflectionAction<A extends CommandActor>
implements CommandAction<A> {
    private final CommandFunction function;
    private final Map<Integer, ParameterSupplier<A>> parameters = new HashMap<Integer, ParameterSupplier<A>>();

    public ReflectionAction(CommandFunction function) {
        this.function = function;
    }

    @Override
    public void execute(ExecutionContext<A> context) {
        try {
            Object[] arguments = new Object[this.function.method().getParameterCount()];
            this.parameters.forEach((index, parameter) -> {
                arguments[index.intValue()] = parameter.get(context);
            });
            context.resolvedArguments().forEach((parameterName, value) -> {
                context.lamp().validate(context.actor(), value, context.command().parameter((String)parameterName));
                int index = this.function.parameter((String)parameterName).methodIndex();
                arguments[index] = value;
            });
            Object result = this.function.call(arguments);
            if (result != null) {
                this.function.responseHandler().handleResponse(result, context);
            }
            context.lamp().hooks().onPostCommandExecuted(context.command(), context);
        } catch (Throwable t) {
            context.lamp().handleException(t, ErrorContext.executingFunction(context));
        }
    }

    void addContextParameter(CommandParameter parameter, ContextParameter<A, ?> contextParameter) {
        this.parameters.put(parameter.methodIndex(), context -> contextParameter.resolve(parameter, context));
    }

    private static interface ParameterSupplier<A extends CommandActor> {
        public Object get(@NotNull ExecutionContext<A> var1);
    }
}

