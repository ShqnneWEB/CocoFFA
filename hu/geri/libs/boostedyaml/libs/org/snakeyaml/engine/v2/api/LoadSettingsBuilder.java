/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.ConstructNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.LoadSettings;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.SettingKey;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.SpecVersion;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.env.EnvConfig;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.YamlVersionException;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.schema.JsonSchema;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.schema.Schema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

public final class LoadSettingsBuilder {
    private final Map<SettingKey, Object> customProperties = new HashMap<SettingKey, Object>();
    private String label = "reader";
    private Map<Tag, ConstructNode> tagConstructors = new HashMap<Tag, ConstructNode>();
    private IntFunction<List<Object>> defaultList = ArrayList::new;
    private IntFunction<Set<Object>> defaultSet = LinkedHashSet::new;
    private IntFunction<Map<Object, Object>> defaultMap = LinkedHashMap::new;
    private UnaryOperator<SpecVersion> versionFunction = version -> {
        if (version.getMajor() != 1) {
            throw new YamlVersionException((SpecVersion)version);
        }
        return version;
    };
    private Integer bufferSize = 1024;
    private boolean allowDuplicateKeys = false;
    private boolean allowRecursiveKeys = false;
    private boolean parseComments = false;
    private int maxAliasesForCollections = 50;
    private boolean useMarks = true;
    private Optional<EnvConfig> envConfig = Optional.empty();
    private int codePointLimit = 0x300000;
    private Schema schema = new JsonSchema();

    LoadSettingsBuilder() {
    }

    public LoadSettingsBuilder setLabel(String label) {
        Objects.requireNonNull(label, "label cannot be null");
        this.label = label;
        return this;
    }

    public LoadSettingsBuilder setTagConstructors(Map<Tag, ConstructNode> tagConstructors) {
        this.tagConstructors = tagConstructors;
        return this;
    }

    public LoadSettingsBuilder setDefaultList(IntFunction<List<Object>> defaultList) {
        Objects.requireNonNull(defaultList, "defaultList cannot be null");
        this.defaultList = defaultList;
        return this;
    }

    public LoadSettingsBuilder setDefaultSet(IntFunction<Set<Object>> defaultSet) {
        Objects.requireNonNull(defaultSet, "defaultSet cannot be null");
        this.defaultSet = defaultSet;
        return this;
    }

    public LoadSettingsBuilder setDefaultMap(IntFunction<Map<Object, Object>> defaultMap) {
        Objects.requireNonNull(defaultMap, "defaultMap cannot be null");
        this.defaultMap = defaultMap;
        return this;
    }

    public LoadSettingsBuilder setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    public LoadSettingsBuilder setAllowDuplicateKeys(boolean allowDuplicateKeys) {
        this.allowDuplicateKeys = allowDuplicateKeys;
        return this;
    }

    public LoadSettingsBuilder setAllowRecursiveKeys(boolean allowRecursiveKeys) {
        this.allowRecursiveKeys = allowRecursiveKeys;
        return this;
    }

    public LoadSettingsBuilder setMaxAliasesForCollections(int maxAliasesForCollections) {
        this.maxAliasesForCollections = maxAliasesForCollections;
        return this;
    }

    public LoadSettingsBuilder setUseMarks(boolean useMarks) {
        this.useMarks = useMarks;
        return this;
    }

    public LoadSettingsBuilder setVersionFunction(UnaryOperator<SpecVersion> versionFunction) {
        Objects.requireNonNull(versionFunction, "versionFunction cannot be null");
        this.versionFunction = versionFunction;
        return this;
    }

    public LoadSettingsBuilder setEnvConfig(Optional<EnvConfig> envConfig) {
        this.envConfig = envConfig;
        return this;
    }

    public LoadSettingsBuilder setCustomProperty(SettingKey key, Object value) {
        this.customProperties.put(key, value);
        return this;
    }

    public LoadSettingsBuilder setParseComments(boolean parseComments) {
        this.parseComments = parseComments;
        return this;
    }

    public LoadSettingsBuilder setCodePointLimit(int codePointLimit) {
        this.codePointLimit = codePointLimit;
        return this;
    }

    public LoadSettingsBuilder setSchema(Schema schema) {
        this.schema = schema;
        return this;
    }

    public LoadSettings build() {
        return new LoadSettings(this.label, this.tagConstructors, this.defaultList, this.defaultSet, this.defaultMap, this.versionFunction, this.bufferSize, this.allowDuplicateKeys, this.allowRecursiveKeys, this.maxAliasesForCollections, this.useMarks, this.customProperties, this.envConfig, this.parseComments, this.codePointLimit, this.schema);
    }
}

