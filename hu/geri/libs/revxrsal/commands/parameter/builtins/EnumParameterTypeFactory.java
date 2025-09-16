/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter.builtins;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.EnumNotFoundException;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.parameter.PrioritySpec;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public enum EnumParameterTypeFactory implements ParameterType.Factory<CommandActor>
{
    INSTANCE;


    @Override
    public <T> ParameterType<CommandActor, T> create(@NotNull Type parameterType, @NotNull AnnotationList annotations, @NotNull Lamp<CommandActor> lamp) {
        Class<?> rawType = Classes.getRawType(parameterType);
        if (!rawType.isEnum()) {
            return null;
        }
        Enum[] enumConstants = (Enum[])rawType.getEnumConstants();
        HashMap<String, Enum> byKeys = new HashMap<String, Enum>();
        ArrayList<String> suggestions = new ArrayList<String>();
        for (Enum enumConstant : enumConstants) {
            String name = enumConstant.name().toLowerCase();
            byKeys.put(name, enumConstant);
            suggestions.add(name);
        }
        return new EnumParameterType(rawType, byKeys, suggestions);
    }

    private static final class EnumParameterType<E extends Enum<E>>
    implements ParameterType<CommandActor, E> {
        private final Class<E> enumType;
        private final Map<String, E> byKeys;
        private final List<String> suggestions;

        private EnumParameterType(Class<E> enumType, Map<String, E> byKeys, List<String> suggestions) {
            this.enumType = enumType;
            this.byKeys = byKeys;
            this.suggestions = suggestions;
        }

        @Override
        public E parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<CommandActor> context) {
            String key = input.readUnquotedString();
            Enum value = (Enum)this.byKeys.get(key.toLowerCase());
            if (value != null) {
                return (E)value;
            }
            throw new EnumNotFoundException(key, this.enumType);
        }

        @Override
        @NotNull
        public SuggestionProvider<CommandActor> defaultSuggestions() {
            return SuggestionProvider.of(this.suggestions);
        }

        @Override
        @NotNull
        public PrioritySpec parsePriority() {
            return PrioritySpec.highest();
        }

        public Map<String, E> byKeys() {
            return this.byKeys;
        }

        public List<String> suggestions() {
            return this.suggestions;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            EnumParameterType that = (EnumParameterType)obj;
            return Objects.equals(this.byKeys, that.byKeys) && Objects.equals(this.suggestions, that.suggestions);
        }

        public int hashCode() {
            return Objects.hash(this.byKeys, this.suggestions);
        }

        public String toString() {
            return "EnumParameterType[byKeys=" + this.byKeys + ", suggestions=" + this.suggestions + ']';
        }
    }
}

