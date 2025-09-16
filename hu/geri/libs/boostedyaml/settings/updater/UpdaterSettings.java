/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.settings.updater;

import hu.geri.libs.boostedyaml.YamlDocument;
import hu.geri.libs.boostedyaml.dvs.Pattern;
import hu.geri.libs.boostedyaml.dvs.versioning.AutomaticVersioning;
import hu.geri.libs.boostedyaml.dvs.versioning.ManualVersioning;
import hu.geri.libs.boostedyaml.dvs.versioning.Versioning;
import hu.geri.libs.boostedyaml.route.Route;
import hu.geri.libs.boostedyaml.route.RouteFactory;
import hu.geri.libs.boostedyaml.settings.Settings;
import hu.geri.libs.boostedyaml.settings.updater.MergeRule;
import hu.geri.libs.boostedyaml.settings.updater.ValueMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpdaterSettings
implements Settings {
    public static final boolean DEFAULT_AUTO_SAVE = true;
    public static final boolean DEFAULT_ENABLE_DOWNGRADING = true;
    public static final boolean DEFAULT_KEEP_ALL = false;
    public static final OptionSorting DEFAULT_OPTION_SORTING = OptionSorting.SORT_BY_DEFAULTS;
    public static final Map<MergeRule, Boolean> DEFAULT_MERGE_RULES = Collections.unmodifiableMap(new HashMap<MergeRule, Boolean>(){
        {
            this.put(MergeRule.MAPPINGS, true);
            this.put(MergeRule.MAPPING_AT_SECTION, false);
            this.put(MergeRule.SECTION_AT_MAPPING, false);
        }
    });
    public static final Versioning DEFAULT_VERSIONING = null;
    public static final UpdaterSettings DEFAULT = UpdaterSettings.builder().build();
    private final boolean autoSave;
    private final boolean enableDowngrading;
    private final boolean keepAll;
    private final Map<MergeRule, Boolean> mergeRules;
    private final Map<String, RouteSet> ignored;
    private final Map<String, RouteMap<Route, String>> relocations;
    private final Map<String, Map<Route, ValueMapper>> mappers;
    private final Map<String, List<Consumer<YamlDocument>>> customLogic;
    private final Versioning versioning;
    private final OptionSorting optionSorting;

    public UpdaterSettings(Builder builder) {
        this.autoSave = builder.autoSave;
        this.enableDowngrading = builder.enableDowngrading;
        this.keepAll = builder.keepAll;
        this.optionSorting = builder.optionSorting;
        this.mergeRules = builder.mergeRules;
        this.ignored = builder.ignored;
        this.relocations = builder.relocations;
        this.mappers = builder.mappers;
        this.customLogic = builder.customLogic;
        this.versioning = builder.versioning;
    }

    public Map<MergeRule, Boolean> getMergeRules() {
        return this.mergeRules;
    }

    public Set<Route> getIgnoredRoutes(@NotNull String versionId, char separator) {
        RouteSet ignored = this.ignored.get(versionId);
        return ignored == null ? Collections.emptySet() : ignored.merge(separator);
    }

    public Map<Route, Route> getRelocations(@NotNull String versionId, char separator) {
        RouteMap<Route, String> relocations = this.relocations.get(versionId);
        return relocations == null ? Collections.emptyMap() : relocations.merge(Function.identity(), route -> Route.fromString(route, separator), separator);
    }

    public Map<Route, ValueMapper> getMappers(@NotNull String versionId, char separator) {
        return this.mappers.getOrDefault(versionId, Collections.emptyMap());
    }

    public List<Consumer<YamlDocument>> getCustomLogic(@NotNull String versionId) {
        return this.customLogic.getOrDefault(versionId, Collections.emptyList());
    }

    public Versioning getVersioning() {
        return this.versioning;
    }

    public boolean isEnableDowngrading() {
        return this.enableDowngrading;
    }

    public boolean isKeepAll() {
        return this.keepAll;
    }

    public boolean isAutoSave() {
        return this.autoSave;
    }

    public OptionSorting getOptionSorting() {
        return this.optionSorting;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(UpdaterSettings settings) {
        return UpdaterSettings.builder().setAutoSave(settings.autoSave).setEnableDowngrading(settings.enableDowngrading).setKeepAll(settings.keepAll).setOptionSorting(settings.optionSorting).setMergeRules(settings.mergeRules).setIgnoredRoutesInternal(settings.ignored).setRelocationsInternal(settings.relocations).addMappers(settings.mappers).addCustomLogic(settings.customLogic).setVersioning(settings.versioning);
    }

    private static class RouteSet {
        private Set<Route> routes = null;
        private Set<String> strings = null;

        private RouteSet() {
        }

        public Set<Route> merge(char separator) {
            if ((this.routes == null || this.routes.isEmpty()) && (this.strings == null || this.strings.isEmpty())) {
                return Collections.emptySet();
            }
            HashSet<Route> set = new HashSet<Route>();
            if (this.strings != null) {
                this.strings.forEach(route -> set.add(Route.fromString(route, separator)));
            }
            if (this.routes != null) {
                set.addAll(this.routes);
            }
            return set;
        }

        public Set<Route> getRouteSet() {
            return this.routes == null ? (this.routes = new HashSet<Route>()) : this.routes;
        }

        public Set<String> getStringSet() {
            return this.strings == null ? (this.strings = new HashSet<String>()) : this.strings;
        }
    }

    private static class RouteMap<R, S> {
        private Map<Route, R> routes = null;
        private Map<String, S> strings = null;

        private RouteMap() {
        }

        @NotNull
        public <T> Map<Route, T> merge(@NotNull Function<R, T> routeMapper, @NotNull Function<S, T> stringMapper, char separator) {
            if ((this.routes == null || this.routes.isEmpty()) && (this.strings == null || this.strings.isEmpty())) {
                return Collections.emptyMap();
            }
            HashMap map = new HashMap();
            if (this.strings != null) {
                this.strings.forEach((key, value) -> map.put(Route.fromString(key, separator), stringMapper.apply(value)));
            }
            if (this.routes != null) {
                this.routes.forEach((key, value) -> map.put(key, routeMapper.apply(value)));
            }
            return map;
        }

        @NotNull
        public Map<Route, R> getRouteMap() {
            return this.routes == null ? (this.routes = new HashMap<Route, R>()) : this.routes;
        }

        @NotNull
        public Map<String, S> getStringMap() {
            return this.strings == null ? (this.strings = new HashMap<String, S>()) : this.strings;
        }
    }

    public static class Builder {
        private boolean autoSave = true;
        private boolean enableDowngrading = true;
        private boolean keepAll = false;
        private final Map<MergeRule, Boolean> mergeRules = new HashMap<MergeRule, Boolean>(DEFAULT_MERGE_RULES);
        private final Map<String, RouteSet> ignored = new HashMap<String, RouteSet>();
        private final Map<String, RouteMap<Route, String>> relocations = new HashMap<String, RouteMap<Route, String>>();
        private final Map<String, Map<Route, ValueMapper>> mappers = new HashMap<String, Map<Route, ValueMapper>>();
        private final Map<String, List<Consumer<YamlDocument>>> customLogic = new HashMap<String, List<Consumer<YamlDocument>>>();
        private Versioning versioning = DEFAULT_VERSIONING;
        private OptionSorting optionSorting = DEFAULT_OPTION_SORTING;

        private Builder() {
        }

        public Builder setAutoSave(boolean autoSave) {
            this.autoSave = autoSave;
            return this;
        }

        public Builder setEnableDowngrading(boolean enableDowngrading) {
            this.enableDowngrading = enableDowngrading;
            return this;
        }

        public Builder setKeepAll(boolean keepAll) {
            this.keepAll = keepAll;
            return this;
        }

        public Builder setOptionSorting(@NotNull OptionSorting optionSorting) {
            this.optionSorting = optionSorting;
            return this;
        }

        public Builder setMergeRules(@NotNull Map<MergeRule, Boolean> mergeRules) {
            this.mergeRules.putAll(mergeRules);
            return this;
        }

        public Builder setMergeRule(@NotNull MergeRule rule, boolean preserveDocument) {
            this.mergeRules.put(rule, preserveDocument);
            return this;
        }

        private Builder setIgnoredRoutesInternal(@NotNull Map<String, RouteSet> routes) {
            this.ignored.putAll(routes);
            return this;
        }

        @Deprecated
        public Builder setIgnoredRoutes(@NotNull Map<String, Set<Route>> routes) {
            routes.forEach((versionId, set) -> this.ignored.computeIfAbsent((String)versionId, key -> new RouteSet()).getRouteSet().addAll((Collection<Route>)set));
            return this;
        }

        @Deprecated
        public Builder setIgnoredRoutes(@NotNull String versionId, @NotNull Set<Route> routes) {
            this.ignored.computeIfAbsent(versionId, key -> new RouteSet()).getRouteSet().addAll(routes);
            return this;
        }

        @Deprecated
        public Builder setIgnoredStringRoutes(@NotNull Map<String, Set<String>> routes) {
            routes.forEach((versionId, set) -> this.ignored.computeIfAbsent((String)versionId, key -> new RouteSet()).getStringSet().addAll((Collection<String>)set));
            return this;
        }

        @Deprecated
        public Builder setIgnoredStringRoutes(@NotNull String versionId, @NotNull Set<String> routes) {
            this.ignored.computeIfAbsent(versionId, key -> new RouteSet()).getStringSet().addAll(routes);
            return this;
        }

        public Builder addIgnoredRoute(@NotNull String versionId, @NotNull Route route) {
            return this.addIgnoredRoutes(versionId, Collections.singleton(route));
        }

        public Builder addIgnoredRoutes(@NotNull String versionId, @NotNull Set<Route> routes) {
            return this.addIgnoredRoutes(Collections.singletonMap(versionId, routes));
        }

        public Builder addIgnoredRoutes(@NotNull Map<String, Set<Route>> routes) {
            routes.forEach((versionId, set) -> this.ignored.computeIfAbsent((String)versionId, key -> new RouteSet()).getRouteSet().addAll((Collection<Route>)set));
            return this;
        }

        public Builder addIgnoredRoute(@NotNull String versionId, @NotNull String route, char separator) {
            return this.addIgnoredRoutes(versionId, Collections.singleton(route), separator);
        }

        public Builder addIgnoredRoutes(@NotNull String versionId, @NotNull Set<String> routes, char separator) {
            this.addIgnoredRoutes(versionId, routes, new RouteFactory(separator));
            return this;
        }

        public Builder addIgnoredRoutes(@NotNull Map<String, Set<String>> routes, char separator) {
            RouteFactory factory = new RouteFactory(separator);
            routes.forEach((versionId, collection) -> this.addIgnoredRoutes((String)versionId, (Set<String>)collection, factory));
            return this;
        }

        private void addIgnoredRoutes(@NotNull String versionId, @NotNull Set<String> routes, @NotNull RouteFactory factory) {
            Set<Route> set = this.ignored.computeIfAbsent(versionId, key -> new RouteSet()).getRouteSet();
            routes.forEach(route -> set.add(factory.create((String)route)));
        }

        private Builder setRelocationsInternal(@NotNull Map<String, RouteMap<Route, String>> relocations) {
            this.relocations.putAll(relocations);
            return this;
        }

        @Deprecated
        public Builder setRelocations(@NotNull Map<String, Map<Route, Route>> relocations) {
            relocations.forEach((versionId, map) -> this.relocations.computeIfAbsent((String)versionId, key -> new RouteMap()).getRouteMap().putAll(map));
            return this;
        }

        @Deprecated
        public Builder setRelocations(@NotNull String versionId, @NotNull Map<Route, Route> relocations) {
            this.relocations.computeIfAbsent(versionId, key -> new RouteMap()).getRouteMap().putAll(relocations);
            return this;
        }

        @Deprecated
        public Builder setStringRelocations(@NotNull Map<String, Map<String, String>> relocations) {
            relocations.forEach((versionId, map) -> this.relocations.computeIfAbsent((String)versionId, key -> new RouteMap()).getStringMap().putAll(map));
            return this;
        }

        @Deprecated
        public Builder setStringRelocations(@NotNull String versionId, @NotNull Map<String, String> relocations) {
            this.relocations.computeIfAbsent(versionId, key -> new RouteMap()).getStringMap().putAll(relocations);
            return this;
        }

        public Builder addRelocation(@NotNull String versionId, @NotNull Route fromRoute, @NotNull Route toRoute) {
            return this.addRelocations(versionId, Collections.singletonMap(fromRoute, toRoute));
        }

        public Builder addRelocations(@NotNull String versionId, @NotNull Map<Route, Route> relocations) {
            return this.addRelocations(Collections.singletonMap(versionId, relocations));
        }

        public Builder addRelocations(@NotNull Map<String, Map<Route, Route>> relocations) {
            relocations.forEach((versionId, map) -> this.relocations.computeIfAbsent((String)versionId, key -> new RouteMap()).getRouteMap().putAll(map));
            return this;
        }

        public Builder addRelocation(@NotNull String versionId, @NotNull String fromRoute, @NotNull String toRoute, char separator) {
            return this.addRelocations(versionId, Collections.singletonMap(fromRoute, toRoute), separator);
        }

        public Builder addRelocations(@NotNull String versionId, @NotNull Map<String, String> relocations, char separator) {
            this.addRelocations(Collections.singletonMap(versionId, relocations), separator);
            return this;
        }

        public Builder addRelocations(@NotNull Map<String, Map<String, String>> relocations, char separator) {
            RouteFactory factory = new RouteFactory(separator);
            relocations.forEach((versionId, collection) -> {
                Map map = this.relocations.computeIfAbsent((String)versionId, key -> new RouteMap()).getRouteMap();
                collection.forEach((from, to) -> map.put(factory.create((String)from), factory.create((String)to)));
            });
            return this;
        }

        public Builder addMapper(@NotNull String versionId, @NotNull Route route, @NotNull ValueMapper mapper) {
            return this.addMappers(versionId, Collections.singletonMap(route, mapper));
        }

        public Builder addMappers(@NotNull String versionId, @NotNull Map<Route, ValueMapper> mappers) {
            return this.addMappers(Collections.singletonMap(versionId, mappers));
        }

        public Builder addMappers(@NotNull Map<String, Map<Route, ValueMapper>> mappers) {
            mappers.forEach((versionId, map) -> this.mappers.computeIfAbsent((String)versionId, key -> new HashMap()).putAll(map));
            return this;
        }

        public Builder addMapper(@NotNull String versionId, @NotNull String route, @NotNull ValueMapper mapper, char separator) {
            return this.addMappers(versionId, Collections.singletonMap(route, mapper), separator);
        }

        public Builder addMappers(@NotNull String versionId, @NotNull Map<String, ValueMapper> mappers, char separator) {
            return this.addMappers(Collections.singletonMap(versionId, mappers), separator);
        }

        public Builder addMappers(@NotNull Map<String, Map<String, ValueMapper>> mappers, char separator) {
            RouteFactory factory = new RouteFactory(separator);
            mappers.forEach((versionId, collection) -> {
                Map map = this.mappers.computeIfAbsent((String)versionId, key -> new HashMap());
                collection.forEach((route, mapper) -> map.put(factory.create((String)route), mapper));
            });
            return this;
        }

        public Builder addCustomLogic(@NotNull String versionId, @NotNull Consumer<YamlDocument> consumer) {
            return this.addCustomLogic(versionId, Collections.singletonList(consumer));
        }

        public Builder addCustomLogic(@NotNull Map<String, List<Consumer<YamlDocument>>> consumers) {
            consumers.forEach(this::addCustomLogic);
            return this;
        }

        public Builder addCustomLogic(@NotNull String versionId, @NotNull Collection<Consumer<YamlDocument>> consumers) {
            this.customLogic.computeIfAbsent(versionId, key -> new ArrayList()).addAll(consumers);
            return this;
        }

        public Builder setVersioning(@NotNull Versioning versioning) {
            this.versioning = versioning;
            return this;
        }

        public Builder setVersioning(@NotNull Pattern pattern, @Nullable String documentVersionId, @NotNull String defaultsVersionId) {
            return this.setVersioning(new ManualVersioning(pattern, documentVersionId, defaultsVersionId));
        }

        public Builder setVersioning(@NotNull Pattern pattern, @NotNull Route route) {
            return this.setVersioning(new AutomaticVersioning(pattern, route));
        }

        public Builder setVersioning(@NotNull Pattern pattern, @NotNull String route) {
            return this.setVersioning(new AutomaticVersioning(pattern, route));
        }

        public UpdaterSettings build() {
            return new UpdaterSettings(this);
        }
    }

    public static enum OptionSorting {
        NONE,
        SORT_BY_DEFAULTS;

    }
}

