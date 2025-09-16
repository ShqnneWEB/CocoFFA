/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.autocomplete;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.autocomplete.SuggestionProvider;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.lang.reflect.Type;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ClassSuggestionProviderFactory<A extends CommandActor>
implements SuggestionProvider.Factory<A> {
    private final Class<?> type;
    private final SuggestionProvider<A> provider;
    private final boolean allowSubclasses;

    ClassSuggestionProviderFactory(Class<?> type, SuggestionProvider<A> provider, boolean allowSubclasses) {
        this.type = Classes.wrap(type);
        this.provider = provider;
        this.allowSubclasses = allowSubclasses;
    }

    @Override
    @Nullable
    public SuggestionProvider<A> create(@NotNull Type parameterType, @NotNull AnnotationList annotations, @NotNull Lamp<A> lamp) {
        Class<?> pType = Classes.wrap(Classes.getRawType(parameterType));
        if (this.allowSubclasses && this.type.isAssignableFrom(pType)) {
            return this.provider;
        }
        if (this.type == pType) {
            return this.provider;
        }
        return null;
    }

    public Class<?> type() {
        return this.type;
    }

    public SuggestionProvider<A> provider() {
        return this.provider;
    }

    public boolean allowSubclasses() {
        return this.allowSubclasses;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        ClassSuggestionProviderFactory that = (ClassSuggestionProviderFactory)obj;
        return Objects.equals(this.type, that.type) && Objects.equals(this.provider, that.provider) && this.allowSubclasses == that.allowSubclasses;
    }

    public int hashCode() {
        return Objects.hash(this.type, this.provider, this.allowSubclasses);
    }

    public String toString() {
        return "ClassSuggestionProviderFactory[type=" + this.type + ", provider=" + this.provider + ", allowSubclasses=" + this.allowSubclasses + ']';
    }
}

