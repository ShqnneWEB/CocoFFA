/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.reflect.ktx;

import hu.geri.libs.revxrsal.commands.reflect.MethodCaller;
import hu.geri.libs.revxrsal.commands.reflect.ktx.CallableMethod;
import hu.geri.libs.revxrsal.commands.reflect.ktx.DefaultFunctionFinder;
import hu.geri.libs.revxrsal.commands.reflect.ktx.KotlinConstants;
import hu.geri.libs.revxrsal.commands.reflect.ktx.KotlinFunction;
import hu.geri.libs.revxrsal.commands.reflect.ktx.KotlinSingletons;
import hu.geri.libs.revxrsal.commands.util.Lazy;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

final class KotlinFunctionImpl
implements KotlinFunction {
    private static final String CONTEXT_PARAMETER_PREFIX = "$this$";
    private final CallableMethod mainMethod;
    private final Supplier<@Nullable CallableMethod> defaultMethod;
    private final @Unmodifiable List<Parameter> parameters;
    private final Supplier<@Unmodifiable Map<String, Parameter>> byName = Lazy.of(() -> {
        HashMap<String, Parameter> byName = new HashMap<String, Parameter>();
        for (Parameter parameter : this.getParameters()) {
            byName.put(parameter.getName(), parameter);
        }
        return Collections.unmodifiableMap(byName);
    });

    public KotlinFunctionImpl(Method mainMethod) {
        MethodCaller mainCaller = KotlinSingletons.getCallerForNonDefault(mainMethod);
        this.parameters = Arrays.asList(mainMethod.getParameters());
        this.mainMethod = new CallableMethod(mainMethod, mainCaller);
        this.defaultMethod = Lazy.of(() -> DefaultFunctionFinder.findDefaultFunction(mainMethod));
    }

    private static void checkCallableStatic(@Nullable Object instance, @NotNull Method method) {
        if (instance == null && !Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException("The given method is not static, and no instance was provided. Either mark the function as static with @JvmStatic, or pass the object/companion object value for the instance.");
        }
    }

    @Override
    @NotNull
    public CallableMethod getMethod() {
        return this.mainMethod;
    }

    @Override
    @Nullable
    public CallableMethod getDefaultSyntheticMethod() {
        return this.defaultMethod.get();
    }

    @Override
    public boolean isSuspend() {
        Parameter lastParameter = hu.geri.libs.revxrsal.commands.util.Collections.getOrNull(this.parameters, this.parameters.size() - 1);
        return lastParameter != null && lastParameter.getType() == KotlinConstants.continuation();
    }

    @Override
    public <T> T call(@Nullable Object instance, @NotNull List<Object> arguments, @NotNull Function<Parameter, Boolean> isOptional) {
        Map<Parameter, Object> callArgs = this.mapArgsToParams(i -> hu.geri.libs.revxrsal.commands.util.Collections.getOrNull(arguments, (int)i));
        return this.callByParameters(instance, callArgs, isOptional);
    }

    @Override
    public <T> T callByIndices(@Nullable Object instance, @NotNull Map<Integer, Object> arguments, @NotNull Function<Parameter, Boolean> isOptional) {
        Map<Parameter, Object> callArgs = this.mapArgsToParams(arguments::get);
        return this.callByParameters(instance, callArgs, isOptional);
    }

    @Override
    public <T> T callByNames(@Nullable Object instance, @NotNull Map<String, Object> arguments, @NotNull Function<Parameter, Boolean> isOptional) {
        return this.callByParameters(instance, hu.geri.libs.revxrsal.commands.util.Collections.mapKeys(arguments, this::getParameter), isOptional);
    }

    @Override
    public <T> T callByParameters(@Nullable Object instance, @NotNull Map<Parameter, Object> arguments, @NotNull Function<Parameter, Boolean> isOptional) {
        KotlinFunctionImpl.checkCallableStatic(instance, this.mainMethod.method());
        ArrayList<Object> args = new ArrayList<Object>();
        int mask = 0;
        ArrayList<Integer> masks = new ArrayList<Integer>(1);
        int index = 0;
        boolean anyOptional = false;
        boolean hasContextReceiver = false;
        for (Parameter parameter : this.parameters) {
            Object providedArg;
            if (index == 0 && parameter.getName().startsWith(CONTEXT_PARAMETER_PREFIX)) {
                hasContextReceiver = true;
            }
            if (index != 0 && index % 32 == 0) {
                masks.add(mask);
                mask = 0;
            }
            if ((providedArg = arguments.get(parameter)) != null) {
                args.add(providedArg);
            } else if (isOptional.apply(parameter).booleanValue()) {
                mask |= 1 << index % 32;
                args.add(KotlinConstants.defaultPrimitiveValue(parameter.getType()));
                anyOptional = true;
            } else if (parameter.isVarArgs()) {
                args.add(Array.newInstance(parameter.getType(), 0));
            } else {
                throw new IllegalArgumentException("No argument provided for a required parameter: " + parameter + ".");
            }
            ++index;
        }
        if (!anyOptional) {
            return (T)this.mainMethod.caller().call(instance, args.toArray());
        }
        CallableMethod defaultMethod = this.defaultMethod.get();
        if (defaultMethod == null) {
            if (this.mainMethod.method().getParameterCount() == args.size()) {
                return (T)this.mainMethod.caller().call(instance, args.toArray());
            }
            throw new IllegalArgumentException("Unable to invoke function with default parameters. This may happen because you have an @Optional non-null primitive type (e.g. Int) with no default value using @Default or a Kotlin-default value. It may also occur if you have @Switch with no default value. (@Switch param: Boolean = ...). Either mark it as nullable, add a default value (@Optional param: Type = ...), or use @Default");
        }
        masks.add(mask);
        if (hasContextReceiver) {
            masks.set(0, (Integer)masks.get(0) / 2);
        }
        args.addAll(masks);
        args.add(null);
        return (T)defaultMethod.caller().call(instance, args.toArray());
    }

    @NotNull
    private Map<Parameter, Object> mapArgsToParams(@NotNull Function<Integer, Object> map) {
        HashMap<Parameter, Object> callArgs = new HashMap<Parameter, Object>();
        for (int i = 0; i < this.parameters.size(); ++i) {
            Parameter parameter = this.parameters.get(i);
            callArgs.put(parameter, map.apply(i));
        }
        return callArgs;
    }

    @Override
    public @Unmodifiable @NotNull List<Parameter> getParameters() {
        return this.parameters;
    }

    @Override
    public @Unmodifiable @NotNull Map<String, Parameter> getParametersByName() {
        return this.byName.get();
    }

    @Override
    @NotNull
    public Parameter getParameter(@NotNull String name) {
        Parameter parameter = this.getParametersByName().get(name);
        if (parameter == null) {
            throw new IllegalArgumentException("No such parameter: '" + name + "'. Available parameters: " + this.getParametersByName().keySet());
        }
        return parameter;
    }

    @Override
    @NotNull
    public Parameter getParameter(int index) {
        return this.parameters.get(index);
    }
}

