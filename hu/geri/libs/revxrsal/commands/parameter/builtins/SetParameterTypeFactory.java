/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter.builtins;

import hu.geri.libs.revxrsal.commands.parameter.builtins.CollectionParameterTypeFactory;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public final class SetParameterTypeFactory
extends CollectionParameterTypeFactory {
    public static final SetParameterTypeFactory INSTANCE = new SetParameterTypeFactory();

    private SetParameterTypeFactory() {
    }

    @Override
    protected boolean matchType(@NotNull Type type, @NotNull Class<?> rawType) {
        return rawType == Set.class;
    }

    @Override
    protected Type getElementType(@NotNull Type type) {
        return Classes.getFirstGeneric(type, String.class);
    }

    @Override
    protected Object convert(List<Object> items, Type componentType) {
        return new LinkedHashSet<Object>(items);
    }

    @Override
    protected boolean preventsDuplicates() {
        return true;
    }
}

