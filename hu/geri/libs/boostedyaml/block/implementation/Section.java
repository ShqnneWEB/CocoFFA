/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.block.implementation;

import hu.geri.libs.boostedyaml.YamlDocument;
import hu.geri.libs.boostedyaml.block.Block;
import hu.geri.libs.boostedyaml.block.implementation.TerminatedBlock;
import hu.geri.libs.boostedyaml.engine.ExtendedConstructor;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.NodeTuple;
import hu.geri.libs.boostedyaml.route.Route;
import hu.geri.libs.boostedyaml.settings.general.GeneralSettings;
import hu.geri.libs.boostedyaml.utils.conversion.ListConversions;
import hu.geri.libs.boostedyaml.utils.conversion.PrimitiveConversions;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Section
extends Block<Map<Object, Block<?>>> {
    private YamlDocument root;
    private Section defaults = null;
    private Section parent;
    private Object name;
    private Route route;

    public Section(@NotNull YamlDocument root, @Nullable Section parent, @NotNull Route route, @Nullable Node keyNode, @NotNull MappingNode valueNode, @NotNull ExtendedConstructor constructor) {
        super(keyNode, valueNode, root.getGeneralSettings().getDefaultMap());
        this.root = root;
        this.parent = parent;
        this.name = this.adaptKey(route.get(route.length() - 1));
        this.route = route;
        this.resetDefaults();
        this.init(root, keyNode, valueNode, constructor);
    }

    public Section(@NotNull YamlDocument root, @Nullable Section parent, @NotNull Route route, @Nullable Block<?> previous, @NotNull Map<?, ?> mappings) {
        super(previous, root.getGeneralSettings().getDefaultMap());
        this.root = root;
        this.parent = parent;
        this.name = this.adaptKey(route.get(route.length() - 1));
        this.route = route;
        this.resetDefaults();
        for (Map.Entry<?, ?> entry : mappings.entrySet()) {
            Object key = this.adaptKey(entry.getKey());
            Object value = entry.getValue();
            ((Map)this.getStoredValue()).put(key, value instanceof Map ? new Section(root, this, route.add(key), null, (Map)value) : new TerminatedBlock(null, value));
        }
    }

    protected Section(@NotNull Map<Object, Block<?>> defaultMap) {
        super(defaultMap);
        this.root = null;
        this.parent = null;
        this.name = null;
        this.route = null;
        this.defaults = null;
    }

    protected void initEmpty(@NotNull YamlDocument root) {
        if (!root.isRoot()) {
            throw new IllegalStateException("Cannot init non-root section!");
        }
        super.init(null, null);
        this.root = root;
        this.resetDefaults();
    }

    protected void init(@NotNull YamlDocument root, @Nullable Node keyNode, @NotNull MappingNode valueNode, @NotNull ExtendedConstructor constructor) {
        if (root == this && keyNode != null) {
            throw new IllegalArgumentException("Root sections cannot have a key node!");
        }
        super.init(keyNode, valueNode);
        this.root = root;
        this.resetDefaults();
        for (NodeTuple tuple : valueNode.getValue()) {
            Object key = this.adaptKey(constructor.getConstructed(tuple.getKeyNode()));
            Object value = constructor.getConstructed(tuple.getValueNode());
            ((Map)this.getStoredValue()).put(key, value instanceof Map ? new Section(root, this, this.getSubRoute(key), tuple.getKeyNode(), (MappingNode)tuple.getValueNode(), constructor) : new TerminatedBlock(tuple.getKeyNode(), tuple.getValueNode(), value));
        }
    }

    public boolean isEmpty(boolean deep) {
        if (((Map)this.getStoredValue()).isEmpty()) {
            return true;
        }
        if (!deep) {
            return false;
        }
        for (Block value : ((Map)this.getStoredValue()).values()) {
            if (!(value instanceof TerminatedBlock) && (!(value instanceof Section) || ((Section)value).isEmpty(true))) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isSection() {
        return true;
    }

    public boolean isRoot() {
        return false;
    }

    @NotNull
    public YamlDocument getRoot() {
        return this.root;
    }

    public Section getParent() {
        return this.parent;
    }

    @Nullable
    public Object getName() {
        return this.name;
    }

    @Nullable
    public String getNameAsString() {
        return this.name == null ? null : this.name.toString();
    }

    @NotNull
    public Route getNameAsRoute() {
        return Route.from(this.name);
    }

    @Nullable
    public Route getRoute() {
        return this.route;
    }

    @Nullable
    public String getRouteAsString() {
        return this.route == null ? null : this.route.join(this.root.getGeneralSettings().getRouteSeparator());
    }

    @NotNull
    public Route getSubRoute(@NotNull Object key) {
        return Route.addTo(this.route, key);
    }

    @Nullable
    public Section getDefaults() {
        return this.defaults;
    }

    public boolean hasDefaults() {
        return this.defaults != null;
    }

    private void adapt(@NotNull YamlDocument root, @Nullable Section parent, @NotNull Route route) {
        if (this.parent != null && this.parent != parent && ((Map)this.parent.getStoredValue()).get(this.name) == this) {
            this.parent.removeInternal(this.parent, this.name);
        }
        this.name = route.get(route.length() - 1);
        this.parent = parent;
        this.adapt(root, route);
    }

    private void adapt(@NotNull YamlDocument root, @NotNull Route route) {
        this.root = root;
        this.route = route;
        this.resetDefaults();
        for (Map.Entry entry : ((Map)this.getStoredValue()).entrySet()) {
            if (!(entry.getValue() instanceof Section)) continue;
            ((Section)entry.getValue()).adapt(root, route.add(entry.getKey()));
        }
    }

    @NotNull
    public Object adaptKey(@NotNull Object key) {
        Objects.requireNonNull(key, "Sections cannot contain null keys!");
        return this.root.getGeneralSettings().getKeyFormat() == GeneralSettings.KeyFormat.OBJECT ? key : key.toString();
    }

    private void resetDefaults() {
        this.defaults = this.isRoot() ? this.root.getDefaults() : (this.parent == null || this.parent.defaults == null ? null : this.parent.defaults.getSection(Route.fromSingleKey(this.name), null));
    }

    private boolean canUseDefaults() {
        return this.hasDefaults() && this.root.getGeneralSettings().isUseDefaults();
    }

    @NotNull
    public Set<Route> getRoutes(boolean deep) {
        Set<Route> keys = this.root.getGeneralSettings().getDefaultSet();
        if (this.canUseDefaults()) {
            keys.addAll(this.defaults.getRoutes(deep));
        }
        this.addData((route, entry) -> keys.add((Route)route), null, deep);
        return keys;
    }

    @NotNull
    public Set<String> getRoutesAsStrings(boolean deep) {
        Set<String> keys = this.root.getGeneralSettings().getDefaultSet();
        if (this.canUseDefaults()) {
            keys.addAll(this.defaults.getRoutesAsStrings(deep));
        }
        this.addData((route, entry) -> keys.add((String)route), new StringBuilder(), this.root.getGeneralSettings().getRouteSeparator(), deep);
        return keys;
    }

    @NotNull
    public Set<Object> getKeys() {
        Set<Object> keys = this.root.getGeneralSettings().getDefaultSet(((Map)this.getStoredValue()).size());
        if (this.canUseDefaults()) {
            keys.addAll(this.defaults.getKeys());
        }
        keys.addAll(((Map)this.getStoredValue()).keySet());
        return keys;
    }

    @NotNull
    public Map<Route, Object> getRouteMappedValues(boolean deep) {
        Map<Route, Object> values = this.root.getGeneralSettings().getDefaultMap();
        if (this.canUseDefaults()) {
            values.putAll(this.defaults.getRouteMappedValues(deep));
        }
        this.addData((route, entry) -> values.put((Route)route, entry.getValue() instanceof Section ? entry.getValue() : ((Block)entry.getValue()).getStoredValue()), null, deep);
        return values;
    }

    @NotNull
    public Map<String, Object> getStringRouteMappedValues(boolean deep) {
        Map<String, Object> values = this.root.getGeneralSettings().getDefaultMap();
        if (this.canUseDefaults()) {
            values.putAll(this.defaults.getStringRouteMappedValues(deep));
        }
        this.addData((route, entry) -> values.put((String)route, entry.getValue() instanceof Section ? entry.getValue() : ((Block)entry.getValue()).getStoredValue()), new StringBuilder(), this.root.getGeneralSettings().getRouteSeparator(), deep);
        return values;
    }

    @NotNull
    public Map<Route, Block<?>> getRouteMappedBlocks(boolean deep) {
        Map<Route, Block<?>> blocks = this.root.getGeneralSettings().getDefaultMap();
        if (this.canUseDefaults()) {
            blocks.putAll(this.defaults.getRouteMappedBlocks(deep));
        }
        this.addData((route, entry) -> {
            Block cfr_ignored_0 = (Block)blocks.put((Route)route, (Block<?>)entry.getValue());
        }, null, deep);
        return blocks;
    }

    @NotNull
    public Map<String, Block<?>> getStringRouteMappedBlocks(boolean deep) {
        Map<String, Block<?>> blocks = this.root.getGeneralSettings().getDefaultMap();
        if (this.canUseDefaults()) {
            blocks.putAll(this.defaults.getStringRouteMappedBlocks(deep));
        }
        this.addData((route, entry) -> {
            Block cfr_ignored_0 = (Block)blocks.put((String)route, (Block<?>)entry.getValue());
        }, new StringBuilder(), this.root.getGeneralSettings().getRouteSeparator(), deep);
        return blocks;
    }

    private void addData(@NotNull BiConsumer<Route, Map.Entry<?, Block<?>>> consumer, @Nullable Route current, boolean deep) {
        for (Map.Entry entry : ((Map)this.getStoredValue()).entrySet()) {
            Route entryRoute = Route.addTo(current, entry.getKey());
            consumer.accept(entryRoute, entry);
            if (!deep || !(entry.getValue() instanceof Section)) continue;
            ((Section)entry.getValue()).addData(consumer, entryRoute, true);
        }
    }

    private void addData(@NotNull BiConsumer<String, Map.Entry<?, Block<?>>> consumer, @NotNull StringBuilder routeBuilder, char separator, boolean deep) {
        for (Map.Entry entry : ((Map)this.getStoredValue()).entrySet()) {
            int length = routeBuilder.length();
            if (length > 0) {
                routeBuilder.append(separator);
            }
            consumer.accept(routeBuilder.append(entry.getKey().toString()).toString(), entry);
            if (deep && entry.getValue() instanceof Section) {
                ((Section)entry.getValue()).addData(consumer, routeBuilder, separator, true);
            }
            routeBuilder.setLength(length);
        }
    }

    public boolean contains(@NotNull Route route) {
        return this.getBlock(route) != null;
    }

    public boolean contains(@NotNull String route) {
        return this.getBlock(route) != null;
    }

    public Section createSection(@NotNull Route route) {
        Section current = this;
        for (int i = 0; i < route.length(); ++i) {
            current = current.createSectionInternal(route.get(i), null);
        }
        return current;
    }

    public Section createSection(@NotNull String route) {
        int nextSeparator;
        int lastSeparator = 0;
        Section section = this;
        while ((nextSeparator = route.indexOf(this.root.getGeneralSettings().getRouteSeparator(), lastSeparator)) != -1) {
            section = section.createSectionInternal(route.substring(lastSeparator, nextSeparator), null);
            lastSeparator = nextSeparator + 1;
        }
        return section.createSectionInternal(route.substring(lastSeparator), null);
    }

    private Section createSectionInternal(@NotNull Object key, @Nullable Block<?> previous) {
        Object adapted = this.adaptKey(key);
        return this.getOptionalSection(Route.from(adapted)).orElseGet(() -> {
            Section section = new Section(this.root, this, this.getSubRoute(adapted), previous, this.root.getGeneralSettings().getDefaultMap());
            ((Map)this.getStoredValue()).put(adapted, section);
            return section;
        });
    }

    public void repopulate(@NotNull Map<Object, Block<?>> mappings) {
        this.clear();
        mappings.forEach(this::setInternal);
    }

    public void setAll(@NotNull Map<Route, Object> mappings) {
        mappings.forEach(this::set);
    }

    public void set(@NotNull Route route, @Nullable Object value) {
        this.traverse(route, true).ifPresent(reference -> ((BlockReference)reference).parent.setInternal(((BlockReference)reference).key, value));
    }

    public void set(@NotNull String route, @Nullable Object value) {
        this.traverse(route, true).ifPresent(reference -> ((BlockReference)reference).parent.setInternal(((BlockReference)reference).key, value));
    }

    private void setInternal(@NotNull Object key, @Nullable Object value) {
        if (value instanceof Section) {
            Section section = (Section)value;
            if (section.isRoot()) {
                throw new IllegalArgumentException("Cannot set root section as the value!");
            }
            if (section.getRoot().getGeneralSettings().getKeyFormat() != this.getRoot().getGeneralSettings().getKeyFormat()) {
                throw new IllegalArgumentException("Cannot move sections between files with different key formats!");
            }
            ((Map)this.getStoredValue()).put(key, section);
            section.adapt(this.root, this, this.getSubRoute(key));
            return;
        }
        if (value instanceof TerminatedBlock) {
            ((Map)this.getStoredValue()).put(key, (TerminatedBlock)value);
            return;
        }
        if (value instanceof Map) {
            ((Map)this.getStoredValue()).put(key, new Section(this.root, this, this.getSubRoute(key), ((Map)this.getStoredValue()).getOrDefault(key, null), (Map)value));
            return;
        }
        Block previous = (Block)((Map)this.getStoredValue()).get(key);
        if (previous == null) {
            ((Map)this.getStoredValue()).put(key, new TerminatedBlock(null, null, value));
            return;
        }
        ((Map)this.getStoredValue()).put(key, new TerminatedBlock((Block<?>)previous, value));
    }

    @Nullable
    public Block<?> move(@NotNull Route source, @NotNull Route destination) {
        return this.traverse(source, false).map(reference -> (Block)((Map)((BlockReference)reference).parent.getStoredValue()).remove(((BlockReference)reference).key)).map(block -> {
            this.set(destination, block);
            return block;
        }).orElse(null);
    }

    @Nullable
    public Block<?> move(@NotNull String source, @NotNull String destination) {
        return this.traverse(source, false).map(reference -> (Block)((Map)((BlockReference)reference).parent.getStoredValue()).remove(((BlockReference)reference).key)).map(block -> {
            this.set(destination, block);
            return block;
        }).orElse(null);
    }

    private Optional<BlockReference> traverse(@NotNull Route route, boolean createParents) {
        int i = -1;
        Section section = this;
        while (true) {
            Object key = this.adaptKey(route.get(++i));
            if (i + 1 == route.length()) {
                return Optional.of(new BlockReference(section, key));
            }
            Block block = ((Map)section.getStoredValue()).getOrDefault(key, null);
            if (block instanceof Section) {
                section = (Section)block;
                continue;
            }
            if (!createParents) {
                return Optional.empty();
            }
            section = section.createSectionInternal(key, block);
        }
    }

    private Optional<BlockReference> traverse(@NotNull String route, boolean createParents) {
        int lastSeparator = 0;
        Section section = this;
        int nextSeparator;
        while ((nextSeparator = route.indexOf(this.root.getGeneralSettings().getRouteSeparator(), lastSeparator)) != -1) {
            String key = route.substring(lastSeparator, nextSeparator);
            Block block = ((Map)section.getStoredValue()).getOrDefault(key, null);
            lastSeparator = nextSeparator + 1;
            if (block instanceof Section) {
                section = (Section)block;
                continue;
            }
            if (!createParents) {
                return Optional.empty();
            }
            section = section.createSectionInternal(key, block);
        }
        return Optional.of(new BlockReference(section, route.substring(lastSeparator)));
    }

    public boolean remove(@NotNull Route route) {
        return this.removeInternal(this.getParent(route).orElse(null), this.adaptKey(route.get(route.length() - 1)));
    }

    public boolean remove(@NotNull String route) {
        return this.removeInternal(this.getParent(route).orElse(null), route.substring(route.lastIndexOf(this.root.getGeneralSettings().getRouteSeparator()) + 1));
    }

    private boolean removeInternal(@Nullable Section parent, @Nullable Object key) {
        if (parent == null) {
            return false;
        }
        return ((Map)parent.getStoredValue()).remove(key) != null;
    }

    public void clear() {
        ((Map)this.getStoredValue()).clear();
    }

    public Optional<Block<?>> getOptionalBlock(@NotNull Route route) {
        return this.getBlockInternal(route, false);
    }

    private Optional<Block<?>> getDirectOptionalBlock(@NotNull Object key) {
        return Optional.ofNullable(((Map)this.getStoredValue()).get(this.adaptKey(key)));
    }

    public Optional<Block<?>> getOptionalBlock(@NotNull String route) {
        return route.indexOf(this.root.getGeneralSettings().getRouteSeparator()) != -1 ? this.getBlockInternalString(route, false) : this.getDirectOptionalBlock(route);
    }

    public Block<?> getBlock(@NotNull Route route) {
        return this.getOptionalBlock(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getBlock(route) : null);
    }

    public Block<?> getBlock(@NotNull String route) {
        return this.getOptionalBlock(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getBlock(route) : null);
    }

    private Optional<Block<?>> getBlockInternalString(@NotNull String route, boolean parent) {
        int nextSeparator;
        int lastSeparator = 0;
        Section section = this;
        while ((nextSeparator = route.indexOf(this.root.getGeneralSettings().getRouteSeparator(), lastSeparator)) != -1) {
            Block block = ((Map)section.getStoredValue()).getOrDefault(route.substring(lastSeparator, nextSeparator), null);
            if (!(block instanceof Section)) {
                return Optional.empty();
            }
            section = (Section)block;
            lastSeparator = nextSeparator + 1;
        }
        return Optional.ofNullable(parent ? section : (Block)((Map)section.getStoredValue()).get(route.substring(lastSeparator)));
    }

    private Optional<Block<?>> getBlockInternal(@NotNull Route route, boolean parent) {
        int i = -1;
        Section section = this;
        while (++i < route.length() - 1) {
            Block block = ((Map)section.getStoredValue()).getOrDefault(this.adaptKey(route.get(i)), null);
            if (!(block instanceof Section)) {
                return Optional.empty();
            }
            section = (Section)block;
        }
        return Optional.ofNullable(parent ? section : (Block)((Map)section.getStoredValue()).get(this.adaptKey(route.get(i))));
    }

    public Optional<Section> getParent(@NotNull Route route) {
        return this.getBlockInternal(route, true).map(block -> block instanceof Section ? (Section)block : null);
    }

    public Optional<Section> getParent(@NotNull String route) {
        return this.getBlockInternalString(route, true).map(block -> block instanceof Section ? (Section)block : null);
    }

    public Optional<Object> getOptional(@NotNull Route route) {
        return this.getOptionalBlock(route).map(block -> block instanceof Section ? block : block.getStoredValue());
    }

    public Optional<Object> getOptional(@NotNull String route) {
        return this.getOptionalBlock(route).map(block -> block instanceof Section ? block : block.getStoredValue());
    }

    public Object get(@NotNull Route route) {
        return this.getOptional(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.get(route) : this.root.getGeneralSettings().getDefaultObject());
    }

    public Object get(@NotNull String route) {
        return this.getOptional(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.get(route) : this.root.getGeneralSettings().getDefaultObject());
    }

    public Object get(@NotNull Route route, @Nullable Object def) {
        return this.getOptional(route).orElse(def);
    }

    public Object get(@NotNull String route, @Nullable Object def) {
        return this.getOptional(route).orElse(def);
    }

    public <T> Optional<T> getAsOptional(@NotNull Route route, @NotNull Class<T> clazz) {
        return this.getOptional(route).map(object -> clazz.isInstance(object) ? object : (PrimitiveConversions.isNumber(object.getClass()) && PrimitiveConversions.isNumber(clazz) ? PrimitiveConversions.convertNumber(object, clazz) : (PrimitiveConversions.NON_NUMERIC_CONVERSIONS.containsKey(object.getClass()) && PrimitiveConversions.NON_NUMERIC_CONVERSIONS.containsKey(clazz) ? object : null)));
    }

    public <T> Optional<T> getAsOptional(@NotNull String route, @NotNull Class<T> clazz) {
        return this.getOptional(route).map(object -> clazz.isInstance(object) ? object : (PrimitiveConversions.isNumber(object.getClass()) && PrimitiveConversions.isNumber(clazz) ? PrimitiveConversions.convertNumber(object, clazz) : (PrimitiveConversions.NON_NUMERIC_CONVERSIONS.containsKey(object.getClass()) && PrimitiveConversions.NON_NUMERIC_CONVERSIONS.containsKey(clazz) ? object : null)));
    }

    public <T> T getAs(@NotNull Route route, @NotNull Class<T> clazz) {
        return (T)this.getAsOptional(route, clazz).orElseGet(() -> this.canUseDefaults() ? this.defaults.getAs(route, clazz) : null);
    }

    public <T> T getAs(@NotNull String route, @NotNull Class<T> clazz) {
        return (T)this.getAsOptional(route, clazz).orElseGet(() -> this.canUseDefaults() ? this.defaults.getAs(route, clazz) : null);
    }

    public <T> T getAs(@NotNull Route route, @NotNull Class<T> clazz, @Nullable T def) {
        return this.getAsOptional(route, clazz).orElse(def);
    }

    public <T> T getAs(@NotNull String route, @NotNull Class<T> clazz, @Nullable T def) {
        return this.getAsOptional(route, clazz).orElse(def);
    }

    public <T> boolean is(@NotNull Route route, @NotNull Class<T> clazz) {
        Object o = this.get(route);
        return PrimitiveConversions.PRIMITIVES_TO_OBJECTS.containsKey(clazz) ? PrimitiveConversions.PRIMITIVES_TO_OBJECTS.get(clazz).isInstance(o) : clazz.isInstance(o);
    }

    public <T> boolean is(@NotNull String route, @NotNull Class<T> clazz) {
        Object o = this.get(route);
        return PrimitiveConversions.PRIMITIVES_TO_OBJECTS.containsKey(clazz) ? PrimitiveConversions.PRIMITIVES_TO_OBJECTS.get(clazz).isInstance(o) : clazz.isInstance(o);
    }

    public Optional<Section> getOptionalSection(@NotNull Route route) {
        return this.getAsOptional(route, Section.class);
    }

    public Optional<Section> getOptionalSection(@NotNull String route) {
        return this.getAsOptional(route, Section.class);
    }

    public Section getSection(@NotNull Route route) {
        return this.getOptionalSection(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getSection(route) : null);
    }

    public Section getSection(@NotNull String route) {
        return this.getOptionalSection(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getSection(route) : null);
    }

    public Section getSection(@NotNull Route route, @Nullable Section def) {
        return this.getOptionalSection(route).orElse(def);
    }

    public Section getSection(@NotNull String route, @Nullable Section def) {
        return this.getOptionalSection(route).orElse(def);
    }

    public boolean isSection(@NotNull Route route) {
        return this.get(route) instanceof Section;
    }

    public boolean isSection(@NotNull String route) {
        return this.get(route) instanceof Section;
    }

    public Optional<String> getOptionalString(@NotNull Route route) {
        return this.getOptional(route).map(Object::toString);
    }

    public Optional<String> getOptionalString(@NotNull String route) {
        return this.getOptional(route).map(Object::toString);
    }

    public String getString(@NotNull Route route) {
        return this.getOptionalString(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getString(route) : this.root.getGeneralSettings().getDefaultString());
    }

    public String getString(@NotNull String route) {
        return this.getOptionalString(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getString(route) : this.root.getGeneralSettings().getDefaultString());
    }

    public String getString(@NotNull Route route, @Nullable String def) {
        return this.getOptionalString(route).orElse(def);
    }

    public String getString(@NotNull String route, @Nullable String def) {
        return this.getOptionalString(route).orElse(def);
    }

    public boolean isString(@NotNull Route route) {
        return this.get(route) instanceof String;
    }

    public boolean isString(@NotNull String route) {
        return this.get(route) instanceof String;
    }

    public <T extends Enum<T>> Optional<T> getOptionalEnum(@NotNull Route route, @NotNull Class<T> clazz) {
        return this.getOptional(route).map(name -> this.toEnum(name, clazz));
    }

    public <T extends Enum<T>> Optional<T> getOptionalEnum(@NotNull String route, @NotNull Class<T> clazz) {
        return this.getOptionalString(route).map(name -> this.toEnum(name, clazz));
    }

    public <T extends Enum<T>> T getEnum(@NotNull Route route, @NotNull Class<T> clazz) {
        return (T)this.getOptionalEnum(route, clazz).orElseGet(() -> this.canUseDefaults() ? (Enum)this.defaults.getEnum(route, clazz) : null);
    }

    public <T extends Enum<T>> T getEnum(@NotNull String route, @NotNull Class<T> clazz) {
        return (T)this.getOptionalEnum(route, clazz).orElseGet(() -> this.canUseDefaults() ? (Enum)this.defaults.getEnum(route, clazz) : null);
    }

    public <T extends Enum<T>> T getEnum(@NotNull Route route, @NotNull Class<T> clazz, @Nullable T def) {
        return (T)((Enum)this.getOptionalEnum(route, clazz).orElse(def));
    }

    public <T extends Enum<T>> T getEnum(@NotNull String route, @NotNull Class<T> clazz, @Nullable T def) {
        return (T)((Enum)this.getOptionalEnum(route, clazz).orElse(def));
    }

    public <T extends Enum<T>> boolean isEnum(@NotNull Route route, @NotNull Class<T> clazz) {
        return this.toEnum(this.get(route), clazz) != null;
    }

    public <T extends Enum<T>> boolean isEnum(@NotNull String route, @NotNull Class<T> clazz) {
        return this.toEnum(this.get(route), clazz) != null;
    }

    private <T extends Enum<T>> T toEnum(@Nullable Object object, @NotNull Class<T> clazz) {
        if (object == null) {
            return null;
        }
        if (clazz.isInstance(object)) {
            return (T)((Enum)object);
        }
        if (object instanceof Enum) {
            return null;
        }
        try {
            return Enum.valueOf(clazz, object.toString());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public Optional<Character> getOptionalChar(@NotNull Route route) {
        return this.getOptional(route).map(this::toChar);
    }

    public Optional<Character> getOptionalChar(@NotNull String route) {
        return this.getOptional(route).map(this::toChar);
    }

    public Character getChar(@NotNull Route route) {
        return this.getOptionalChar(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getChar(route) : this.root.getGeneralSettings().getDefaultChar());
    }

    public Character getChar(@NotNull String route) {
        return this.getOptionalChar(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getChar(route) : this.root.getGeneralSettings().getDefaultChar());
    }

    public Character getChar(@NotNull Route route, @Nullable Character def) {
        return this.getOptionalChar(route).orElse(def);
    }

    public Character getChar(@NotNull String route, @Nullable Character def) {
        return this.getOptionalChar(route).orElse(def);
    }

    public boolean isChar(@NotNull Route route) {
        return this.toChar(this.get(route)) != null;
    }

    public boolean isChar(@NotNull String route) {
        return this.toChar(this.get(route)) != null;
    }

    private Character toChar(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Character) {
            return (Character)object;
        }
        if (object instanceof Integer) {
            return Character.valueOf((char)((Integer)object).intValue());
        }
        if (object instanceof String && object.toString().length() == 1) {
            return Character.valueOf(object.toString().charAt(0));
        }
        return null;
    }

    public Optional<Number> getOptionalNumber(@NotNull Route route) {
        return this.getAsOptional(route, Number.class);
    }

    public Optional<Number> getOptionalNumber(@NotNull String route) {
        return this.getAsOptional(route, Number.class);
    }

    public Number getNumber(@NotNull Route route) {
        return this.getOptionalNumber(route).orElseGet(() -> this.canUseDefaults() ? (Number)this.defaults.getNumber(route) : (Number)this.root.getGeneralSettings().getDefaultNumber());
    }

    public Number getNumber(@NotNull String route) {
        return this.getOptionalNumber(route).orElseGet(() -> this.canUseDefaults() ? (Number)this.defaults.getNumber(route) : (Number)this.root.getGeneralSettings().getDefaultNumber());
    }

    public Number getNumber(@NotNull Route route, @Nullable Number def) {
        return this.getOptionalNumber(route).orElse(def);
    }

    public Number getNumber(@NotNull String route, @Nullable Number def) {
        return this.getOptionalNumber(route).orElse(def);
    }

    public boolean isNumber(@NotNull Route route) {
        return this.get(route) instanceof Number;
    }

    public boolean isNumber(@NotNull String route) {
        return this.get(route) instanceof Number;
    }

    public Optional<Integer> getOptionalInt(@NotNull Route route) {
        return PrimitiveConversions.toInt(this.getAs(route, Number.class));
    }

    public Optional<Integer> getOptionalInt(@NotNull String route) {
        return PrimitiveConversions.toInt(this.getAs(route, Number.class));
    }

    public Integer getInt(@NotNull Route route) {
        return this.getOptionalInt(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getInt(route).intValue() : this.root.getGeneralSettings().getDefaultNumber().intValue());
    }

    public Integer getInt(@NotNull String route) {
        return this.getOptionalInt(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getInt(route).intValue() : this.root.getGeneralSettings().getDefaultNumber().intValue());
    }

    public Integer getInt(@NotNull Route route, @Nullable Integer def) {
        return this.getOptionalInt(route).orElse(def);
    }

    public Integer getInt(@NotNull String route, @Nullable Integer def) {
        return this.getOptionalInt(route).orElse(def);
    }

    public boolean isInt(@NotNull Route route) {
        return this.get(route) instanceof Integer;
    }

    public boolean isInt(@NotNull String route) {
        return this.get(route) instanceof Integer;
    }

    public Optional<BigInteger> getOptionalBigInt(@NotNull Route route) {
        return PrimitiveConversions.toBigInt(this.getAs(route, Number.class));
    }

    public Optional<BigInteger> getOptionalBigInt(@NotNull String route) {
        return PrimitiveConversions.toBigInt(this.getAs(route, Number.class));
    }

    public BigInteger getBigInt(@NotNull Route route) {
        return this.getOptionalBigInt(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getBigInt(route) : BigInteger.valueOf(this.root.getGeneralSettings().getDefaultNumber().longValue()));
    }

    public BigInteger getBigInt(@NotNull String route) {
        return this.getOptionalBigInt(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getBigInt(route) : BigInteger.valueOf(this.root.getGeneralSettings().getDefaultNumber().longValue()));
    }

    public BigInteger getBigInt(@NotNull Route route, @Nullable BigInteger def) {
        return this.getOptionalBigInt(route).orElse(def);
    }

    public BigInteger getBigInt(@NotNull String route, @Nullable BigInteger def) {
        return this.getOptionalBigInt(route).orElse(def);
    }

    public boolean isBigInt(@NotNull Route route) {
        return this.get(route) instanceof BigInteger;
    }

    public boolean isBigInt(@NotNull String route) {
        return this.get(route) instanceof BigInteger;
    }

    public Optional<Boolean> getOptionalBoolean(@NotNull Route route) {
        return this.getAsOptional(route, Boolean.class);
    }

    public Optional<Boolean> getOptionalBoolean(@NotNull String route) {
        return this.getAsOptional(route, Boolean.class);
    }

    public Boolean getBoolean(@NotNull Route route) {
        return this.getOptionalBoolean(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getBoolean(route) : this.root.getGeneralSettings().getDefaultBoolean());
    }

    public Boolean getBoolean(@NotNull String route) {
        return this.getOptionalBoolean(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getBoolean(route) : this.root.getGeneralSettings().getDefaultBoolean());
    }

    public Boolean getBoolean(@NotNull Route route, @Nullable Boolean def) {
        return this.getOptionalBoolean(route).orElse(def);
    }

    public Boolean getBoolean(@NotNull String route, @Nullable Boolean def) {
        return this.getOptionalBoolean(route).orElse(def);
    }

    public boolean isBoolean(@NotNull Route route) {
        return this.get(route) instanceof Boolean;
    }

    public boolean isBoolean(@NotNull String route) {
        return this.get(route) instanceof Boolean;
    }

    public Optional<Double> getOptionalDouble(@NotNull Route route) {
        return PrimitiveConversions.toDouble(this.getAs(route, Number.class));
    }

    public Optional<Double> getOptionalDouble(@NotNull String route) {
        return PrimitiveConversions.toDouble(this.getAs(route, Number.class));
    }

    public Double getDouble(@NotNull Route route) {
        return this.getOptionalDouble(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getDouble(route).doubleValue() : this.root.getGeneralSettings().getDefaultNumber().doubleValue());
    }

    public Double getDouble(@NotNull String route) {
        return this.getOptionalDouble(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getDouble(route).doubleValue() : this.root.getGeneralSettings().getDefaultNumber().doubleValue());
    }

    public Double getDouble(@NotNull Route route, @Nullable Double def) {
        return this.getOptionalDouble(route).orElse(def);
    }

    public Double getDouble(@NotNull String route, @Nullable Double def) {
        return this.getOptionalDouble(route).orElse(def);
    }

    public boolean isDouble(@NotNull Route route) {
        return this.get(route) instanceof Double;
    }

    public boolean isDouble(@NotNull String route) {
        return this.get(route) instanceof Double;
    }

    public Optional<Float> getOptionalFloat(@NotNull Route route) {
        return PrimitiveConversions.toFloat(this.getAs(route, Number.class));
    }

    public Optional<Float> getOptionalFloat(@NotNull String route) {
        return PrimitiveConversions.toFloat(this.getAs(route, Number.class));
    }

    public Float getFloat(@NotNull Route route) {
        return this.getOptionalFloat(route).orElseGet(() -> Float.valueOf(this.canUseDefaults() ? this.defaults.getFloat(route).floatValue() : this.root.getGeneralSettings().getDefaultNumber().floatValue()));
    }

    public Float getFloat(@NotNull String route) {
        return this.getOptionalFloat(route).orElseGet(() -> Float.valueOf(this.canUseDefaults() ? this.defaults.getFloat(route).floatValue() : this.root.getGeneralSettings().getDefaultNumber().floatValue()));
    }

    public Float getFloat(@NotNull Route route, @Nullable Float def) {
        return this.getOptionalFloat(route).orElse(def);
    }

    public Float getFloat(@NotNull String route, @Nullable Float def) {
        return this.getOptionalFloat(route).orElse(def);
    }

    public boolean isFloat(@NotNull Route route) {
        return this.get(route) instanceof Float;
    }

    public boolean isFloat(@NotNull String route) {
        return this.get(route) instanceof Float;
    }

    public Optional<Byte> getOptionalByte(@NotNull Route route) {
        return PrimitiveConversions.toByte(this.getAs(route, Number.class));
    }

    public Optional<Byte> getOptionalByte(@NotNull String route) {
        return PrimitiveConversions.toByte(this.getAs(route, Number.class));
    }

    public Byte getByte(@NotNull Route route) {
        return this.getOptionalByte(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getByte(route).byteValue() : this.root.getGeneralSettings().getDefaultNumber().byteValue());
    }

    public Byte getByte(@NotNull String route) {
        return this.getOptionalByte(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getByte(route).byteValue() : this.root.getGeneralSettings().getDefaultNumber().byteValue());
    }

    public Byte getByte(@NotNull Route route, @Nullable Byte def) {
        return this.getOptionalByte(route).orElse(def);
    }

    public Byte getByte(@NotNull String route, @Nullable Byte def) {
        return this.getOptionalByte(route).orElse(def);
    }

    public boolean isByte(@NotNull Route route) {
        return this.get(route) instanceof Byte;
    }

    public boolean isByte(@NotNull String route) {
        return this.get(route) instanceof Byte;
    }

    public Optional<Long> getOptionalLong(@NotNull Route route) {
        return PrimitiveConversions.toLong(this.getAs(route, Number.class));
    }

    public Optional<Long> getOptionalLong(String route) {
        return PrimitiveConversions.toLong(this.getAs(route, Number.class));
    }

    public Long getLong(@NotNull Route route) {
        return this.getOptionalLong(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getLong(route).longValue() : this.root.getGeneralSettings().getDefaultNumber().longValue());
    }

    public Long getLong(@NotNull String route) {
        return this.getOptionalLong(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getLong(route).longValue() : this.root.getGeneralSettings().getDefaultNumber().longValue());
    }

    public Long getLong(@NotNull Route route, @Nullable Long def) {
        return this.getOptionalLong(route).orElse(def);
    }

    public Long getLong(@NotNull String route, @Nullable Long def) {
        return this.getOptionalLong(route).orElse(def);
    }

    public boolean isLong(@NotNull Route route) {
        return this.get(route) instanceof Long;
    }

    public boolean isLong(@NotNull String route) {
        return this.get(route) instanceof Long;
    }

    public Optional<Short> getOptionalShort(@NotNull Route route) {
        return PrimitiveConversions.toShort(this.getAs(route, Number.class));
    }

    public Optional<Short> getOptionalShort(@NotNull String route) {
        return PrimitiveConversions.toShort(this.getAs(route, Number.class));
    }

    public Short getShort(@NotNull Route route) {
        return this.getOptionalShort(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getShort(route).shortValue() : this.root.getGeneralSettings().getDefaultNumber().shortValue());
    }

    public Short getShort(@NotNull String route) {
        return this.getOptionalShort(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getShort(route).shortValue() : this.root.getGeneralSettings().getDefaultNumber().shortValue());
    }

    public Short getShort(@NotNull Route route, @Nullable Short def) {
        return this.getOptionalShort(route).orElse(def);
    }

    public Short getShort(@NotNull String route, @Nullable Short def) {
        return this.getOptionalShort(route).orElse(def);
    }

    public boolean isShort(@NotNull Route route) {
        return this.get(route) instanceof Short;
    }

    public boolean isShort(@NotNull String route) {
        return this.get(route) instanceof Short;
    }

    public boolean isDecimal(@NotNull Route route) {
        Object o = this.get(route);
        return o instanceof Double || o instanceof Float;
    }

    public boolean isDecimal(@NotNull String route) {
        Object o = this.get(route);
        return o instanceof Double || o instanceof Float;
    }

    public Optional<List<?>> getOptionalList(@NotNull Route route) {
        return this.getAsOptional(route, List.class).map(list -> list);
    }

    public Optional<List<?>> getOptionalList(@NotNull String route) {
        return this.getAsOptional(route, List.class).map(list -> list);
    }

    public List<?> getList(@NotNull Route route) {
        return this.getOptionalList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public List<?> getList(@NotNull String route) {
        return this.getOptionalList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public List<?> getList(@NotNull Route route, @Nullable List<?> def) {
        return this.getOptionalList(route).orElse(def);
    }

    public List<?> getList(@NotNull String route, @Nullable List<?> def) {
        return this.getOptionalList(route).orElse(def);
    }

    public boolean isList(@NotNull Route route) {
        return this.get(route) instanceof List;
    }

    public boolean isList(@NotNull String route) {
        return this.get(route) instanceof List;
    }

    public Optional<List<String>> getOptionalStringList(@NotNull Route route) {
        return ListConversions.toStringList(this.getList(route, null));
    }

    public Optional<List<String>> getOptionalStringList(@NotNull String route) {
        return ListConversions.toStringList(this.getList(route, null));
    }

    public List<String> getStringList(@NotNull Route route, @Nullable List<String> def) {
        return this.getOptionalStringList(route).orElse(def);
    }

    public List<String> getStringList(@NotNull String route, @Nullable List<String> def) {
        return this.getOptionalStringList(route).orElse(def);
    }

    public List<String> getStringList(@NotNull Route route) {
        return this.getOptionalStringList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getStringList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public List<String> getStringList(@NotNull String route) {
        return this.getOptionalStringList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getStringList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public Optional<List<Integer>> getOptionalIntList(@NotNull Route route) {
        return ListConversions.toIntList(this.getList(route, null));
    }

    public Optional<List<Integer>> getOptionalIntList(@NotNull String route) {
        return ListConversions.toIntList(this.getList(route, null));
    }

    public List<Integer> getIntList(@NotNull Route route, @Nullable List<Integer> def) {
        return this.getOptionalIntList(route).orElse(def);
    }

    public List<Integer> getIntList(@NotNull String route, @Nullable List<Integer> def) {
        return this.getOptionalIntList(route).orElse(def);
    }

    public List<Integer> getIntList(@NotNull Route route) {
        return this.getOptionalIntList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getIntList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public List<Integer> getIntList(@NotNull String route) {
        return this.getOptionalIntList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getIntList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public Optional<List<BigInteger>> getOptionalBigIntList(@NotNull Route route) {
        return ListConversions.toBigIntList(this.getList(route, null));
    }

    public Optional<List<BigInteger>> getOptionalBigIntList(@NotNull String route) {
        return ListConversions.toBigIntList(this.getList(route, null));
    }

    public List<BigInteger> getBigIntList(@NotNull Route route, @Nullable List<BigInteger> def) {
        return this.getOptionalBigIntList(route).orElse(def);
    }

    public List<BigInteger> getBigIntList(@NotNull String route, @Nullable List<BigInteger> def) {
        return this.getOptionalBigIntList(route).orElse(def);
    }

    public List<BigInteger> getBigIntList(@NotNull Route route) {
        return this.getOptionalBigIntList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getBigIntList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public List<BigInteger> getBigIntList(@NotNull String route) {
        return this.getOptionalBigIntList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getBigIntList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public Optional<List<Byte>> getOptionalByteList(@NotNull Route route) {
        return ListConversions.toByteList(this.getList(route, null));
    }

    public Optional<List<Byte>> getOptionalByteList(@NotNull String route) {
        return ListConversions.toByteList(this.getList(route, null));
    }

    public List<Byte> getByteList(@NotNull Route route, @Nullable List<Byte> def) {
        return this.getOptionalByteList(route).orElse(def);
    }

    public List<Byte> getByteList(@NotNull String route, @Nullable List<Byte> def) {
        return this.getOptionalByteList(route).orElse(def);
    }

    public List<Byte> getByteList(@NotNull Route route) {
        return this.getOptionalByteList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getByteList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public List<Byte> getByteList(@NotNull String route) {
        return this.getOptionalByteList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getByteList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public Optional<List<Long>> getOptionalLongList(@NotNull Route route) {
        return ListConversions.toLongList(this.getList(route, null));
    }

    public Optional<List<Long>> getOptionalLongList(@NotNull String route) {
        return ListConversions.toLongList(this.getList(route, null));
    }

    public List<Long> getLongList(@NotNull Route route, @Nullable List<Long> def) {
        return this.getOptionalLongList(route).orElse(def);
    }

    public List<Long> getLongList(@NotNull String route, @Nullable List<Long> def) {
        return this.getOptionalLongList(route).orElse(def);
    }

    public List<Long> getLongList(@NotNull Route route) {
        return this.getOptionalLongList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getLongList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public List<Long> getLongList(@NotNull String route) {
        return this.getOptionalLongList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getLongList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public Optional<List<Double>> getOptionalDoubleList(@NotNull Route route) {
        return ListConversions.toDoubleList(this.getList(route, null));
    }

    public Optional<List<Double>> getOptionalDoubleList(@NotNull String route) {
        return ListConversions.toDoubleList(this.getList(route, null));
    }

    public List<Double> getDoubleList(@NotNull Route route, @Nullable List<Double> def) {
        return this.getOptionalDoubleList(route).orElse(def);
    }

    public List<Double> getDoubleList(@NotNull String route, @Nullable List<Double> def) {
        return this.getOptionalDoubleList(route).orElse(def);
    }

    public List<Double> getDoubleList(@NotNull Route route) {
        return this.getOptionalDoubleList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getDoubleList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public List<Double> getDoubleList(@NotNull String route) {
        return this.getOptionalDoubleList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getDoubleList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public Optional<List<Float>> getOptionalFloatList(@NotNull Route route) {
        return ListConversions.toFloatList(this.getList(route, null));
    }

    public Optional<List<Float>> getOptionalFloatList(@NotNull String route) {
        return ListConversions.toFloatList(this.getList(route, null));
    }

    public List<Float> getFloatList(@NotNull Route route, @Nullable List<Float> def) {
        return this.getOptionalFloatList(route).orElse(def);
    }

    public List<Float> getFloatList(@NotNull String route, @Nullable List<Float> def) {
        return this.getOptionalFloatList(route).orElse(def);
    }

    public List<Float> getFloatList(@NotNull Route route) {
        return this.getOptionalFloatList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getFloatList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public List<Float> getFloatList(@NotNull String route) {
        return this.getOptionalFloatList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getFloatList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public Optional<List<Short>> getOptionalShortList(@NotNull Route route) {
        return ListConversions.toShortList(this.getList(route, null));
    }

    public Optional<List<Short>> getOptionalShortList(@NotNull String route) {
        return ListConversions.toShortList(this.getList(route, null));
    }

    public List<Short> getShortList(@NotNull Route route, @Nullable List<Short> def) {
        return this.getOptionalShortList(route).orElse(def);
    }

    public List<Short> getShortList(@NotNull String route, @Nullable List<Short> def) {
        return this.getOptionalShortList(route).orElse(def);
    }

    public List<Short> getShortList(@NotNull Route route) {
        return this.getOptionalShortList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getShortList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public List<Short> getShortList(@NotNull String route) {
        return this.getOptionalShortList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getShortList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public Optional<List<Map<?, ?>>> getOptionalMapList(@NotNull Route route) {
        return ListConversions.toMapList(this.getList(route, null));
    }

    public Optional<List<Map<?, ?>>> getOptionalMapList(@NotNull String route) {
        return ListConversions.toMapList(this.getList(route, null));
    }

    public List<Map<?, ?>> getMapList(@NotNull Route route, @Nullable List<Map<?, ?>> def) {
        return this.getOptionalMapList(route).orElse(def);
    }

    public List<Map<?, ?>> getMapList(@NotNull String route, @Nullable List<Map<?, ?>> def) {
        return this.getOptionalMapList(route).orElse(def);
    }

    public List<Map<?, ?>> getMapList(@NotNull Route route) {
        return this.getOptionalMapList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getMapList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    public List<Map<?, ?>> getMapList(@NotNull String route) {
        return this.getOptionalMapList(route).orElseGet(() -> this.canUseDefaults() ? this.defaults.getMapList(route) : this.root.getGeneralSettings().getDefaultList());
    }

    private static class BlockReference {
        @NotNull
        private final Section parent;
        @NotNull
        private final Object key;

        private BlockReference(@NotNull Section parent, @NotNull Object key) {
            this.parent = parent;
            this.key = key;
        }
    }
}

