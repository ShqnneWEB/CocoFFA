/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.command;

import hu.geri.libs.revxrsal.commands.Lamp;
import hu.geri.libs.revxrsal.commands.annotation.Cooldown;
import hu.geri.libs.revxrsal.commands.annotation.list.AnnotationList;
import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.command.CooldownHandle;
import hu.geri.libs.revxrsal.commands.command.ExecutableCommand;
import hu.geri.libs.revxrsal.commands.exception.CooldownException;
import hu.geri.libs.revxrsal.commands.hook.PostCommandExecutedHook;
import hu.geri.libs.revxrsal.commands.node.ExecutionContext;
import hu.geri.libs.revxrsal.commands.parameter.ContextParameter;
import hu.geri.libs.revxrsal.commands.process.CommandCondition;
import hu.geri.libs.revxrsal.commands.util.Classes;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@ApiStatus.Internal
public final class ThreadExecutorCooldownCondition
implements CommandCondition<CommandActor>,
PostCommandExecutedHook<CommandActor>,
ContextParameter.Factory<CommandActor> {
    private static final ScheduledExecutorService COOLDOWN_POOL = Executors.newSingleThreadScheduledExecutor();
    private final Map<UUID, Map<Integer, Long>> cooldowns = new ConcurrentHashMap<UUID, Map<Integer, Long>>();

    @Override
    public void onPostExecuted(@NotNull ExecutableCommand<CommandActor> command, @NotNull ExecutionContext<CommandActor> context) {
        Cooldown cooldown = command.annotations().get(Cooldown.class);
        if (cooldown == null || cooldown.value() == 0L) {
            return;
        }
        Map spans = this.cooldowns.computeIfAbsent(context.actor().uniqueId(), u -> new ConcurrentHashMap());
        spans.put(command.hashCode(), System.currentTimeMillis());
        COOLDOWN_POOL.schedule(() -> (Long)spans.remove(command.hashCode()), cooldown.value(), cooldown.unit());
    }

    @Override
    public void test(@NotNull ExecutionContext<CommandActor> context) {
        @Nullable Cooldown cooldown = context.command().annotations().get(Cooldown.class);
        if (cooldown == null || cooldown.value() == 0L) {
            return;
        }
        UUID uuid = context.actor().uniqueId();
        @Nullable Map<Integer, Long> spans = this.cooldowns.get(uuid);
        if (spans == null) {
            return;
        }
        @Nullable Long created = spans.get(context.command().hashCode());
        if (created == null) {
            return;
        }
        long passed = System.currentTimeMillis() - created;
        long left = cooldown.unit().toMillis(cooldown.value()) - passed;
        if (left > 0L && left < 1000L) {
            left = 1000L;
        }
        throw new CooldownException(left);
    }

    @Override
    @Nullable
    public <T> ContextParameter<CommandActor, T> create(@NotNull Type parameterType, @NotNull AnnotationList annotations, @NotNull Lamp<CommandActor> lamp) {
        Class<?> rawType = Classes.getRawType(parameterType);
        if (!CooldownHandle.class.isAssignableFrom(rawType)) {
            return null;
        }
        @Nullable Cooldown cooldown = annotations.get(Cooldown.class);
        return (parameter, context) -> {
            if (context.command().annotations().contains(Cooldown.class)) {
                throw new IllegalArgumentException("Cannot have both @Cooldown and CooldownHandle in one command. Either put @Cooldown on the CooldownHandle parameter (@Cooldown(...) CooldownHandle handle), or remove @Cooldown entirely.");
            }
            return new BasicHandle(context.actor().uniqueId(), context.command().hashCode(), cooldown);
        };
    }

    private class BasicHandle
    implements CooldownHandle {
        private final UUID actor;
        private final int hashCode;
        @Nullable
        private final Cooldown cooldown;

        public BasicHandle(UUID actor, @Nullable int hashCode, Cooldown cooldown) {
            this.actor = actor;
            this.hashCode = hashCode;
            this.cooldown = cooldown;
        }

        @Override
        @NotNull
        public CooldownHandle withCooldown(long cooldownValue, @NotNull TimeUnit unit) {
            return new BasicHandle(this.actor, this.hashCode, new DynamicCooldown(cooldownValue, unit));
        }

        @Override
        public boolean isOnCooldown() {
            @Nullable Map spans = (Map)ThreadExecutorCooldownCondition.this.cooldowns.get(this.actor);
            if (spans == null) {
                return false;
            }
            @Nullable Long created = (Long)spans.get(this.hashCode);
            return created != null;
        }

        @Override
        public long elapsedMillis() {
            @Nullable Map spans = (Map)ThreadExecutorCooldownCondition.this.cooldowns.get(this.actor);
            if (spans == null) {
                return 0L;
            }
            @Nullable Long created = (Long)spans.get(this.hashCode);
            if (created == null) {
                return 0L;
            }
            return System.currentTimeMillis() - created;
        }

        @Override
        public void cooldown() {
            if (this.cooldown == null) {
                throw new IllegalArgumentException("cooldown() can only be used if the parameter has @Cooldown on it, otherwise use cooldown(duration, unit) or other overloads.");
            }
            this.cooldown(this.cooldown.value(), this.cooldown.unit());
        }

        @Override
        public void requireNotOnCooldown() {
            if (this.cooldown == null) {
                throw new IllegalArgumentException("requireNotOnCooldown() can only be used if the parameter has @Cooldown on it, otherwise use requireNotOnCooldown(duration, unit) or other overloads.");
            }
            this.requireNotOnCooldown(this.cooldown.value(), this.cooldown.unit());
        }

        @Override
        public void requireNotOnCooldown(long cooldownValue, @NotNull TimeUnit cooldownUnit) {
            long elapsed = this.elapsedMillis();
            if (elapsed == 0L) {
                return;
            }
            long left = cooldownUnit.toMillis(cooldownValue) - elapsed;
            if (left > 0L && left < 1000L) {
                left = 1000L;
            }
            throw new CooldownException(left);
        }

        @Override
        public void removeCooldown() {
            @Nullable Map spans = (Map)ThreadExecutorCooldownCondition.this.cooldowns.get(this.actor);
            if (spans == null) {
                return;
            }
            spans.remove(this.hashCode);
        }

        @Override
        public void cooldown(@Range(from=1L, to=0x7FFFFFFFFFFFFFFFL) long duration, @NotNull TimeUnit unit) {
            Map spans = ThreadExecutorCooldownCondition.this.cooldowns.computeIfAbsent(this.actor, u -> new ConcurrentHashMap());
            spans.put(this.hashCode, System.currentTimeMillis());
            COOLDOWN_POOL.schedule(() -> (Long)spans.remove(this.hashCode), duration, unit);
        }

        @Override
        public long remainingTimeMillis() {
            if (this.cooldown == null) {
                throw new IllegalArgumentException("remainingTimeMillis() can only be used if the parameter has @Cooldown on it, otherwise use remainingTimeMillis(duration, unit) or other overloads.");
            }
            return this.remainingTime(this.cooldown.value(), this.cooldown.unit(), TimeUnit.MILLISECONDS);
        }

        @Override
        public long remainingTime(@NotNull TimeUnit outputUnit) {
            return outputUnit.convert(this.remainingTimeMillis(), TimeUnit.MILLISECONDS);
        }

        private class DynamicCooldown
        implements Cooldown {
            private final long value;
            private final TimeUnit unit;

            public DynamicCooldown(long value, TimeUnit unit) {
                this.value = value;
                this.unit = unit;
            }

            @Override
            public long value() {
                return this.value;
            }

            @Override
            public TimeUnit unit() {
                return this.unit;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Cooldown.class;
            }
        }
    }
}

