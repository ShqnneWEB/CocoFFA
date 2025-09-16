/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.json;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.ConstructScalar;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.ConstructorException;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.NodeType;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.resolver.ScalarResolver;
import java.util.Optional;

public class ConstructOptionalClass
extends ConstructScalar {
    private final ScalarResolver scalarResolver;

    public ConstructOptionalClass(ScalarResolver scalarResolver) {
        this.scalarResolver = scalarResolver;
    }

    @Override
    public Object construct(Node node) {
        if (node.getNodeType() != NodeType.SCALAR) {
            throw new ConstructorException("while constructing Optional", Optional.empty(), "found non scalar node", node.getStartMark());
        }
        String value = this.constructScalar(node);
        Tag implicitTag = this.scalarResolver.resolve(value, true);
        if (implicitTag.equals(Tag.NULL)) {
            return Optional.empty();
        }
        return Optional.of(value);
    }
}

