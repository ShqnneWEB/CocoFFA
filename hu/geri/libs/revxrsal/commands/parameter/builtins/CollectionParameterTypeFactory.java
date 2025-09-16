/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter.builtins;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.Delimiter;
import hu.geri.libs.revxrsal.commands.annotation.Sized;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.InputParseException;
import hu.geri.libs.revxrsal.commands.exception.InvalidListSizeException;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.parameter.PrioritySpec;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.util.Classes;
import hu.geri.libs.revxrsal.commands.util.Collections;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
abstract class CollectionParameterTypeFactory
implements ParameterType.Factory<CommandActor> {
    CollectionParameterTypeFactory() {
    }

    protected abstract boolean matchType(@NotNull Type var1, @NotNull Class<?> var2);

    protected abstract Type getElementType(@NotNull Type var1);

    protected abstract Object convert(List<Object> var1, Type var2);

    protected boolean preventsDuplicates() {
        return false;
    }

    @Override
    @Nullable
    public <T> ParameterType<CommandActor, T> create(@NotNull Type parameterType, @NotNull AnnotationList annotations, @NotNull Lamp<CommandActor> lamp) {
        Class<?> rawType = Classes.getRawType(parameterType);
        if (!this.matchType(parameterType, rawType)) {
            return null;
        }
        Type elementType = this.getElementType(parameterType);
        if (elementType == null) {
            return null;
        }
        @NotNull ParameterType<CommandActor, Object> componentType = lamp.resolver(elementType).requireParameterType(elementType);
        Sized sized = annotations.get(Sized.class);
        int min = 0;
        int max = Integer.MAX_VALUE;
        if (sized != null) {
            min = sized.min();
            max = sized.max();
            if (min < 0 || max < 0 || max < min) {
                throw new IllegalArgumentException("Illegal range input in @Sized");
            }
        }
        char delimiter = annotations.mapOr(Delimiter.class, Delimiter::value, Character.valueOf(' ')).charValue();
        return new CollectionParameterType(delimiter, min, max, componentType, elementType);
    }

    private final class CollectionParameterType
    implements ParameterType<CommandActor, Object> {
        private final char delimiter;
        private final int minSize;
        private final int maxSize;
        private final ParameterType<CommandActor, Object> componentType;
        private final PrioritySpec priority;
        private final Type elementType;

        public CollectionParameterType(char delimiter, int minSize, int maxSize, ParameterType<CommandActor, Object> componentType, Type elementType) {
            this.delimiter = delimiter;
            this.minSize = minSize;
            this.maxSize = maxSize;
            this.componentType = componentType;
            this.elementType = elementType;
            this.priority = componentType.parsePriority().toBuilder().lowerThan(componentType.getClass()).build();
        }

        private List<Object> parseList(@NotNull MutableStringStream input, @NotNull ExecutionContext<CommandActor> context) {
            ArrayList<Object> elements = new ArrayList<Object>();
            while (input.hasRemaining()) {
                Object el = this.componentType.parse(input, context);
                elements.add(el);
                if (!input.hasRemaining()) continue;
                if (input.peek() == this.delimiter) {
                    input.skipWhitespace();
                    continue;
                }
                throw new InputParseException(InputParseException.Cause.EXPECTED_WHITESPACE);
            }
            if (elements.size() > this.maxSize || elements.size() < this.minSize) {
                throw new InvalidListSizeException(this.minSize, this.maxSize, elements.size(), elements);
            }
            return elements;
        }

        @Override
        public Object parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<CommandActor> context) {
            List<Object> objects = this.parseList(input, context);
            return CollectionParameterTypeFactory.this.convert(objects, this.elementType);
        }

        @Override
        @NotNull
        public SuggestionProvider<CommandActor> defaultSuggestions() {
            SuggestionProvider<CommandActor> paramSuggestions = this.componentType.defaultSuggestions();
            if (paramSuggestions.equals(SuggestionProvider.empty())) {
                return SuggestionProvider.empty();
            }
            return context -> {
                List<String> inputted = Arrays.asList(context.input().peekRemaining().split(Character.toString(this.delimiter)));
                if (CollectionParameterTypeFactory.this.preventsDuplicates()) {
                    return Collections.filter(paramSuggestions.getSuggestions(context), c -> !inputted.contains(c));
                }
                return paramSuggestions.getSuggestions(context);
            };
        }

        @Override
        @NotNull
        public PrioritySpec parsePriority() {
            return this.priority;
        }

        @Override
        public boolean isGreedy() {
            return this.maxSize == Integer.MAX_VALUE;
        }
    }
}

