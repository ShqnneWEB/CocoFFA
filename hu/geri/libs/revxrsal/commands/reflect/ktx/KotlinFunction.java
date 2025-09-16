/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.reflect.ktx;

import hu.geri.libs.revxrsal.commands.reflect.ktx.CallableMethod;
import hu.geri.libs.revxrsal.commands.reflect.ktx.KotlinFunctionImpl;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public interface KotlinFunction {
    @NotNull
    public static KotlinFunction wrap(@NotNull Method method) {
        Preconditions.notNull(method, "method");
        return new KotlinFunctionImpl(method);
    }

    public boolean isSuspend();

    public <T> T call(@Nullable Object var1, @NotNull List<Object> var2, @NotNull Function<Parameter, Boolean> var3);

    public <T> T callByIndices(@Nullable Object var1, @NotNull Map<Integer, Object> var2, @NotNull Function<Parameter, Boolean> var3);

    public <T> T callByParameters(@Nullable Object var1, @NotNull Map<Parameter, Object> var2, @NotNull Function<Parameter, Boolean> var3);

    public <T> T callByNames(@Nullable Object var1, @NotNull Map<String, Object> var2, @NotNull Function<Parameter, Boolean> var3);

    @NotNull
    public CallableMethod getMethod();

    @Nullable
    public CallableMethod getDefaultSyntheticMethod();

    public @Unmodifiable @NotNull List<Parameter> getParameters();

    public @Unmodifiable @NotNull Map<String, Parameter> getParametersByName();

    @NotNull
    public Parameter getParameter(@NotNull String var1);

    @NotNull
    public Parameter getParameter(int var1) throws IndexOutOfBoundsException;
}

