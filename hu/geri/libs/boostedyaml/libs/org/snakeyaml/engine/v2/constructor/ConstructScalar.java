/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.ConstructNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.ScalarNode;
import java.util.HashMap;
import java.util.Map;

public abstract class ConstructScalar
implements ConstructNode {
    protected static final Map<String, Boolean> BOOL_VALUES = new HashMap<String, Boolean>();

    protected String constructScalar(Node node) {
        return ((ScalarNode)node).getValue();
    }

    static {
        BOOL_VALUES.put("true", Boolean.TRUE);
        BOOL_VALUES.put("false", Boolean.FALSE);
    }
}

