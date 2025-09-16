/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.resolver;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.resolver.BaseScalarResolver;

public class FailsafeScalarResolver
extends BaseScalarResolver {
    @Override
    protected void addImplicitResolvers() {
        this.addImplicitResolver(Tag.NULL, EMPTY, null);
    }
}

