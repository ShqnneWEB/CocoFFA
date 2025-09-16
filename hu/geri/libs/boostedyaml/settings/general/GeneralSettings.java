/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.settings.general;

import hu.geri.libs.boostedyaml.serialization.YamlSerializer;
import hu.geri.libs.boostedyaml.serialization.standard.StandardSerializer;
import hu.geri.libs.boostedyaml.settings.Settings;
import hu.geri.libs.boostedyaml.utils.supplier.ListSupplier;
import hu.geri.libs.boostedyaml.utils.supplier.MapSupplier;
import hu.geri.libs.boostedyaml.utils.supplier.SetSupplier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeneralSettings
implements Settings {
    public static final char DEFAULT_ROUTE_SEPARATOR = '.';
    public static final String DEFAULT_ESCAPED_SEPARATOR = Pattern.quote(String.valueOf('.'));
    public static final KeyFormat DEFAULT_KEY_FORMATTING = KeyFormat.STRING;
    public static final YamlSerializer DEFAULT_SERIALIZER = StandardSerializer.getDefault();
    public static final boolean DEFAULT_USE_DEFAULTS = true;
    public static final Object DEFAULT_OBJECT = null;
    public static final Number DEFAULT_NUMBER = 0;
    public static final String DEFAULT_STRING = null;
    public static final Character DEFAULT_CHAR = Character.valueOf(' ');
    public static final Boolean DEFAULT_BOOLEAN = false;
    public static final ListSupplier DEFAULT_LIST = ArrayList::new;
    public static final SetSupplier DEFAULT_SET = LinkedHashSet::new;
    public static final MapSupplier DEFAULT_MAP = LinkedHashMap::new;
    public static final GeneralSettings DEFAULT = GeneralSettings.builder().build();
    private final KeyFormat keyFormat;
    private final char separator;
    private final String escapedSeparator;
    private final YamlSerializer serializer;
    private final boolean useDefaults;
    private final Object defaultObject;
    private final Number defaultNumber;
    private final String defaultString;
    private final Character defaultChar;
    private final Boolean defaultBoolean;
    private final ListSupplier defaultList;
    private final SetSupplier defaultSet;
    private final MapSupplier defaultMap;

    private GeneralSettings(Builder builder) {
        this.keyFormat = builder.keyFormat;
        this.separator = builder.routeSeparator;
        this.escapedSeparator = Pattern.quote(String.valueOf(this.separator));
        this.serializer = builder.serializer;
        this.defaultObject = builder.defaultObject;
        this.defaultNumber = builder.defaultNumber;
        this.defaultString = builder.defaultString;
        this.defaultChar = builder.defaultChar;
        this.defaultBoolean = builder.defaultBoolean;
        this.defaultList = builder.defaultList;
        this.defaultSet = builder.defaultSet;
        this.defaultMap = builder.defaultMap;
        this.useDefaults = builder.useDefaults;
    }

    public KeyFormat getKeyFormat() {
        return this.keyFormat;
    }

    public char getRouteSeparator() {
        return this.separator;
    }

    public String getEscapedSeparator() {
        return this.escapedSeparator;
    }

    public YamlSerializer getSerializer() {
        return this.serializer;
    }

    public boolean isUseDefaults() {
        return this.useDefaults;
    }

    public Object getDefaultObject() {
        return this.defaultObject;
    }

    public String getDefaultString() {
        return this.defaultString;
    }

    public Character getDefaultChar() {
        return this.defaultChar;
    }

    public Number getDefaultNumber() {
        return this.defaultNumber;
    }

    public Boolean getDefaultBoolean() {
        return this.defaultBoolean;
    }

    public <T> List<T> getDefaultList(int size) {
        return this.defaultList.supply(size);
    }

    public <T> List<T> getDefaultList() {
        return this.getDefaultList(0);
    }

    public <T> Set<T> getDefaultSet(int size) {
        return this.defaultSet.supply(size);
    }

    public <T> Set<T> getDefaultSet() {
        return this.getDefaultSet(0);
    }

    public <K, V> Map<K, V> getDefaultMap(int size) {
        return this.defaultMap.supply(size);
    }

    public <K, V> Map<K, V> getDefaultMap() {
        return this.getDefaultMap(0);
    }

    public MapSupplier getDefaultMapSupplier() {
        return this.defaultMap;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(GeneralSettings settings) {
        return GeneralSettings.builder().setKeyFormat(settings.keyFormat).setRouteSeparator(settings.separator).setSerializer(settings.serializer).setUseDefaults(settings.useDefaults).setDefaultObject(settings.defaultObject).setDefaultNumber(settings.defaultNumber).setDefaultString(settings.defaultString).setDefaultChar(settings.defaultChar).setDefaultBoolean(settings.defaultBoolean).setDefaultList(settings.defaultList).setDefaultSet(settings.defaultSet).setDefaultMap(settings.defaultMap);
    }

    public static class Builder {
        private KeyFormat keyFormat = DEFAULT_KEY_FORMATTING;
        private char routeSeparator = (char)46;
        private YamlSerializer serializer = DEFAULT_SERIALIZER;
        private boolean useDefaults = true;
        private Object defaultObject = DEFAULT_OBJECT;
        private Number defaultNumber = DEFAULT_NUMBER;
        private String defaultString = DEFAULT_STRING;
        private Character defaultChar = DEFAULT_CHAR;
        private Boolean defaultBoolean = DEFAULT_BOOLEAN;
        private ListSupplier defaultList = DEFAULT_LIST;
        private SetSupplier defaultSet = DEFAULT_SET;
        private MapSupplier defaultMap = DEFAULT_MAP;

        private Builder() {
        }

        public Builder setKeyFormat(@NotNull KeyFormat keyFormat) {
            this.keyFormat = keyFormat;
            return this;
        }

        public Builder setRouteSeparator(char separator) {
            this.routeSeparator = separator;
            return this;
        }

        public Builder setSerializer(@NotNull YamlSerializer serializer) {
            this.serializer = serializer;
            return this;
        }

        public Builder setUseDefaults(boolean useDefaults) {
            this.useDefaults = useDefaults;
            return this;
        }

        public Builder setDefaultObject(@Nullable Object defaultObject) {
            this.defaultObject = defaultObject;
            return this;
        }

        public Builder setDefaultNumber(@NotNull Number defaultNumber) {
            this.defaultNumber = defaultNumber;
            return this;
        }

        public Builder setDefaultString(@Nullable String defaultString) {
            this.defaultString = defaultString;
            return this;
        }

        public Builder setDefaultChar(@Nullable Character defaultChar) {
            this.defaultChar = defaultChar;
            return this;
        }

        public Builder setDefaultBoolean(@Nullable Boolean defaultBoolean) {
            this.defaultBoolean = defaultBoolean;
            return this;
        }

        public Builder setDefaultList(@NotNull ListSupplier defaultList) {
            this.defaultList = defaultList;
            return this;
        }

        public Builder setDefaultSet(@NotNull SetSupplier defaultSet) {
            this.defaultSet = defaultSet;
            return this;
        }

        public Builder setDefaultMap(@NotNull MapSupplier defaultMap) {
            this.defaultMap = defaultMap;
            return this;
        }

        public GeneralSettings build() {
            return new GeneralSettings(this);
        }
    }

    public static enum KeyFormat {
        STRING,
        OBJECT;

    }
}

