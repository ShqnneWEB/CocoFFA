/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.core;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.json.ConstructYamlJsonFloat;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.ScalarNode;

public class ConstructYamlCoreFloat
extends ConstructYamlJsonFloat {
    @Override
    protected String constructScalar(Node node) {
        return ((ScalarNode)node).getValue().toLowerCase();
    }
}

