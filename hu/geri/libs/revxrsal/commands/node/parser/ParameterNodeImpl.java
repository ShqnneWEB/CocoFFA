/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node.parser;

import hu.geri.libs.revxrsal.commands.annotation.Default;
import hu.geri.libs.revxrsal.commands.annotation.Description;
import hu.geri.libs.revxrsal.commands.annotation.Flag;
import hu.geri.libs.revxrsal.commands.annotation.Length;
import hu.geri.libs.revxrsal.commands.annotation.Sized;
import hu.geri.libs.revxrsal.commands.annotation.Switch;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CommandParameter;
import hu.geri.libs.revxrsal.commands.command.CommandPermission;
import hu.geri.libs.revxrsal.commands.exception.MissingArgumentException;
import hu.geri.libs.revxrsal.commands.exception.NoPermissionException;
import hu.geri.libs.revxrsal.commands.node.CommandAction;
import hu.geri.libs.revxrsal.commands.node.CommandNode;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.node.parser.BaseCommandNode;
import hu.geri.libs.revxrsal.commands.node.parser.LiteralNodeImpl;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.reflect.ktx.KotlinConstants;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStreamImpl;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.util.Collection;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ParameterNodeImpl<A extends CommandActor, T>
extends BaseCommandNode<A>
implements ParameterNode<A, T> {
    @NotNull
    private final ParameterType<A, T> type;
    @NotNull
    private final SuggestionProvider<A> suggestions;
    @NotNull
    private final CommandParameter parameter;
    @NotNull
    private final CommandPermission<A> permission;
    private final boolean isOptional;
    @Nullable
    private final Switch switchAnn;
    @Nullable
    private final Flag flagAnn;

    public ParameterNodeImpl(@NotNull String name, @Nullable CommandAction<A> action, boolean isLast, @NotNull ParameterType<A, T> type, @NotNull SuggestionProvider<A> suggestions, @NotNull CommandParameter parameter, @NotNull CommandPermission<A> permission, boolean isOptional) {
        super(name, action, isLast);
        this.type = type;
        this.suggestions = suggestions;
        this.parameter = parameter;
        this.permission = permission;
        this.isOptional = isOptional;
        this.switchAnn = parameter.getAnnotation(Switch.class);
        this.flagAnn = parameter.getAnnotation(Flag.class);
        if (this.isSwitch() && Classes.wrap(this.type()) != Boolean.class) {
            throw new IllegalArgumentException("@Switch can only be used on boolean types!");
        }
        if (this.isSwitch() && this.isFlag()) {
            throw new IllegalArgumentException("A parameter cannot have @Switch and @Flag at the same time!");
        }
    }

    @Nullable
    private static String getDefaultValue(AnnotationList annotations) {
        String defaultValue = annotations.map(Default.class, Default::value);
        if (defaultValue == null) {
            Sized sized = annotations.get(Sized.class);
            if (sized != null) {
                return sized.min() == 0 ? "" : null;
            }
            Length length = annotations.get(Length.class);
            if (length != null) {
                return length.min() == 0 ? "" : null;
            }
        }
        return defaultValue;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public T parse(MutableStringStream input, ExecutionContext<A> context) {
        this.checkForPermission(context);
        if (!input.hasFinished()) return this.type.parse(input, context);
        if (!this.isOptional()) throw new MissingArgumentException(this, context.command());
        String defaultValue = ParameterNodeImpl.getDefaultValue(this.parameter.annotations());
        if (defaultValue != null) {
            ((MutableStringStreamImpl)input).extend(defaultValue);
            return this.type.parse(input, context);
        } else {
            if (!KotlinConstants.isKotlinClass(context.command().function().method().getDeclaringClass())) return (T)KotlinConstants.defaultPrimitiveValue(this.parameter.type());
            return null;
        }
    }

    private void checkForPermission(ExecutionContext<A> context) {
        if (!this.permission.isExecutableBy(context.actor())) {
            throw new NoPermissionException(this);
        }
    }

    @Override
    public int compareTo(@NotNull CommandNode<A> o) {
        if (o instanceof LiteralNodeImpl) {
            return 1;
        }
        ParameterNodeImpl n = (ParameterNodeImpl)o;
        if (this.isOptional && !n.isOptional) {
            return 1;
        }
        if (n.isOptional && !this.isOptional) {
            return -1;
        }
        int compare = this.type.parsePriority().comparator().compare(this.parameterType(), n.parameterType());
        if (compare != 0) {
            return compare;
        }
        compare = n.parameterType().parsePriority().comparator().compare(n.parameterType(), this.parameterType());
        return compare == 0 ? 0 : -compare;
    }

    @Override
    @NotNull
    public AnnotationList annotations() {
        return this.parameter.annotations();
    }

    @Override
    public boolean isOptional() {
        return this.isOptional;
    }

    @Override
    @NotNull
    public ParameterType<A, T> parameterType() {
        return this.type;
    }

    @Override
    @NotNull
    public SuggestionProvider<A> suggestions() {
        return this.suggestions;
    }

    @Override
    public boolean isGreedy() {
        return this.parameterType().isGreedy();
    }

    @Override
    @NotNull
    public CommandParameter parameter() {
        return this.parameter;
    }

    @Override
    @NotNull
    public CommandPermission<A> permission() {
        return this.permission;
    }

    @Override
    @NotNull
    public Collection<String> complete(@NotNull ExecutionContext<A> context) {
        return this.suggestions.getSuggestions(context);
    }

    @Override
    @NotNull
    public <L> ParameterNode<A, L> requireParameterNode() {
        return this;
    }

    @Override
    @Nullable
    public String description() {
        return this.annotations().map(Description.class, Description::value);
    }

    @Override
    @Nullable
    public String switchName() {
        if (this.switchAnn != null) {
            return this.switchAnn.value().isEmpty() ? this.name() : this.switchAnn.value();
        }
        return null;
    }

    @Override
    @Nullable
    public String flagName() {
        if (this.flagAnn != null) {
            return this.flagAnn.value().isEmpty() ? this.name() : this.flagAnn.value();
        }
        return null;
    }

    @Override
    public Character shorthand() {
        char shorthand;
        if (this.isFlag()) {
            shorthand = this.flagAnn.shorthand();
            if (shorthand == '\u0000') {
                shorthand = this.flagName().charAt(0);
            }
        } else if (this.isSwitch()) {
            shorthand = this.switchAnn.shorthand();
            if (shorthand == '\u0000') {
                shorthand = this.switchName().charAt(0);
            }
        } else {
            return null;
        }
        return Character.valueOf(shorthand);
    }

    @Override
    @NotNull
    public String representation() {
        if (this.isFlag() || this.isSwitch()) {
            char shorthand = Objects.requireNonNull(this.shorthand(), "shorthand() is null for a flag or switch. This is not supposed to happen!").charValue();
            if (this.isSwitch()) {
                return "[--" + this.switchName() + " | -" + shorthand + "]";
            }
            if (this.isFlag()) {
                if (this.isOptional()) {
                    return "[--" + this.flagName() + " <" + this.name() + "> | -" + shorthand + " <" + this.name() + ">]";
                }
                return "<--" + this.flagName() + " <" + this.name() + "> | -" + shorthand + " <" + this.name() + ">]";
            }
        }
        return this.isRequired() ? "<" + this.name() + ">" : "[" + this.name() + "]";
    }

    @Override
    public boolean isSwitch() {
        return this.switchAnn != null;
    }

    @Override
    public boolean isFlag() {
        return this.flagAnn != null;
    }

    public String toString() {
        return "ParameterNode(name=" + this.name() + ')';
    }
}

