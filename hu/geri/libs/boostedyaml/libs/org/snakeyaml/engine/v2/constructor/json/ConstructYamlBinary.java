/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.json;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.ConstructScalar;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import java.util.Base64;

public class ConstructYamlBinary
extends ConstructScalar {
    @Override
    public Object construct(Node node) {
        String noWhiteSpaces = this.constructScalar(node).replaceAll("\\s", "");
        return Base64.getDecoder().decode(noWhiteSpaces);
    }
}

