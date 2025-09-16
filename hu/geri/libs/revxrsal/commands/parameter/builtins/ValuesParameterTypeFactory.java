/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.parameter.builtins;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.Values;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.exception.ValueNotAllowedException;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.parameter.PrioritySpec;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.util.Collections;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public enum ValuesParameterTypeFactory implements ParameterType.Factory<CommandActor>
{
    INSTANCE;


    @Override
    public <T> ParameterType<CommandActor, T> create(@NotNull Type parameterType, @NotNull AnnotationList annotations, @NotNull Lamp<CommandActor> lamp) {
        List<String> allowed;
        final Values values = annotations.get(Values.class);
        if (values == null) {
            return null;
        }
        final ParameterType delegate = lamp.findNextResolver(parameterType, annotations, this).requireParameterType();
        List<String> list = allowed = values.caseSensitive() ? Arrays.asList(values.value()) : Collections.map(values.value(), String::toUpperCase);
        if (allowed.isEmpty()) {
            throw new IllegalArgumentException("@Values() must contain at least 1 value!");
        }
        return new ParameterType<CommandActor, T>(){

            @Override
            public T parse(@NotNull MutableStringStream input, @NotNull @NotNull ExecutionContext<@NotNull CommandActor> context) {
                int start = input.position();
                Object value = delegate.parse(input, context);
                int end = input.position();
                input.setPosition(start);
                String consumed = input.peek(end - start);
                input.setPosition(end);
                if (values.caseSensitive() && allowed.contains(consumed) || !values.caseSensitive() && allowed.contains(consumed.toUpperCase())) {
                    return value;
                }
                throw new ValueNotAllowedException(consumed, Arrays.asList(values.value()), values.caseSensitive());
            }

            @Override
            @NotNull
            public @NotNull SuggestionProvider<@NotNull CommandActor> defaultSuggestions() {
                return SuggestionProvider.of(values.value());
            }

            @Override
            @NotNull
            public PrioritySpec parsePriority() {
                return PrioritySpec.highest();
            }
        };
    }
}

