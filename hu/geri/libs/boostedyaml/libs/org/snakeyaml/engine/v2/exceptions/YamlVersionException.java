/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.SpecVersion;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;

public class YamlVersionException
extends YamlEngineException {
    private final SpecVersion specVersion;

    public YamlVersionException(SpecVersion specVersion) {
        super(specVersion.toString());
        this.specVersion = specVersion;
    }

    public SpecVersion getSpecVersion() {
        return this.specVersion;
    }
}

