/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.schema;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.ConstructNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.resolver.FailsafeScalarResolver;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.resolver.ScalarResolver;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.schema.Schema;
import java.util.HashMap;
import java.util.Map;

public class FailsafeSchema
implements Schema {
    @Override
    public ScalarResolver getScalarResolver() {
        return new FailsafeScalarResolver();
    }

    @Override
    public Map<Tag, ConstructNode> getSchemaTagConstructors() {
        return new HashMap<Tag, ConstructNode>();
    }
}

