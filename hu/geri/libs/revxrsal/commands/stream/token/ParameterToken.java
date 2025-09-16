/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.stream.token;

import hu.geri.libs.revxrsal.commands.stream.token.Token;
import java.util.Objects;

public final class ParameterToken
implements Token {
    private final String name;

    public ParameterToken(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        ParameterToken that = (ParameterToken)obj;
        return Objects.equals(this.name, that.name);
    }

    public int hashCode() {
        return Objects.hash(this.name);
    }

    public String toString() {
        return "ParameterToken[name=" + this.name + ']';
    }
}

