/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.MarkedYamlEngineException;
import java.util.Optional;

public class ConstructorException
extends MarkedYamlEngineException {
    public ConstructorException(String context, Optional<Mark> contextMark, String problem, Optional<Mark> problemMark, Throwable cause) {
        super(context, contextMark, problem, problemMark, cause);
    }

    public ConstructorException(String context, Optional<Mark> contextMark, String problem, Optional<Mark> problemMark) {
        this(context, contextMark, problem, problemMark, null);
    }
}

