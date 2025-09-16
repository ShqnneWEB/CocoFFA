/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.util;

import hu.geri.libs.revxrsal.commands.annotation.Command;
import hu.geri.libs.revxrsal.commands.annotation.Subcommand;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandFunction;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import hu.geri.libs.revxrsal.commands.util.Reflections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class CommandPaths {
    private CommandPaths() {
        Preconditions.cannotInstantiate(CommandPaths.class);
    }

    @NotNull
    public static List<String> parseCommandAnnotations(@NotNull Class<?> container, @NotNull CommandFunction function) {
        ArrayList commands = new ArrayList();
        ArrayList subcommands = new ArrayList();
        Command commandAnnotation = function.annotations().require(Command.class, "Method " + function.name() + " does not have a parent command! You might have forgotten one of the following:\n- @Command on the method or class\n- implement OrphanCommand");
        Preconditions.notEmpty(commandAnnotation.value(), "@Command#value() cannot be an empty array!");
        Collections.addAll(commands, commandAnnotation.value());
        ArrayList parentSubcommandAliases = new ArrayList();
        for (Class<?> topClass : Reflections.getTopClasses(container)) {
            AnnotationList annotations = AnnotationList.create(topClass).replaceAnnotations(topClass, function.lamp().annotationReplacers());
            Subcommand ps = annotations.get(Subcommand.class);
            if (ps == null) continue;
            Collections.addAll(parentSubcommandAliases, ps.value());
        }
        Subcommand subcommandAnnotation = function.annotations().get(Subcommand.class);
        if (subcommandAnnotation != null) {
            if (parentSubcommandAliases.isEmpty()) {
                Collections.addAll(subcommands, subcommandAnnotation.value());
            } else {
                for (String parentSubcommandAlias : parentSubcommandAliases) {
                    Arrays.stream(subcommandAnnotation.value()).map(v -> parentSubcommandAlias + ' ' + v).forEach(subcommands::add);
                }
            }
        }
        ArrayList<String> paths = new ArrayList<String>();
        for (String command : commands) {
            if (!subcommands.isEmpty()) {
                for (String subcommand : subcommands) {
                    paths.add(command + ' ' + subcommand);
                }
                continue;
            }
            paths.add(command);
        }
        return paths;
    }
}

