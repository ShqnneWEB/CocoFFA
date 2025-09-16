/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.ConstructNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.LoadSettingsBuilder;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.SettingKey;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.SpecVersion;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.env.EnvConfig;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.schema.Schema;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

public final class LoadSettings {
    private final String label;
    private final Map<Tag, ConstructNode> tagConstructors;
    private final IntFunction<List<Object>> defaultList;
    private final IntFunction<Set<Object>> defaultSet;
    private final IntFunction<Map<Object, Object>> defaultMap;
    private final UnaryOperator<SpecVersion> versionFunction;
    private final Integer bufferSize;
    private final boolean allowDuplicateKeys;
    private final boolean allowRecursiveKeys;
    private final boolean parseComments;
    private final int maxAliasesForCollections;
    private final boolean useMarks;
    private final Optional<EnvConfig> envConfig;
    private final int codePointLimit;
    private final Schema schema;
    private final Map<SettingKey, Object> customProperties;

    LoadSettings(String label, Map<Tag, ConstructNode> tagConstructors, IntFunction<List<Object>> defaultList, IntFunction<Set<Object>> defaultSet, IntFunction<Map<Object, Object>> defaultMap, UnaryOperator<SpecVersion> versionFunction, Integer bufferSize, boolean allowDuplicateKeys, boolean allowRecursiveKeys, int maxAliasesForCollections, boolean useMarks, Map<SettingKey, Object> customProperties, Optional<EnvConfig> envConfig, boolean parseComments, int codePointLimit, Schema schema) {
        this.label = label;
        this.tagConstructors = tagConstructors;
        this.defaultList = defaultList;
        this.defaultSet = defaultSet;
        this.defaultMap = defaultMap;
        this.versionFunction = versionFunction;
        this.bufferSize = bufferSize;
        this.allowDuplicateKeys = allowDuplicateKeys;
        this.allowRecursiveKeys = allowRecursiveKeys;
        this.parseComments = parseComments;
        this.maxAliasesForCollections = maxAliasesForCollections;
        this.useMarks = useMarks;
        this.customProperties = customProperties;
        this.envConfig = envConfig;
        this.codePointLimit = codePointLimit;
        this.schema = schema;
    }

    public static LoadSettingsBuilder builder() {
        return new LoadSettingsBuilder();
    }

    public String getLabel() {
        return this.label;
    }

    public Map<Tag, ConstructNode> getTagConstructors() {
        return this.tagConstructors;
    }

    public IntFunction<List<Object>> getDefaultList() {
        return this.defaultList;
    }

    public IntFunction<Set<Object>> getDefaultSet() {
        return this.defaultSet;
    }

    public IntFunction<Map<Object, Object>> getDefaultMap() {
        return this.defaultMap;
    }

    public Integer getBufferSize() {
        return this.bufferSize;
    }

    public boolean getAllowDuplicateKeys() {
        return this.allowDuplicateKeys;
    }

    public boolean getAllowRecursiveKeys() {
        return this.allowRecursiveKeys;
    }

    public boolean getUseMarks() {
        return this.useMarks;
    }

    public Function<SpecVersion, SpecVersion> getVersionFunction() {
        return this.versionFunction;
    }

    public Object getCustomProperty(SettingKey key) {
        return this.customProperties.get(key);
    }

    public int getMaxAliasesForCollections() {
        return this.maxAliasesForCollections;
    }

    public Optional<EnvConfig> getEnvConfig() {
        return this.envConfig;
    }

    public boolean getParseComments() {
        return this.parseComments;
    }

    public int getCodePointLimit() {
        return this.codePointLimit;
    }

    public Schema getSchema() {
        return this.schema;
    }
}

