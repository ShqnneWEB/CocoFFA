/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.config;

import hu.geri.libs.boostedyaml.YamlDocument;
import hu.geri.libs.boostedyaml.block.implementation.Section;
import hu.geri.libs.boostedyaml.settings.dumper.DumperSettings;
import hu.geri.libs.boostedyaml.settings.general.GeneralSettings;
import hu.geri.libs.boostedyaml.settings.loader.LoaderSettings;
import hu.geri.libs.boostedyaml.settings.updater.UpdaterSettings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Config {
    private YamlDocument configuration;

    public Config(File file) {
        this(file, null);
    }

    public Config(File file, InputStream defaults) {
        this(file, defaults, GeneralSettings.DEFAULT, LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.DEFAULT);
    }

    public Config(File file, InputStream defaults, GeneralSettings generalSettings, LoaderSettings loaderSettings, DumperSettings dumperSettings, UpdaterSettings updaterSettings) {
        try {
            this.configuration = YamlDocument.create(file, defaults, generalSettings, loaderSettings, dumperSettings, updaterSettings);
        } catch (Exception e) {
            System.err.println("Failed to load config file: " + file.getName() + " - " + e.getMessage());
        }
    }

    public <T> T get(String route) {
        return (T)this.configuration.get(route);
    }

    public <T> T get(String route, T def) {
        return (T)this.configuration.get(route, def);
    }

    public void set(String key, Object value) {
        this.configuration.set(key, value);
    }

    public void remove(String key) {
        this.configuration.remove(key);
    }

    public boolean getBoolean(String key) {
        return this.configuration.getBoolean(key);
    }

    public String getString(String key) {
        return this.configuration.getString(key);
    }

    public int getInt(String key) {
        return this.configuration.getInt(key);
    }

    public int getInt(String key, int def) {
        return this.configuration.getInt(key, (Integer)def);
    }

    public long getLong(String key) {
        return this.configuration.getLong(key);
    }

    public long getLong(String key, long def) {
        return this.configuration.getLong(key, (Long)def);
    }

    public List<String> getStringList(String key) {
        return this.getList(key);
    }

    public List<String> getStringList(String key, List<String> def) {
        return this.getList(key, def);
    }

    public <T> List<T> getList(String key) {
        return this.configuration.getList(key);
    }

    public <T> List<T> getList(String key, List<T> def) {
        return this.configuration.getList(key, def);
    }

    public double getDouble(String key) {
        return this.configuration.getDouble(key);
    }

    public double getDouble(String key, double def) {
        return this.configuration.getDouble(key, (Double)def);
    }

    public float getFloat(String key, float def) {
        return this.configuration.getFloat(key, Float.valueOf(def)).floatValue();
    }

    public String getString(String key, String def) {
        return this.configuration.getString(key, def);
    }

    public boolean getBoolean(String key, boolean def) {
        return this.configuration.getBoolean(key, (Boolean)def);
    }

    public float getFloat(String key) {
        return this.configuration.getFloat(key).floatValue();
    }

    public <T, U> List<Map<T, U>> getMapList(String key) {
        ArrayList<Map<T, U>> listMap = new ArrayList<Map<T, U>>();
        List<Map<?, ?>> list = this.configuration.getMapList(key);
        for (Map<?, ?> map : list) {
            HashMap hashMap = new HashMap();
            map.forEach((k, v) -> hashMap.put(k, v));
            listMap.add(hashMap);
        }
        return listMap;
    }

    public Section getSection(String key) {
        return this.configuration.getSection(key);
    }

    public <T> Optional<T> getOptional(String key) {
        return this.configuration.getOptional(key);
    }

    public Set<String> getKeys(boolean deep) {
        if (this.configuration == null) {
            return new HashSet<String>();
        }
        return this.configuration.getRoutesAsStrings(deep);
    }

    public Set<String> getKeys() {
        if (this.configuration == null) {
            return new HashSet<String>();
        }
        return this.configuration.getRoutesAsStrings(false);
    }

    public void reload() {
        try {
            this.configuration.reload();
        } catch (Exception exception) {
            // empty catch block
        }
    }

    public void save() {
        try {
            this.configuration.save();
        } catch (IOException iOException) {
            // empty catch block
        }
    }

    public YamlDocument getBackingDocument() {
        return this.configuration;
    }

    public boolean contains(String key) {
        return this.configuration.contains(key);
    }

    public List<Integer> getIntegerList(String key) {
        List<?> list = this.configuration.getList(key);
        if (list == null) {
            return new ArrayList<Integer>();
        }
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (Object obj : list) {
            if (obj instanceof Number) {
                result.add(((Number)obj).intValue());
                continue;
            }
            if (!(obj instanceof String)) continue;
            try {
                result.add(Integer.parseInt((String)obj));
            } catch (NumberFormatException numberFormatException) {}
        }
        return result;
    }
}

