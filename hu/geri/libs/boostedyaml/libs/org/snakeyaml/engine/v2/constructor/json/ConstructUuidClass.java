/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.json;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.ConstructScalar;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import java.util.UUID;

public class ConstructUuidClass
extends ConstructScalar {
    @Override
    public Object construct(Node node) {
        String uuidValue = this.constructScalar(node);
        return UUID.fromString(uuidValue);
    }
}

