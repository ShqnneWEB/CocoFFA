/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import java.util.Objects;
import java.util.Optional;

public class MarkedYamlEngineException
extends YamlEngineException {
    private final String context;
    private final Optional<Mark> contextMark;
    private final String problem;
    private final Optional<Mark> problemMark;

    protected MarkedYamlEngineException(String context, Optional<Mark> contextMark, String problem, Optional<Mark> problemMark, Throwable cause) {
        super(context + "; " + problem + "; " + problemMark, cause);
        Objects.requireNonNull(contextMark, "contextMark must be provided");
        Objects.requireNonNull(problemMark, "problemMark must be provided");
        this.context = context;
        this.contextMark = contextMark;
        this.problem = problem;
        this.problemMark = problemMark;
    }

    protected MarkedYamlEngineException(String context, Optional<Mark> contextMark, String problem, Optional<Mark> problemMark) {
        this(context, contextMark, problem, problemMark, null);
    }

    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        StringBuilder lines = new StringBuilder();
        if (this.context != null) {
            lines.append(this.context);
            lines.append("\n");
        }
        if (this.contextMark.isPresent() && (this.problem == null || !this.problemMark.isPresent() || this.contextMark.get().getName().equals(this.problemMark.get().getName()) || this.contextMark.get().getLine() != this.problemMark.get().getLine() || this.contextMark.get().getColumn() != this.problemMark.get().getColumn())) {
            lines.append(this.contextMark.get());
            lines.append("\n");
        }
        if (this.problem != null) {
            lines.append(this.problem);
            lines.append("\n");
        }
        if (this.problemMark.isPresent()) {
            lines.append(this.problemMark.get());
            lines.append("\n");
        }
        return lines.toString();
    }

    public String getContext() {
        return this.context;
    }

    public Optional<Mark> getContextMark() {
        return this.contextMark;
    }

    public String getProblem() {
        return this.problem;
    }

    public Optional<Mark> getProblemMark() {
        return this.problemMark;
    }
}

