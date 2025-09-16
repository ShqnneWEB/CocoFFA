/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Entity
 */
package hu.geri.libs.revxrsal.commands.bukkit.parameters;

import com.google.common.collect.ForwardingList;
import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.bukkit.actor.BukkitCommandActor;
import hu.geri.libs.revxrsal.commands.bukkit.exception.EmptyEntitySelectorException;
import hu.geri.libs.revxrsal.commands.bukkit.exception.MalformedEntitySelectorException;
import hu.geri.libs.revxrsal.commands.bukkit.parameters.EntitySelector;
import hu.geri.libs.revxrsal.commands.exception.CommandErrorException;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ParameterType;
import hu.geri.libs.revxrsal.commands.stream.MutableStringStream;
import hu.geri.libs.revxrsal.commands.util.Classes;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EntitySelectorParameterTypeFactory
implements ParameterType.Factory<BukkitCommandActor> {
    @Override
    @Nullable
    public <T> ParameterType<BukkitCommandActor, T> create(@NotNull Type parameterType, @NotNull AnnotationList annotations, @NotNull Lamp<BukkitCommandActor> lamp) {
        Class<?> rawType = Classes.getRawType(parameterType);
        if (rawType != EntitySelector.class) {
            return null;
        }
        Class<Entity> entityClass = Classes.getRawType(Classes.getFirstGeneric(parameterType, Entity.class)).asSubclass(Entity.class);
        return new EntitySelectorParameterType(entityClass);
    }

    static final class SelectorList<E extends Entity>
    extends ForwardingList<E>
    implements EntitySelector<E> {
        private final List<E> entities;

        public SelectorList(List<E> entities) {
            this.entities = Preconditions.notNull(entities, "entities list");
        }

        @Override
        protected List<E> delegate() {
            return this.entities;
        }
    }

    static final class EntitySelectorParameterType
    implements ParameterType<BukkitCommandActor, EntitySelector<?>> {
        private final Class<?> entityType;

        public EntitySelectorParameterType(Class<?> entityType) {
            this.entityType = entityType;
        }

        @Override
        public EntitySelector<?> parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
            String selector = input.readString();
            try {
                ArrayList<Entity> c = new ArrayList<Entity>(Bukkit.getServer().selectEntities(context.actor().sender(), selector));
                c.removeIf(obj -> !this.entityType.isInstance(obj));
                if (c.isEmpty()) {
                    throw new EmptyEntitySelectorException(selector);
                }
                return new SelectorList(c);
            } catch (IllegalArgumentException e) {
                throw new MalformedEntitySelectorException(selector, e.getCause().getMessage());
            } catch (NoSuchMethodError e) {
                throw new CommandErrorException("Entity selectors on legacy versions are not supported yet!", new Object[0]);
            }
        }
    }
}

