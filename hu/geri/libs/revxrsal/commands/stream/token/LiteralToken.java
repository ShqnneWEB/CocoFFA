/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.stream.token;

import hu.geri.libs.revxrsal.commands.stream.token.Token;
import java.util.Objects;

public final class LiteralToken
implements Token {
    private final String value;

    public LiteralToken(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        LiteralToken that = (LiteralToken)obj;
        return Objects.equals(this.value, that.value);
    }

    public int hashCode() {
        return Objects.hash(this.value);
    }

    public String toString() {
        return "LiteralToken[value=" + this.value + ']';
    }
}

