/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.settings.loader;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.ConstructNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.LoadSettings;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.LoadSettingsBuilder;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.env.EnvConfig;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.schema.Schema;
import hu.geri.libs.boostedyaml.settings.Settings;
import hu.geri.libs.boostedyaml.settings.general.GeneralSettings;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LoaderSettings
implements Settings {
    public static final LoaderSettings DEFAULT = LoaderSettings.builder().build();
    private final LoadSettingsBuilder builder;
    private final boolean createFileIfAbsent;
    private final boolean autoUpdate;

    private LoaderSettings(Builder builder) {
        this.builder = builder.builder;
        this.autoUpdate = builder.autoUpdate;
        this.createFileIfAbsent = builder.createFileIfAbsent;
    }

    public boolean isAutoUpdate() {
        return this.autoUpdate;
    }

    public boolean isCreateFileIfAbsent() {
        return this.createFileIfAbsent;
    }

    public LoadSettings buildEngineSettings(GeneralSettings generalSettings) {
        return this.builder.setParseComments(true).setDefaultList(generalSettings::getDefaultList).setDefaultSet(generalSettings::getDefaultSet).setDefaultMap(generalSettings::getDefaultMap).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(LoadSettingsBuilder builder) {
        return new Builder(builder);
    }

    public static Builder builder(LoaderSettings settings) {
        return LoaderSettings.builder(settings.builder).setAutoUpdate(settings.autoUpdate).setCreateFileIfAbsent(settings.createFileIfAbsent);
    }

    public static class Builder {
        public static final boolean DEFAULT_CREATE_FILE_IF_ABSENT = true;
        public static final boolean DEFAULT_AUTO_UPDATE = false;
        public static final boolean DEFAULT_DETAILED_ERRORS = true;
        public static final boolean DEFAULT_ALLOW_DUPLICATE_KEYS = true;
        private final LoadSettingsBuilder builder;
        private boolean autoUpdate = false;
        private boolean createFileIfAbsent = true;

        private Builder(LoadSettingsBuilder builder) {
            this.builder = builder;
        }

        private Builder() {
            this.builder = LoadSettings.builder();
            this.setDetailedErrors(true);
            this.setAllowDuplicateKeys(true);
        }

        public Builder setCreateFileIfAbsent(boolean createFileIfAbsent) {
            this.createFileIfAbsent = createFileIfAbsent;
            return this;
        }

        public Builder setAutoUpdate(boolean autoUpdate) {
            this.autoUpdate = autoUpdate;
            return this;
        }

        public Builder setErrorLabel(@NotNull String label) {
            this.builder.setLabel(label);
            return this;
        }

        public Builder setDetailedErrors(boolean detailedErrors) {
            this.builder.setUseMarks(detailedErrors);
            return this;
        }

        public Builder setAllowDuplicateKeys(boolean allowDuplicateKeys) {
            this.builder.setAllowDuplicateKeys(allowDuplicateKeys);
            return this;
        }

        public Builder setMaxCollectionAliases(int maxCollectionAliases) {
            this.builder.setMaxAliasesForCollections(maxCollectionAliases);
            return this;
        }

        public Builder setTagConstructors(@NotNull Map<Tag, ConstructNode> constructors) {
            this.builder.setTagConstructors(constructors);
            return this;
        }

        public Builder setSchema(@NotNull Schema schema) {
            this.builder.setSchema(schema);
            return this;
        }

        public Builder setEnvironmentConfig(@Nullable EnvConfig envConfig) {
            this.builder.setEnvConfig(Optional.ofNullable(envConfig));
            return this;
        }

        public LoaderSettings build() {
            return new LoaderSettings(this);
        }
    }
}

