/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter.builtins;

import hu.geri.libs.revxrsal.commands.parameter.builtins.CollectionParameterTypeFactory;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class ArrayParameterTypeFactory
extends CollectionParameterTypeFactory {
    public static final ArrayParameterTypeFactory INSTANCE = new ArrayParameterTypeFactory();

    @Override
    protected boolean matchType(@NotNull Type type, @NotNull Class<?> rawType) {
        Type elementType = Classes.arrayComponentType(type);
        return elementType != null;
    }

    @Override
    protected Type getElementType(@NotNull Type type) {
        return Classes.arrayComponentType(type);
    }

    @Override
    protected Object convert(List<Object> items, Type componentType) {
        Class<?> arrayComponentType = Classes.getRawType(componentType);
        Object array = Array.newInstance(arrayComponentType, items.size());
        for (int i = 0; i < items.size(); ++i) {
            Array.set(array, i, items.get(i));
        }
        return array;
    }
}

