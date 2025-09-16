/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.exception;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandFunction;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.exception.CommandExceptionHandler;
import hu.geri.libs.revxrsal.commands.exception.context.ErrorContext;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.node.LiteralNode;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.util.Reflections;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class RuntimeExceptionAdapter<A extends CommandActor>
implements CommandExceptionHandler<A> {
    private final List<CommandExceptionHandler<A>> handlers = new ArrayList<CommandExceptionHandler<A>>();

    public RuntimeExceptionAdapter() {
        for (Method method : Reflections.getAllMethods(this.getClass())) {
            if (!RuntimeExceptionAdapter.isHandler(method)) continue;
            CommandExceptionHandler<A> handler = this.createHandler(method);
            this.handlers.add(handler);
        }
    }

    @Contract(value="_ -> fail")
    private static void sneakyThrow(Throwable t) {
        throw t;
    }

    private static boolean isHandler(Method method) {
        return method.isAnnotationPresent(HandleException.class);
    }

    @NotNull
    protected static String fmt(@NotNull Number number) {
        return NumberFormat.getInstance().format(number);
    }

    @NotNull
    private CommandExceptionHandler<A> createHandler(Method method) {
        HandlerParameterSupplier[] suppliers = new HandlerParameterSupplier[method.getParameterCount()];
        ArrayList conditions = new ArrayList();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; ++i) {
            Parameter parameter = parameters[i];
            Class<?> type = parameter.getType();
            if (Throwable.class.isAssignableFrom(type)) {
                conditions.add((throwable, errorContext) -> type.isAssignableFrom(throwable.getClass()));
                suppliers[i] = (throwable, errorContext) -> throwable;
                continue;
            }
            if (ExecutionContext.class.isAssignableFrom(type)) {
                conditions.add((throwable, errorContext) -> errorContext.hasExecutionContext());
                suppliers[i] = (throwable, errorContext) -> errorContext.context();
                continue;
            }
            if (ErrorContext.class.isAssignableFrom(type)) {
                conditions.add((throwable, errorContext) -> type.isAssignableFrom(errorContext.getClass()));
                suppliers[i] = (throwable, errorContext) -> errorContext;
                continue;
            }
            if (CommandActor.class.isAssignableFrom(type)) {
                suppliers[i] = (throwable, errorContext) -> errorContext.actor();
                continue;
            }
            if (Lamp.class.isAssignableFrom(type)) {
                suppliers[i] = (throwable, errorContext) -> errorContext.lamp();
                continue;
            }
            if (ExecutableCommand.class.isAssignableFrom(type)) {
                conditions.add((throwable, errorContext) -> errorContext.hasExecutionContext());
                suppliers[i] = (throwable, errorContext) -> errorContext.context().command();
                continue;
            }
            if (CommandFunction.class.isAssignableFrom(type)) {
                conditions.add((throwable, errorContext) -> errorContext.hasExecutionContext());
                suppliers[i] = (throwable, errorContext) -> errorContext.context().command().function();
                continue;
            }
            if (CommandParameter.class.isAssignableFrom(type)) {
                conditions.add((throwable, errorContext) -> errorContext instanceof ErrorContext.ParsingParameter);
                suppliers[i] = (throwable, errorContext) -> ((ErrorContext.ParsingParameter)errorContext).parameter().parameter();
                continue;
            }
            if (ParameterNode.class.isAssignableFrom(type)) {
                conditions.add((throwable, errorContext) -> errorContext instanceof ErrorContext.ParsingParameter);
                suppliers[i] = (throwable, errorContext) -> ((ErrorContext.ParsingParameter)errorContext).parameter();
                continue;
            }
            if (LiteralNode.class.isAssignableFrom(type)) {
                conditions.add((throwable, errorContext) -> errorContext instanceof ErrorContext.ParsingLiteral);
                suppliers[i] = (throwable, errorContext) -> ((ErrorContext.ParsingLiteral)errorContext).literal();
                continue;
            }
            throw new IllegalArgumentException("Don't know how to handle parameter of type " + type + " for a @HandleException function (" + method + ")");
        }
        return (throwable, errorContext) -> {
            for (HandlerPredicate condition : conditions) {
                if (condition.test(throwable, errorContext)) continue;
                return;
            }
            Object[] arguments = new Object[parameters.length];
            for (int i = 0; i < suppliers.length; ++i) {
                HandlerParameterSupplier supplier = suppliers[i];
                arguments[i] = supplier.supply(throwable, errorContext);
            }
            try {
                method.invoke(this, arguments);
            } catch (IllegalAccessException e) {
                RuntimeExceptionAdapter.sneakyThrow(e);
            } catch (InvocationTargetException e) {
                RuntimeExceptionAdapter.sneakyThrow(e.getCause());
            }
        };
    }

    @Override
    public final void handleException(@NotNull Throwable throwable, @NotNull ErrorContext<A> errorContext) {
        for (CommandExceptionHandler<A> handler : this.handlers) {
            handler.handleException(throwable, errorContext);
        }
    }

    private static interface HandlerPredicate<A extends CommandActor> {
        public boolean test(@NotNull Throwable var1, @NotNull ErrorContext<A> var2);
    }

    private static interface HandlerParameterSupplier<A extends CommandActor> {
        public Object supply(@NotNull Throwable var1, @NotNull ErrorContext<A> var2);
    }

    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface HandleException {
    }
}

