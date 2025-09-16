/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.updater;

import hu.geri.libs.boostedyaml.block.implementation.Section;
import hu.geri.libs.boostedyaml.settings.general.GeneralSettings;
import hu.geri.libs.boostedyaml.settings.updater.UpdaterSettings;
import hu.geri.libs.boostedyaml.updater.VersionedOperations;
import hu.geri.libs.boostedyaml.updater.operators.Merger;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class Updater {
    public static void update(@NotNull Section document, @NotNull Section defaults, @NotNull UpdaterSettings updaterSettings, @NotNull GeneralSettings generalSettings) throws IOException {
        if (VersionedOperations.run(document, defaults, updaterSettings, generalSettings.getRouteSeparator())) {
            return;
        }
        Merger.merge(document, defaults, updaterSettings);
        if (updaterSettings.getVersioning() != null) {
            updaterSettings.getVersioning().updateVersionID(document, defaults);
        }
        if (updaterSettings.isAutoSave()) {
            document.getRoot().save();
        }
    }
}

