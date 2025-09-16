/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.ConstructorException;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Optional;

public class DuplicateKeyException
extends ConstructorException {
    public DuplicateKeyException(Optional<Mark> contextMark, Object key, Optional<Mark> problemMark) {
        super("while constructing a mapping", contextMark, "found duplicate key " + key.toString(), problemMark);
    }
}

