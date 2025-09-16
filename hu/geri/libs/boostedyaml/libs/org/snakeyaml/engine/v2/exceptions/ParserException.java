/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.MarkedYamlEngineException;
import java.util.Optional;

public class ParserException
extends MarkedYamlEngineException {
    public ParserException(String context, Optional<Mark> contextMark, String problem, Optional<Mark> problemMark) {
        super(context, contextMark, problem, problemMark, null);
    }

    public ParserException(String problem, Optional<Mark> problemMark) {
        super(null, Optional.empty(), problem, problemMark, null);
    }
}

