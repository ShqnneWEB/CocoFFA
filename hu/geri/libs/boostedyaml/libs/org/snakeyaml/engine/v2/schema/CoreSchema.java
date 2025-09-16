/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.schema;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.ConstructNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.core.ConstructYamlCoreBool;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.core.ConstructYamlCoreFloat;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.core.ConstructYamlCoreInt;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.resolver.CoreScalarResolver;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.resolver.ScalarResolver;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.schema.JsonSchema;
import java.util.HashMap;
import java.util.Map;

public class CoreSchema
extends JsonSchema {
    private final Map<Tag, ConstructNode> tagConstructors = new HashMap<Tag, ConstructNode>();

    public CoreSchema() {
        this.tagConstructors.put(Tag.BOOL, new ConstructYamlCoreBool());
        this.tagConstructors.put(Tag.INT, new ConstructYamlCoreInt());
        this.tagConstructors.put(Tag.FLOAT, new ConstructYamlCoreFloat());
    }

    @Override
    public ScalarResolver getScalarResolver() {
        return new CoreScalarResolver();
    }

    @Override
    public Map<Tag, ConstructNode> getSchemaTagConstructors() {
        Map<Tag, ConstructNode> json = super.getSchemaTagConstructors();
        json.putAll(this.tagConstructors);
        return json;
    }
}

