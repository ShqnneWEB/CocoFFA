/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.LiteralMessage
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType$StringType
 *  com.mojang.brigadier.context.StringRange
 *  com.mojang.brigadier.suggestion.IntegerSuggestion
 *  com.mojang.brigadier.suggestion.Suggestion
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package hu.geri.libs.revxrsal.commands.brigadier;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.IntegerSuggestion;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import hu.geri.libs.revxrsal.commands.autocomplete.AsyncSuggestionProvider;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.brigadier.BrigadierConverter;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.Potential;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import hu.geri.libs.revxrsal.commands.util.Strings;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BrigadierAdapter {
    @Nullable
    public static <S, A extends CommandActor> com.mojang.brigadier.suggestion.SuggestionProvider<S> createSuggestionProvider(ParameterNode<A, ?> parameter, BrigadierConverter<A, S> converter) {
        SuggestionProvider suggestions = parameter.suggestions();
        if (suggestions.equals(SuggestionProvider.empty())) {
            if (parameter.parameterType() instanceof BrigadierParameterType) {
                BrigadierParameterType brigadierParameterType = (BrigadierParameterType)parameter.parameterType();
                return (arg_0, arg_1) -> ((ArgumentType)brigadierParameterType.argumentType).listSuggestions(arg_0, arg_1);
            }
            return null;
        }
        String tooltipMessage = parameter.description() == null ? parameter.name() : parameter.description();
        return (context, builder) -> {
            Object actor = converter.createActor(context.getSource(), parameter.lamp());
            LiteralMessage tooltip = new LiteralMessage(tooltipMessage);
            String input = context.getInput();
            MutableStringStream stream = StringStream.createMutable(input.startsWith("/") ? input.substring(1) : input);
            if (stream.peekUnquotedString().indexOf(58) != -1) {
                stream = StringStream.createMutable(Strings.stripNamespace(input));
            }
            Potential test = parameter.command().test(actor, stream.toMutableCopy());
            if (suggestions instanceof AsyncSuggestionProvider) {
                return BrigadierAdapter.provideAsyncCompletions((AsyncSuggestionProvider)((Object)suggestions), builder, test.context(), (Message)tooltip);
            }
            List<@NotNull T> values = suggestions.getSuggestions(test.context()).stream().sorted(String.CASE_INSENSITIVE_ORDER).distinct().map(arg_0 -> BrigadierAdapter.lambda$null$0(builder, (Message)tooltip, arg_0)).collect(Collectors.toList());
            return CompletableFuture.completedFuture(Suggestions.create((String)builder.getInput(), values));
        };
    }

    @NotNull
    public static <A extends CommandActor> CompletableFuture<Suggestions> provideAsyncCompletions(@NotNull AsyncSuggestionProvider<A> suggestions, @NotNull SuggestionsBuilder builder, @NotNull ExecutionContext<A> context, @Nullable Message tooltip) {
        CompletableFuture<Collection<String>> completions = suggestions.getSuggestionsAsync(context);
        return completions.thenApply(strings -> Suggestions.create((String)builder.getInput(), (Collection)strings.stream().sorted(String.CASE_INSENSITIVE_ORDER).distinct().map(v -> BrigadierAdapter.toSuggestion(v, builder, tooltip)).collect(Collectors.toList())));
    }

    @NotNull
    private static Suggestion toSuggestion(@NotNull String value, @NotNull SuggestionsBuilder builder, @Nullable Message tooltip) {
        try {
            int intValue = Integer.parseInt(value);
            return new IntegerSuggestion(StringRange.between((int)builder.getStart(), (int)builder.getInput().length()), intValue, tooltip);
        } catch (NumberFormatException e) {
            return new Suggestion(StringRange.between((int)builder.getStart(), (int)builder.getInput().length()), value, tooltip);
        }
    }

    @NotNull
    public static <A extends CommandActor, T> ParameterType<A, T> toParameterType(@NotNull ArgumentType<T> argumentType) {
        return new BrigadierParameterType(argumentType);
    }

    private static /* synthetic */ Suggestion lambda$null$0(SuggestionsBuilder builder, Message tooltip, String s) {
        return BrigadierAdapter.toSuggestion(s, builder, tooltip);
    }

    private static final class BrigadierParameterType<A extends CommandActor, T>
    implements ParameterType<A, T> {
        private final ArgumentType<T> argumentType;

        private BrigadierParameterType(ArgumentType<T> argumentType) {
            this.argumentType = argumentType;
        }

        @Override
        public boolean isGreedy() {
            if (this.argumentType instanceof StringArgumentType) {
                StringArgumentType sat = (StringArgumentType)this.argumentType;
                return sat.getType() == StringArgumentType.StringType.GREEDY_PHRASE;
            }
            return false;
        }

        @Override
        public T parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<A> context) {
            StringReader reader = new StringReader(input.source());
            reader.setCursor(input.position());
            Object result = this.argumentType.parse(reader);
            input.setPosition(reader.getCursor());
            return (T)result;
        }

        public ArgumentType<T> argumentType() {
            return this.argumentType;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            BrigadierParameterType that = (BrigadierParameterType)obj;
            return Objects.equals(this.argumentType, that.argumentType);
        }

        public int hashCode() {
            return Objects.hash(this.argumentType);
        }

        public String toString() {
            return "BrigadierParameterType[argumentType=" + this.argumentType + ']';
        }
    }
}

