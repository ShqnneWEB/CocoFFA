/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.json;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.ConstructScalar;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;

public class ConstructYamlJsonBool
extends ConstructScalar {
    @Override
    public Object construct(Node node) {
        String val = this.constructScalar(node);
        return BOOL_VALUES.get(val);
    }
}

