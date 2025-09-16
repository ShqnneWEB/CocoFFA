/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.resolver;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.resolver.BaseScalarResolver;
import java.util.regex.Pattern;

public class CoreScalarResolver
extends BaseScalarResolver {
    public static final Pattern BOOL = Pattern.compile("^(?:true|True|TRUE|false|False|FALSE)$");
    public static final Pattern FLOAT = Pattern.compile("^([-+]?(\\.[0-9]+|[0-9]+(\\.[0-9]*)?)([eE][-+]?[0-9]+)?)|([-+]?\\.(?:inf|Inf|INF))|(\\.(?:nan|NaN|NAN))$");
    public static final Pattern INT = Pattern.compile("^([-+]?[0-9]+)|(0o[0-7]+)|(0x[0-9a-fA-F]+)$");
    public static final Pattern NULL = Pattern.compile("^(?:~|null|Null|NULL| )$");

    @Override
    protected void addImplicitResolvers() {
        this.addImplicitResolver(Tag.NULL, EMPTY, null);
        this.addImplicitResolver(Tag.BOOL, BOOL, "tfTF");
        this.addImplicitResolver(Tag.INT, INT, "-+0123456789");
        this.addImplicitResolver(Tag.FLOAT, FLOAT, "-+0123456789.");
        this.addImplicitResolver(Tag.NULL, NULL, "n\u0000");
        this.addImplicitResolver(Tag.ENV_TAG, ENV_FORMAT, "$");
    }
}

