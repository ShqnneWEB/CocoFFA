/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands;

import hu.geri.libs.revxrsal.commands.LampBuilderVisitor;
import hu.geri.libs.revxrsal.commands.LampVisitor;
import hu.geri.libs.revxrsal.commands.annotation.dynamic.AnnotationReplacer;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.autocomplete.AutoCompleter;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProviders;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import hu.geri.libs.revxrsal.commands.command.CommandPermission;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.command.PermissionConditionChecker;
import hu.geri.libs.revxrsal.commands.command.ThreadExecutorCooldownCondition;
import hu.geri.libs.revxrsal.commands.exception.CommandExceptionHandler;
import hu.geri.libs.revxrsal.commands.exception.CommandInvocationException;
import hu.geri.libs.revxrsal.commands.exception.DefaultExceptionHandler;
import hu.geri.libs.revxrsal.commands.exception.SelfHandledException;
import hu.geri.libs.revxrsal.commands.exception.ThrowableFromCommand;
import hu.geri.libs.revxrsal.commands.exception.context.ErrorContext;
import hu.geri.libs.revxrsal.commands.hook.Hooks;
import hu.geri.libs.revxrsal.commands.ktx.KotlinFeatureRegistry;
import hu.geri.libs.revxrsal.commands.node.CommandRegistry;
import hu.geri.libs.revxrsal.commands.node.DispatcherSettings;
import hu.geri.libs.revxrsal.commands.node.ParameterNamingStrategy;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.node.parser.BaseCommandRegistry;
import hu.geri.libs.revxrsal.commands.orphan.OrphanCommand;
import hu.geri.libs.revxrsal.commands.orphan.OrphanRegistry;
import hu.geri.libs.revxrsal.commands.orphan.Orphans;
import hu.geri.libs.revxrsal.commands.parameter.CommandActorSenderResolver;
import hu.geri.libs.revxrsal.commands.parameter.LengthChecker;
import hu.geri.libs.revxrsal.commands.parameter.ParameterFactory;
import hu.geri.libs.revxrsal.commands.parameter.ParameterResolver;
import hu.geri.libs.revxrsal.commands.parameter.ParameterTypes;
import hu.geri.libs.revxrsal.commands.parameter.RangeChecker;
import hu.geri.libs.revxrsal.commands.process.CommandCondition;
import hu.geri.libs.revxrsal.commands.process.MessageSender;
import hu.geri.libs.revxrsal.commands.process.ParameterValidator;
import hu.geri.libs.revxrsal.commands.process.SenderResolver;
import hu.geri.libs.revxrsal.commands.response.CompletionStageResponseHandler;
import hu.geri.libs.revxrsal.commands.response.OptionalResponseHandler;
import hu.geri.libs.revxrsal.commands.response.ResponseHandler;
import hu.geri.libs.revxrsal.commands.response.SupplierResponseHandler;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.stream.StringStream;
import hu.geri.libs.revxrsal.commands.util.Classes;
import hu.geri.libs.revxrsal.commands.util.Collections;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public final class Lamp<A extends CommandActor> {
    private final Map<Class<? extends Annotation>, Set<AnnotationReplacer<?>>> annotationReplacers;
    private final ParameterNamingStrategy parameterNamingStrategy;
    private final ParameterTypes<A> parameterTypes;
    private final SuggestionProviders<A> suggestionProviders;
    private final Hooks<A> hooks;
    private final List<SenderResolver<? super A>> senderResolvers;
    private final List<ParameterValidator<A, Object>> validators;
    private final List<CommandCondition<? super A>> commandConditions;
    private final List<ResponseHandler.Factory<? super A>> responseHandlers;
    private final List<CommandPermission.Factory<? super A>> permissionFactories;
    private final MessageSender<? super A, String> messageSender;
    private final MessageSender<? super A, String> errorSender;
    private final Map<Class<?>, Supplier<Object>> dependencies;
    private final CommandExceptionHandler<A> exceptionHandler;
    private final DispatcherSettings<A> dispatcherSettings;
    private final BaseCommandRegistry<A> tree;
    private final AutoCompleter<A> autoCompleter;

    public Lamp(Builder<A> builder) {
        this.annotationReplacers = Collections.copyMap(((Builder)builder).annotationReplacers);
        this.senderResolvers = Collections.copyList(((Builder)builder).senderResolvers);
        this.validators = Collections.copyList(((Builder)builder).validators);
        this.responseHandlers = Collections.copyList(((Builder)builder).responseHandlers);
        this.commandConditions = Collections.copyList(((Builder)builder).conditions);
        this.permissionFactories = Collections.copyList(((Builder)builder).permissionFactories);
        this.dependencies = Collections.copyMap(((Builder)builder).dependencies);
        this.messageSender = ((Builder)builder).messageSender;
        this.errorSender = ((Builder)builder).errorSender;
        this.parameterNamingStrategy = ((Builder)builder).namingStrategy;
        this.parameterTypes = ((Builder)builder).parameterTypes.build();
        this.suggestionProviders = ((Builder)builder).suggestionProviders.build();
        this.hooks = ((Builder)builder).hooks.build();
        this.exceptionHandler = ((Builder)builder).exceptionHandler;
        this.dispatcherSettings = ((Builder)builder).dispatcherSettings.build();
        this.tree = new BaseCommandRegistry(this);
        this.autoCompleter = AutoCompleter.create(this);
    }

    @NotNull
    public static <A extends CommandActor> Builder<A> builder() {
        return new Builder();
    }

    @NotNull
    public <T> ParameterResolver<A, T> resolver(@NotNull CommandParameter parameter) {
        return this.resolver(parameter.fullType(), parameter.annotations());
    }

    @NotNull
    public <T> ParameterResolver<A, T> resolver(@NotNull Type type) {
        return this.resolver(type, AnnotationList.empty());
    }

    @NotNull
    public <T> ParameterResolver<A, T> resolver(@NotNull Type type, @NotNull AnnotationList annotations) {
        return this.parameterTypes.resolver(type, annotations, this);
    }

    @NotNull
    public <T> ParameterResolver<A, T> findNextResolver(@NotNull Type type, @NotNull AnnotationList annotations, @NotNull ParameterFactory skipPast) {
        return this.parameterTypes.findNextResolver(type, annotations, skipPast, this);
    }

    @NotNull
    public SuggestionProvider<A> suggestionProvider(Type type) {
        return this.suggestionProvider(type, AnnotationList.empty());
    }

    @NotNull
    public SuggestionProvider<A> suggestionProvider(Type type, AnnotationList annotations) {
        return this.suggestionProviders.provider(type, annotations, this);
    }

    @NotNull
    public SuggestionProvider<A> suggestionProvider(CommandParameter parameter) {
        return this.suggestionProviders.provider(parameter, this);
    }

    public SuggestionProvider<A> findNextSuggestionProvider(Type type, AnnotationList annotations, SuggestionProvider.Factory<? super A> skipPast, Lamp<A> lamp) {
        return this.suggestionProviders.findNextProvider(type, annotations, skipPast, lamp);
    }

    @NotNull
    public <T> ResponseHandler<A, T> responseHandler(@NotNull Type type, @NotNull AnnotationList annotations) {
        for (ResponseHandler.Factory<A> responseHandler : this.responseHandlers) {
            ResponseHandler handler = responseHandler.create(type, annotations, this);
            if (handler == null) continue;
            return handler;
        }
        return ResponseHandler.noOp();
    }

    @NotNull
    public @Unmodifiable List<ExecutableCommand<A>> register(Object ... instances) {
        ArrayList<ExecutableCommand<A>> registered = new ArrayList<ExecutableCommand<A>>();
        for (Object instance : instances) {
            Class<?> commandClass;
            Class<?> clazz = commandClass = instance instanceof Class ? (Class<?>)instance : instance.getClass();
            if (instance instanceof OrphanCommand) {
                throw new IllegalArgumentException("You cannot register an OrphanCommand directly! You must wrap it using Orphans.path(...).handler(OrphanCommand)");
            }
            if (instance instanceof Orphans) {
                throw new IllegalArgumentException("You forgot to call .handler(OrphanCommand) in your Orphans.path(...)!");
            }
            if (instance instanceof OrphanRegistry) {
                OrphanRegistry registry = (OrphanRegistry)instance;
                commandClass = registry.handler().getClass();
                instance = registry.handler();
                registered.addAll(this.tree.register(commandClass, instance, registry.paths()));
                continue;
            }
            registered.addAll(this.tree.register(commandClass, instance));
        }
        return registered;
    }

    public void unregister(@NotNull ExecutableCommand<A> execution) {
        if (this.hooks.onCommandUnregistered(execution)) {
            this.tree.unregister(execution);
        }
    }

    public void unregisterAllCommands() {
        this.tree.unregisterIf(this.hooks::onCommandUnregistered);
    }

    public void unregisterIf(@NotNull Predicate<ExecutableCommand<A>> commandPredicate) {
        this.tree.unregisterIf(command -> commandPredicate.test((ExecutableCommand<A>)command) && this.hooks.onCommandUnregistered((ExecutableCommand<A>)command));
    }

    public void dispatch(@NotNull A actor, String input) {
        MutableStringStream stream = StringStream.createMutable(input);
        this.tree.execute(actor, stream);
    }

    public void dispatch(@NotNull A actor, StringStream input) {
        MutableStringStream stream = input.isMutable() ? (MutableStringStream)input : input.toMutableCopy();
        this.tree.execute(actor, stream);
    }

    @NotNull
    public CommandRegistry<A> registry() {
        return this.tree;
    }

    public @Unmodifiable @NotNull Map<Class<? extends Annotation>, Set<AnnotationReplacer<?>>> annotationReplacers() {
        return this.annotationReplacers;
    }

    public @Unmodifiable @NotNull List<CommandCondition<? super A>> commandConditions() {
        return this.commandConditions;
    }

    @NotNull
    public ParameterNamingStrategy parameterNamingStrategy() {
        return this.parameterNamingStrategy;
    }

    @NotNull
    public ParameterTypes<A> parameterTypes() {
        return this.parameterTypes;
    }

    @NotNull
    public SuggestionProviders<A> suggestionProviders() {
        return this.suggestionProviders;
    }

    @NotNull
    public Hooks<A> hooks() {
        return this.hooks;
    }

    public @Unmodifiable @NotNull List<SenderResolver<? super A>> senderResolvers() {
        return this.senderResolvers;
    }

    public @Unmodifiable @NotNull List<ParameterValidator<A, Object>> parameterValidators() {
        return this.validators;
    }

    @NotNull
    public <T> T dependency(@NotNull Class<T> type) {
        Supplier<Object> supplier = this.dependencies.get(type);
        if (supplier == null) {
            throw new IllegalStateException("Cannot find a suitable dependency for type " + type);
        }
        Object value = supplier.get();
        if (value == null) {
            throw new IllegalStateException("Received a null dependency for type " + type);
        }
        return (T)value;
    }

    @ApiStatus.Internal
    public <T> void validate(A actor, T value, ParameterNode<A, T> parameter) {
        for (ParameterValidator<A, Object> validator : this.parameterValidators()) {
            validator.validate(actor, value, parameter, this);
        }
    }

    @NotNull
    public AutoCompleter<A> autoCompleter() {
        return this.autoCompleter;
    }

    public void handleException(@NotNull Throwable throwable, @NotNull ErrorContext<A> errorContext) {
        Preconditions.notNull(throwable, "throwable");
        try {
            if (throwable instanceof SelfHandledException) {
                SelfHandledException she = (SelfHandledException)((Object)throwable);
                she.handle(errorContext);
            }
            if (throwable.getClass().isAnnotationPresent(ThrowableFromCommand.class)) {
                this.exceptionHandler.handleException(throwable, errorContext);
            } else {
                this.dispatcherSettings.stackTraceSanitizer().sanitize(throwable);
                this.exceptionHandler.handleException(new CommandInvocationException(throwable), errorContext);
            }
        } catch (Throwable t) {
            throw new IllegalStateException("The CommandExceptionHandler threw an exception", t);
        }
    }

    @NotNull
    public CommandPermission<A> createPermission(AnnotationList annotations) {
        for (CommandPermission.Factory<A> permissionFactory : this.permissionFactories) {
            CommandPermission<? super A> permission = permissionFactory.create(annotations, this);
            if (permission == null) continue;
            return permission;
        }
        return CommandPermission.alwaysTrue();
    }

    @NotNull
    public MessageSender<? super A, String> messageSender() {
        return this.messageSender;
    }

    @NotNull
    public MessageSender<? super A, String> errorSender() {
        return this.errorSender;
    }

    @NotNull
    public DispatcherSettings<A> dispatcherSettings() {
        return this.dispatcherSettings;
    }

    @Contract(value="_ -> this")
    @NotNull
    public Lamp<A> accept(@NotNull LampVisitor<A> visitor) {
        visitor.visit(this);
        return this;
    }

    public static class Builder<A extends CommandActor> {
        private final ParameterTypes.Builder<A> parameterTypes = ParameterTypes.builder();
        private final SuggestionProviders.Builder<A> suggestionProviders = SuggestionProviders.builder();
        private final Hooks.Builder<A> hooks = Hooks.builder();
        private final List<ParameterValidator<A, Object>> validators = new ArrayList<ParameterValidator<A, Object>>();
        private final List<ResponseHandler.Factory<? super A>> responseHandlers = new ArrayList<ResponseHandler.Factory<? super A>>();
        private final Map<Class<? extends Annotation>, Set<AnnotationReplacer<?>>> annotationReplacers = new LinkedHashMap();
        private final List<SenderResolver<? super A>> senderResolvers = new ArrayList<SenderResolver<? super A>>();
        private final List<CommandCondition<? super A>> conditions = new ArrayList<CommandCondition<? super A>>();
        private final List<CommandPermission.Factory<A>> permissionFactories = new ArrayList<CommandPermission.Factory<A>>();
        private final Map<Class<?>, Supplier<Object>> dependencies = new HashMap();
        private DispatcherSettings.Builder<A> dispatcherSettings = DispatcherSettings.builder();
        private MessageSender<? super A, String> messageSender = CommandActor::sendRawMessage;
        private MessageSender<? super A, String> errorSender = CommandActor::sendRawError;
        private CommandExceptionHandler<A> exceptionHandler = new DefaultExceptionHandler();
        private ParameterNamingStrategy namingStrategy = ParameterNamingStrategy.lowerCaseWithSpace();

        public Builder() {
            this.parameterValidator(Number.class, RangeChecker.INSTANCE);
            this.parameterValidator(String.class, LengthChecker.INSTANCE);
            this.senderResolver(CommandActorSenderResolver.INSTANCE);
            this.responseHandler(SupplierResponseHandler.INSTANCE);
            this.responseHandler(CompletionStageResponseHandler.INSTANCE);
            this.responseHandler(OptionalResponseHandler.INSTANCE);
            this.commandCondition(PermissionConditionChecker.INSTANCE);
            ThreadExecutorCooldownCondition cooldownCondition = new ThreadExecutorCooldownCondition();
            this.commandCondition(cooldownCondition);
            this.parameterTypes().addContextParameterFactoryLast(cooldownCondition);
            this.hooks().onPostCommandExecuted(cooldownCondition);
            this.accept(KotlinFeatureRegistry.INSTANCE);
        }

        @NotNull
        public Builder<A> parameterNamingStrategy(ParameterNamingStrategy namingStrategy) {
            this.namingStrategy = Preconditions.notNull(namingStrategy, "naming strategy");
            return this;
        }

        @NotNull
        public ParameterTypes.Builder<A> parameterTypes() {
            return this.parameterTypes;
        }

        @NotNull
        public Builder<A> parameterTypes(@NotNull Consumer<ParameterTypes.Builder<A>> consumer) {
            Preconditions.notNull(consumer, "consumer");
            consumer.accept(this.parameterTypes);
            return this;
        }

        @NotNull
        public SuggestionProviders.Builder<A> suggestionProviders() {
            return this.suggestionProviders;
        }

        @NotNull
        public Builder<A> suggestionProviders(@NotNull Consumer<SuggestionProviders.Builder<A>> consumer) {
            Preconditions.notNull(consumer, "consumer");
            consumer.accept(this.suggestionProviders);
            return this;
        }

        @NotNull
        public Builder<A> dispatcherSettings(@NotNull Consumer<DispatcherSettings.Builder<A>> consumer) {
            Preconditions.notNull(consumer, "consumer");
            consumer.accept(this.dispatcherSettings);
            return this;
        }

        @NotNull
        public DispatcherSettings.Builder<A> dispatcherSettings() {
            return this.dispatcherSettings;
        }

        @NotNull
        public Builder<A> dispatcherSettings(@NotNull DispatcherSettings<A> settings) {
            Preconditions.notNull(settings, "dispatcher settings");
            this.dispatcherSettings = settings.toBuilder();
            return this;
        }

        @NotNull
        public Hooks.Builder<A> hooks() {
            return this.hooks;
        }

        @NotNull
        public Builder<A> hooks(@NotNull Consumer<Hooks.Builder<A>> consumer) {
            Preconditions.notNull(consumer, "consumer");
            consumer.accept(this.hooks);
            return this;
        }

        public <T extends Annotation> Builder<A> annotationReplacer(@NotNull Class<T> annotationType, @NotNull AnnotationReplacer<T> replacer) {
            Preconditions.notNull(annotationType, "annotation type");
            Preconditions.notNull(replacer, "annotation replacer");
            Classes.checkRetention(annotationType);
            this.annotationReplacers.computeIfAbsent(annotationType, k -> new HashSet()).add(replacer);
            return this;
        }

        public Builder<A> senderResolver(@NotNull SenderResolver<? super A> resolver) {
            Preconditions.notNull(resolver, "sender resolver");
            this.senderResolvers.add(resolver);
            return this;
        }

        public <T> Builder<A> parameterValidator(Class<T> type, @NotNull ParameterValidator<? super A, T> validator) {
            Preconditions.notNull(type, "type");
            Preconditions.notNull(validator, "parameter validator");
            Class wrapped = Classes.wrap(type);
            this.validators.add((actor, value, parameter, lamp) -> {
                if (!wrapped.isAssignableFrom(Classes.wrap(parameter.type()))) {
                    return;
                }
                validator.validate(actor, value, parameter, lamp);
            });
            return this;
        }

        public <T> Builder<A> responseHandler(Class<T> type, @NotNull ResponseHandler<? super A, T> responseHandler) {
            Preconditions.notNull(type, "type");
            return this.responseHandler(ResponseHandler.Factory.forType(type, responseHandler));
        }

        public Builder<A> responseHandler(@NotNull ResponseHandler.Factory<? super A> responseHandler) {
            Preconditions.notNull(responseHandler, "response handler");
            this.responseHandlers.add(responseHandler);
            return this;
        }

        public Builder<A> commandCondition(@NotNull CommandCondition<? super A> condition) {
            Preconditions.notNull(condition, "command condition");
            this.conditions.add(condition);
            return this;
        }

        public Builder<A> permissionFactory(@NotNull CommandPermission.Factory<? super A> factory) {
            Preconditions.notNull(factory, "permission factory");
            this.permissionFactories.add(factory);
            return this;
        }

        public <T extends Annotation> Builder<A> permissionForAnnotation(@NotNull Class<T> annotationType, @NotNull @NotNull Function<@NotNull T, @Nullable CommandPermission<A>> permissionCreator) {
            Preconditions.notNull(annotationType, "annotation type");
            Preconditions.notNull(permissionCreator, "permission creator");
            this.permissionFactories.add(CommandPermission.Factory.forAnnotation(annotationType, permissionCreator));
            return this;
        }

        public Builder<A> exceptionHandler(@NotNull CommandExceptionHandler<A> handler) {
            Preconditions.notNull(handler, "exception handler");
            this.exceptionHandler = handler;
            return this;
        }

        public <T> Builder<A> dependency(Class<T> dependencyType, @NotNull T dependency) {
            Preconditions.notNull(dependencyType, "dependency type");
            Preconditions.notNull(dependency, "dependency");
            this.dependencies.put(dependencyType, () -> dependency);
            return this;
        }

        public <T> Builder<A> dependency(Class<T> dependencyType, @NotNull Supplier<T> dependency) {
            Preconditions.notNull(dependencyType, "dependency type");
            Preconditions.notNull(dependency, "dependency");
            this.dependencies.put(dependencyType, dependency);
            return this;
        }

        public Builder<A> defaultMessageSender(@NotNull MessageSender<? super A, String> messageSender) {
            Preconditions.notNull(messageSender, "message sender");
            this.messageSender = messageSender;
            return this;
        }

        public Builder<A> defaultErrorSender(@NotNull MessageSender<? super A, String> messageSender) {
            Preconditions.notNull(messageSender, "message sender");
            this.errorSender = messageSender;
            return this;
        }

        @Contract(value="_ -> this")
        @NotNull
        public Builder<A> accept(@NotNull LampBuilderVisitor<? super A> visitor) {
            visitor.visit(this);
            return this;
        }

        @Contract(pure=true, value="-> new")
        public Lamp<A> build() {
            return new Lamp(this);
        }
    }
}

