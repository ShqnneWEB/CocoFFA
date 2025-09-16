/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.schema;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.ConstructNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.ConstructYamlNull;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.json.ConstructOptionalClass;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.json.ConstructUuidClass;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.json.ConstructYamlBinary;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.json.ConstructYamlJsonBool;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.json.ConstructYamlJsonFloat;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.json.ConstructYamlJsonInt;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.resolver.JsonScalarResolver;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.resolver.ScalarResolver;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.schema.Schema;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JsonSchema
implements Schema {
    private final Map<Tag, ConstructNode> tagConstructors = new HashMap<Tag, ConstructNode>();
    private final ScalarResolver scalarResolver = new JsonScalarResolver();

    public JsonSchema() {
        this.tagConstructors.put(Tag.NULL, new ConstructYamlNull());
        this.tagConstructors.put(Tag.BOOL, new ConstructYamlJsonBool());
        this.tagConstructors.put(Tag.INT, new ConstructYamlJsonInt());
        this.tagConstructors.put(Tag.FLOAT, new ConstructYamlJsonFloat());
        this.tagConstructors.put(Tag.BINARY, new ConstructYamlBinary());
        this.tagConstructors.put(new Tag(UUID.class), new ConstructUuidClass());
        this.tagConstructors.put(new Tag(Optional.class), new ConstructOptionalClass(this.getScalarResolver()));
    }

    @Override
    public ScalarResolver getScalarResolver() {
        return this.scalarResolver;
    }

    @Override
    public Map<Tag, ConstructNode> getSchemaTagConstructors() {
        return this.tagConstructors;
    }
}

