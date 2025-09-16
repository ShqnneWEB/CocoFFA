/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.resolver;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.resolver.ResolverTuple;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.resolver.ScalarResolver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class BaseScalarResolver
implements ScalarResolver {
    public static final Pattern EMPTY = Pattern.compile("^$");
    public static final Pattern ENV_FORMAT = Pattern.compile("^\\$\\{\\s*(?:(\\w+)(?:(:?[-?])(\\w+)?)?)\\s*\\}$");
    protected Map<Character, List<ResolverTuple>> yamlImplicitResolvers = new HashMap<Character, List<ResolverTuple>>();

    public BaseScalarResolver() {
        this.addImplicitResolvers();
    }

    public void addImplicitResolver(Tag tag, Pattern regexp, String first) {
        if (first == null) {
            List curr = this.yamlImplicitResolvers.computeIfAbsent(null, c -> new ArrayList());
            curr.add(new ResolverTuple(tag, regexp));
        } else {
            char[] chrs = first.toCharArray();
            int j = chrs.length;
            for (int i = 0; i < j; ++i) {
                List<ResolverTuple> curr;
                Character theC = Character.valueOf(chrs[i]);
                if (theC.charValue() == '\u0000') {
                    theC = null;
                }
                if ((curr = this.yamlImplicitResolvers.get(theC)) == null) {
                    curr = new ArrayList<ResolverTuple>();
                    this.yamlImplicitResolvers.put(theC, curr);
                }
                curr.add(new ResolverTuple(tag, regexp));
            }
        }
    }

    abstract void addImplicitResolvers();

    @Override
    public Tag resolve(String value, Boolean implicit) {
        Pattern regexp;
        Tag tag;
        if (!implicit.booleanValue()) {
            return Tag.STR;
        }
        List<ResolverTuple> resolvers = value.isEmpty() ? this.yamlImplicitResolvers.get(Character.valueOf('\u0000')) : this.yamlImplicitResolvers.get(Character.valueOf(value.charAt(0)));
        if (resolvers != null) {
            for (ResolverTuple v : resolvers) {
                tag = v.getTag();
                regexp = v.getRegexp();
                if (!regexp.matcher(value).matches()) continue;
                return tag;
            }
        }
        if (this.yamlImplicitResolvers.containsKey(null)) {
            for (ResolverTuple v : this.yamlImplicitResolvers.get(null)) {
                tag = v.getTag();
                regexp = v.getRegexp();
                if (!regexp.matcher(value).matches()) continue;
                return tag;
            }
        }
        return Tag.STR;
    }
}

