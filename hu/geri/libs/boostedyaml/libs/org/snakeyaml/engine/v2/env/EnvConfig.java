/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.env;

import java.util.Optional;

public interface EnvConfig {
    default public Optional<String> getValueFor(String name, String separator, String value, String environment) {
        return Optional.empty();
    }
}

