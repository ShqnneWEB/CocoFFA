/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter.builtins;

import hu.geri.libs.revxrsal.commands.parameter.builtins.CollectionParameterTypeFactory;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class ListParameterTypeFactory
extends CollectionParameterTypeFactory {
    public static final ListParameterTypeFactory INSTANCE = new ListParameterTypeFactory();

    private ListParameterTypeFactory() {
    }

    @Override
    protected boolean matchType(@NotNull Type type, @NotNull Class<?> rawType) {
        return rawType == List.class || rawType == Collection.class;
    }

    @Override
    protected Type getElementType(@NotNull Type type) {
        return Classes.getFirstGeneric(type, String.class);
    }

    @Override
    protected Object convert(List<Object> items, Type componentType) {
        return items;
    }
}

