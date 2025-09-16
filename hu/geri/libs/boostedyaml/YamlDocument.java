/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml;

import hu.geri.libs.boostedyaml.block.implementation.Section;
import hu.geri.libs.boostedyaml.engine.ExtendedConstructor;
import hu.geri.libs.boostedyaml.engine.ExtendedRepresenter;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.DumpSettings;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.LoadSettings;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.StreamDataWriter;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.YamlUnicodeReader;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.composer.Composer;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.emitter.Emitter;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.parser.ParserImpl;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.scanner.StreamReader;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.serializer.Serializer;
import hu.geri.libs.boostedyaml.settings.Settings;
import hu.geri.libs.boostedyaml.settings.dumper.DumperSettings;
import hu.geri.libs.boostedyaml.settings.general.GeneralSettings;
import hu.geri.libs.boostedyaml.settings.loader.LoaderSettings;
import hu.geri.libs.boostedyaml.settings.updater.UpdaterSettings;
import hu.geri.libs.boostedyaml.updater.Updater;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class YamlDocument
extends Section {
    private final File file;
    private final YamlDocument defaults;
    private GeneralSettings generalSettings;
    private LoaderSettings loaderSettings;
    private DumperSettings dumperSettings;
    private UpdaterSettings updaterSettings;

    protected YamlDocument(@NotNull InputStream document, @Nullable InputStream defaults, @NotNull Settings ... settings) throws IOException {
        super(Collections.emptyMap());
        this.setSettingsInternal(settings);
        this.setValue(this.generalSettings.getDefaultMap());
        this.file = null;
        this.defaults = defaults == null ? null : new YamlDocument(defaults, null, settings);
        this.reload(document);
    }

    protected YamlDocument(@NotNull File document, @Nullable InputStream defaults, @NotNull Settings ... settings) throws IOException {
        super(Collections.emptyMap());
        this.setSettingsInternal(settings);
        this.setValue(this.generalSettings.getDefaultMap());
        this.file = document;
        this.defaults = defaults == null ? null : new YamlDocument(defaults, null, this.generalSettings, this.loaderSettings, this.dumperSettings, this.updaterSettings);
        this.reload();
    }

    private void setSettingsInternal(@NotNull Settings ... settings) {
        for (Settings obj : settings) {
            if (obj instanceof GeneralSettings) {
                if (this.generalSettings != null && this.generalSettings.getKeyFormat() != ((GeneralSettings)obj).getKeyFormat()) {
                    throw new IllegalArgumentException("Cannot change the key format! Recreate the file if needed to do so.");
                }
                this.generalSettings = (GeneralSettings)obj;
                continue;
            }
            if (obj instanceof LoaderSettings) {
                this.loaderSettings = (LoaderSettings)obj;
                continue;
            }
            if (obj instanceof DumperSettings) {
                this.dumperSettings = (DumperSettings)obj;
                continue;
            }
            if (obj instanceof UpdaterSettings) {
                this.updaterSettings = (UpdaterSettings)obj;
                continue;
            }
            throw new IllegalArgumentException("Unknown settings object!");
        }
        this.generalSettings = this.generalSettings == null ? GeneralSettings.DEFAULT : this.generalSettings;
        this.loaderSettings = this.loaderSettings == null ? LoaderSettings.DEFAULT : this.loaderSettings;
        this.dumperSettings = this.dumperSettings == null ? DumperSettings.DEFAULT : this.dumperSettings;
        this.updaterSettings = this.updaterSettings == null ? UpdaterSettings.DEFAULT : this.updaterSettings;
    }

    public boolean reload() throws IOException {
        if (this.file == null) {
            return false;
        }
        this.reload(this.file);
        return true;
    }

    private void reload(@NotNull File file) throws IOException {
        this.clear();
        if (Objects.requireNonNull(file, "File cannot be null!").exists()) {
            this.reload(new BufferedInputStream(new FileInputStream(file)));
            return;
        }
        if (this.loaderSettings.isCreateFileIfAbsent()) {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        if (this.defaults == null) {
            this.initEmpty(this);
            return;
        }
        String dump = this.defaults.dump();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(file, false), StandardCharsets.UTF_8));){
            writer.write(dump);
        }
        this.reload(new BufferedInputStream(new ByteArrayInputStream(dump.getBytes(StandardCharsets.UTF_8))));
    }

    public void reload(@NotNull InputStream inputStream) throws IOException {
        this.reload(inputStream, this.loaderSettings);
    }

    public void reload(@NotNull InputStream inputStream, @NotNull LoaderSettings loaderSettings) throws IOException {
        this.clear();
        LoadSettings settings = Objects.requireNonNull(loaderSettings, "Loader settings cannot be null!").buildEngineSettings(this.generalSettings);
        ExtendedConstructor constructor = new ExtendedConstructor(settings, this.generalSettings.getSerializer());
        ParserImpl parser = new ParserImpl(settings, new StreamReader(settings, new YamlUnicodeReader(Objects.requireNonNull(inputStream, "Input stream cannot be null!"))));
        Composer composer = new Composer(settings, parser);
        if (composer.hasNext()) {
            Node node = composer.next();
            if (composer.hasNext()) {
                throw new InvalidObjectException("Multiple documents are not supported!");
            }
            if (!(node instanceof MappingNode)) {
                throw new IllegalArgumentException(String.format("Top level object is not a map! Parsed node: %s", node.toString()));
            }
            constructor.constructSingleDocument(Optional.of(node));
            this.init(this, null, (MappingNode)node, constructor);
            constructor.clear();
        } else {
            this.initEmpty(this);
        }
        if (this.file != null && loaderSettings.isCreateFileIfAbsent() && !this.file.exists()) {
            if (this.file.getParentFile() != null) {
                this.file.getParentFile().mkdirs();
            }
            this.file.createNewFile();
            this.save();
        }
        if (this.defaults != null && loaderSettings.isAutoUpdate()) {
            Updater.update(this, this.defaults, this.updaterSettings, this.generalSettings);
        }
    }

    public boolean update() throws IOException {
        return this.update(this.updaterSettings);
    }

    public boolean update(@NotNull UpdaterSettings updaterSettings) throws IOException {
        if (this.defaults == null) {
            return false;
        }
        Updater.update(this, this.defaults, Objects.requireNonNull(updaterSettings, "Updater settings cannot be null!"), this.generalSettings);
        return true;
    }

    public void update(@NotNull InputStream defaults) throws IOException {
        this.update(defaults, this.updaterSettings);
    }

    public void update(@NotNull InputStream defaults, @NotNull UpdaterSettings updaterSettings) throws IOException {
        Updater.update(this, YamlDocument.create(Objects.requireNonNull(defaults, "Defaults cannot be null!"), this.generalSettings, this.loaderSettings, this.dumperSettings, UpdaterSettings.DEFAULT), Objects.requireNonNull(updaterSettings, "Updater settings cannot be null!"), this.generalSettings);
    }

    public boolean save() throws IOException {
        if (this.file == null) {
            return false;
        }
        this.save(this.file);
        return true;
    }

    public void save(@NotNull File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(file, false), StandardCharsets.UTF_8));){
            writer.write(this.dump());
        }
    }

    public void save(@NotNull OutputStream stream, Charset charset) throws IOException {
        stream.write(this.dump().getBytes(charset));
    }

    public void save(@NotNull OutputStreamWriter writer) throws IOException {
        writer.write(this.dump());
    }

    public String dump() {
        return this.dump(this.dumperSettings);
    }

    public String dump(@NotNull DumperSettings dumperSettings) {
        DumpSettings settings = dumperSettings.buildEngineSettings();
        SerializedStream stream = new SerializedStream();
        ExtendedRepresenter representer = new ExtendedRepresenter(this.generalSettings, dumperSettings, settings);
        Serializer serializer = new Serializer(settings, new Emitter(settings, stream));
        serializer.emitStreamStart();
        serializer.serializeDocument(representer.represent(this));
        serializer.emitStreamEnd();
        return stream.toString();
    }

    public void setSettings(@NotNull Settings ... settings) {
        this.setSettingsInternal(settings);
    }

    @Deprecated
    public void setLoaderSettings(@NotNull LoaderSettings loaderSettings) {
        this.loaderSettings = loaderSettings;
    }

    public void setDumperSettings(@NotNull DumperSettings dumperSettings) {
        this.dumperSettings = dumperSettings;
    }

    public void setGeneralSettings(@NotNull GeneralSettings generalSettings) {
        if (generalSettings.getKeyFormat() != this.generalSettings.getKeyFormat()) {
            throw new IllegalArgumentException("Cannot change key format! Recreate the file if needed to do so.");
        }
        this.generalSettings = generalSettings;
    }

    public void setUpdaterSettings(@NotNull UpdaterSettings updaterSettings) {
        this.updaterSettings = updaterSettings;
    }

    @Override
    @Nullable
    public YamlDocument getDefaults() {
        return this.defaults;
    }

    @NotNull
    public GeneralSettings getGeneralSettings() {
        return this.generalSettings;
    }

    @NotNull
    public DumperSettings getDumperSettings() {
        return this.dumperSettings;
    }

    @NotNull
    public UpdaterSettings getUpdaterSettings() {
        return this.updaterSettings;
    }

    @NotNull
    public LoaderSettings getLoaderSettings() {
        return this.loaderSettings;
    }

    @Nullable
    public File getFile() {
        return this.file;
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    public static YamlDocument create(@NotNull File document, @NotNull InputStream defaults, @NotNull Settings ... settings) throws IOException {
        return new YamlDocument(document, defaults, settings);
    }

    public static YamlDocument create(@NotNull InputStream document, @NotNull InputStream defaults, @NotNull Settings ... settings) throws IOException {
        return new YamlDocument(document, defaults, settings);
    }

    public static YamlDocument create(@NotNull File document, @NotNull Settings ... settings) throws IOException {
        return new YamlDocument(document, null, settings);
    }

    public static YamlDocument create(@NotNull InputStream document, @NotNull Settings ... settings) throws IOException {
        return new YamlDocument(document, null, settings);
    }

    private static class SerializedStream
    extends StringWriter
    implements StreamDataWriter {
        private SerializedStream() {
        }
    }
}

