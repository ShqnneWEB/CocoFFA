/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.scanner;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.tokens.Token;
import java.util.Iterator;

public interface Scanner
extends Iterator<Token> {
    public boolean checkToken(Token.ID ... var1);

    public Token peekToken();

    @Override
    public Token next();

    public void resetDocumentIndex();
}

