/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.node;

import hu.geri.libs.revxrsal.commands.util.BuiltInNamingStrategies;
import java.lang.reflect.Parameter;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ParameterNamingStrategy {
    public static ParameterNamingStrategy identity() {
        return Parameter::getName;
    }

    public static ParameterNamingStrategy lowerCaseWithSeparator(String separator) {
        return parameter -> BuiltInNamingStrategies.separateCamelCase(parameter.getName(), separator).toLowerCase(Locale.ENGLISH);
    }

    public static ParameterNamingStrategy lowerCaseWithSpace() {
        return ParameterNamingStrategy.lowerCaseWithSeparator(" ");
    }

    public static ParameterNamingStrategy upperCamelCase() {
        return parameter -> BuiltInNamingStrategies.upperCaseFirstLetter(parameter.getName());
    }

    public static ParameterNamingStrategy upperCamelCaseWithSpace() {
        return ParameterNamingStrategy.upperCamelCaseWithSeparator(" ");
    }

    public static ParameterNamingStrategy upperCamelCaseWithSeparator(String separator) {
        return parameter -> BuiltInNamingStrategies.upperCaseFirstLetter(BuiltInNamingStrategies.separateCamelCase(parameter.getName(), separator));
    }

    @NotNull
    public String getName(@NotNull Parameter var1);
}

