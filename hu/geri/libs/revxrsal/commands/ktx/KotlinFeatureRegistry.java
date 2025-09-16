/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.ktx;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.LampBuilderVisitor;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.ktx.SuspendFunctionsSupport;
import hu.geri.libs.revxrsal.commands.reflect.ktx.KotlinConstants;
import org.jetbrains.annotations.NotNull;

public enum KotlinFeatureRegistry implements LampBuilderVisitor<CommandActor>
{
    INSTANCE;


    @Override
    public void visit(@NotNull Lamp.Builder<CommandActor> builder) {
        if (KotlinConstants.continuation() != null) {
            builder.accept(SuspendFunctionsSupport.INSTANCE);
        }
    }
}

