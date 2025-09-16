/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.updater;

import hu.geri.libs.boostedyaml.block.implementation.Section;
import hu.geri.libs.boostedyaml.dvs.Version;
import hu.geri.libs.boostedyaml.dvs.versioning.Versioning;
import hu.geri.libs.boostedyaml.route.Route;
import hu.geri.libs.boostedyaml.settings.updater.UpdaterSettings;
import hu.geri.libs.boostedyaml.updater.operators.Mapper;
import hu.geri.libs.boostedyaml.updater.operators.Relocator;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class VersionedOperations {
    public static boolean run(@NotNull Section document, @NotNull Section defaults, @NotNull UpdaterSettings settings, char separator) {
        int compared;
        Versioning versioning = settings.getVersioning();
        if (versioning == null) {
            return false;
        }
        Version documentVersion = versioning.getDocumentVersion(document, false);
        Version defaultsVersion = Objects.requireNonNull(versioning.getDocumentVersion(defaults, true), "Version ID of the defaults cannot be null! Is it malformed or not specified?");
        int n = compared = documentVersion != null ? documentVersion.compareTo(defaultsVersion) : -1;
        if (compared > 0 && !settings.isEnableDowngrading()) {
            throw new UnsupportedOperationException(String.format("Downgrading is not enabled (%s > %s)!", defaultsVersion.asID(), documentVersion.asID()));
        }
        if (compared == 0) {
            return true;
        }
        if (compared < 0) {
            VersionedOperations.iterate(document, documentVersion != null ? documentVersion : versioning.getFirstVersion(), defaultsVersion, settings, separator);
        }
        settings.getIgnoredRoutes(defaultsVersion.asID(), separator).forEach(route -> document.getOptionalBlock((Route)route).ifPresent(block -> block.setIgnored(true)));
        return false;
    }

    private static void iterate(@NotNull Section document, @NotNull Version documentVersion, @NotNull Version defaultsVersion, @NotNull UpdaterSettings settings, char separator) {
        Version current = documentVersion.copy();
        while (current.compareTo(defaultsVersion) <= 0) {
            current.next();
            Relocator.apply(document, settings.getRelocations(current.asID(), separator));
            Mapper.apply(document, settings.getMappers(current.asID(), separator));
            settings.getCustomLogic(current.asID()).forEach(consumer -> consumer.accept(document.getRoot()));
        }
    }
}

