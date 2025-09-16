/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.command.ProxiedCommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.plugin.PluginManager
 */
package net.kyori.adventure.platform.bukkit;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.graph.MutableGraph;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.bukkit.BukkitAudience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.platform.bukkit.MinecraftReflection;
import net.kyori.adventure.platform.facet.FacetAudienceProvider;
import net.kyori.adventure.platform.facet.Knob;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.renderer.ComponentRenderer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class BukkitAudiencesImpl
extends FacetAudienceProvider<CommandSender, BukkitAudience>
implements BukkitAudiences,
Listener {
    private static final Map<String, BukkitAudiences> INSTANCES;
    private final Plugin plugin;

    static Builder builder(@NotNull Plugin plugin) {
        return new Builder(plugin);
    }

    static BukkitAudiences instanceFor(@NotNull Plugin plugin) {
        return BukkitAudiencesImpl.builder(plugin).build();
    }

    BukkitAudiencesImpl(@NotNull Plugin plugin, @NotNull ComponentRenderer<Pointered> componentRenderer) {
        super(componentRenderer);
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        ConsoleCommandSender console = this.plugin.getServer().getConsoleSender();
        this.addViewer(console);
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            this.addViewer(player);
        }
        this.registerEvent(PlayerJoinEvent.class, EventPriority.LOWEST, event -> this.addViewer(event.getPlayer()));
        this.registerEvent(PlayerQuitEvent.class, EventPriority.MONITOR, event -> this.removeViewer(event.getPlayer()));
        this.registerLocaleEvent(EventPriority.MONITOR, (viewer, locale) -> {
            @Nullable BukkitAudience audience = (BukkitAudience)this.viewers.get(viewer);
            if (audience != null) {
                audience.locale((Locale)locale);
            }
        });
    }

    @Override
    @NotNull
    public Audience sender(@NotNull CommandSender sender) {
        if (sender instanceof Player) {
            return this.player((Player)sender);
        }
        if (sender instanceof ConsoleCommandSender) {
            return this.console();
        }
        if (sender instanceof ProxiedCommandSender) {
            return this.sender(((ProxiedCommandSender)sender).getCallee());
        }
        if (sender instanceof Entity || sender instanceof Block) {
            return Audience.empty();
        }
        return this.createAudience(Collections.singletonList(sender));
    }

    @Override
    @NotNull
    public Audience player(@NotNull Player player) {
        return this.player(player.getUniqueId());
    }

    @Override
    @NotNull
    protected BukkitAudience createAudience(@NotNull Collection<CommandSender> viewers) {
        return new BukkitAudience(this.plugin, this, viewers);
    }

    @Override
    public void close() {
        INSTANCES.remove(this.plugin.getName());
        super.close();
    }

    @Override
    @NotNull
    public ComponentFlattener flattener() {
        return BukkitComponentSerializer.FLATTENER;
    }

    private <T extends Event> void registerEvent(@NotNull Class<T> type, @NotNull EventPriority priority, @NotNull Consumer<T> callback) {
        Objects.requireNonNull(callback, "callback");
        this.plugin.getServer().getPluginManager().registerEvent(type, (Listener)this, priority, (listener, event) -> callback.accept(event), this.plugin, true);
    }

    private void registerLocaleEvent(EventPriority priority, @NotNull BiConsumer<Player, Locale> callback) {
        MethodHandle getMethod;
        Class<?> eventClass = MinecraftReflection.findClass("org.bukkit.event.player.PlayerLocaleChangeEvent");
        if (eventClass == null) {
            eventClass = MinecraftReflection.findClass("com.destroystokyo.paper.event.player.PlayerLocaleChangeEvent");
        }
        if ((getMethod = MinecraftReflection.findMethod(eventClass, "getLocale", String.class, new Class[0])) == null) {
            getMethod = MinecraftReflection.findMethod(eventClass, "getNewLocale", String.class, new Class[0]);
        }
        if (getMethod != null && PlayerEvent.class.isAssignableFrom(eventClass)) {
            Class<?> localeEvent = eventClass;
            MethodHandle getLocale = getMethod;
            this.registerEvent(localeEvent, priority, event -> {
                String locale;
                Player player = event.getPlayer();
                try {
                    locale = getLocale.invoke((PlayerEvent)event);
                } catch (Throwable error) {
                    Knob.logError(error, "Failed to accept %s: %s", localeEvent.getName(), player);
                    return;
                }
                callback.accept(player, BukkitAudiencesImpl.toLocale(locale));
            });
        }
    }

    @NotNull
    private static Locale toLocale(@Nullable String string) {
        Locale locale;
        if (string != null && (locale = Translator.parseLocale(string)) != null) {
            return locale;
        }
        return Locale.US;
    }

    static {
        Knob.OUT = message -> Bukkit.getLogger().log(Level.INFO, (String)message);
        Knob.ERR = (message, error) -> Bukkit.getLogger().log(Level.WARNING, (String)message, (Throwable)error);
        INSTANCES = Collections.synchronizedMap(new HashMap(4));
    }

    static final class Builder
    implements BukkitAudiences.Builder {
        @NotNull
        private final Plugin plugin;
        private ComponentRenderer<Pointered> componentRenderer;

        Builder(@NotNull Plugin plugin) {
            this.plugin = Objects.requireNonNull(plugin, "plugin");
            this.componentRenderer(ptr -> ptr.getOrDefault(Identity.LOCALE, DEFAULT_LOCALE), GlobalTranslator.renderer());
        }

        @Override
        @NotNull
        public Builder componentRenderer(@NotNull ComponentRenderer<Pointered> componentRenderer) {
            this.componentRenderer = Objects.requireNonNull(componentRenderer, "component renderer");
            return this;
        }

        @Override
        public @NotNull BukkitAudiences.Builder partition(@NotNull Function<Pointered, ?> partitionFunction) {
            Objects.requireNonNull(partitionFunction, "partitionFunction");
            return this;
        }

        @Override
        @NotNull
        public BukkitAudiences build() {
            return INSTANCES.computeIfAbsent(this.plugin.getName(), name -> {
                this.softDepend("ViaVersion");
                return new BukkitAudiencesImpl(this.plugin, this.componentRenderer);
            });
        }

        private void softDepend(@NotNull String pluginName) {
            PluginDescriptionFile file = this.plugin.getDescription();
            if (file.getName().equals(pluginName)) {
                return;
            }
            try {
                Field softDepend = MinecraftReflection.needField(file.getClass(), "softDepend");
                List dependencies = (List)softDepend.get(file);
                if (!dependencies.contains(pluginName)) {
                    ImmutableCollection newList = ((ImmutableList.Builder)((ImmutableList.Builder)ImmutableList.builder().addAll((Iterable)dependencies)).add(pluginName)).build();
                    softDepend.set(file, newList);
                }
            } catch (Throwable error) {
                Knob.logError(error, "Failed to inject softDepend in plugin.yml: %s %s", this.plugin, pluginName);
            }
            try {
                PluginManager manager = this.plugin.getServer().getPluginManager();
                Field dependencyGraphField = MinecraftReflection.needField(manager.getClass(), "dependencyGraph");
                MutableGraph graph = (MutableGraph)dependencyGraphField.get(manager);
                graph.putEdge(file.getName(), pluginName);
            } catch (Throwable throwable) {
                // empty catch block
            }
        }
    }
}

