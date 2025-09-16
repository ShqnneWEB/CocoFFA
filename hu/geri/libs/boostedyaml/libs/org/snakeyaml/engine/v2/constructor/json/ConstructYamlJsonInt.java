/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.json;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.ConstructScalar;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import java.math.BigInteger;

public class ConstructYamlJsonInt
extends ConstructScalar {
    @Override
    public Object construct(Node node) {
        String value = this.constructScalar(node);
        return this.createIntNumber(value);
    }

    protected Number createIntNumber(String number) {
        Number result;
        try {
            result = Integer.valueOf(number);
        } catch (NumberFormatException e) {
            try {
                result = Long.valueOf(number);
            } catch (NumberFormatException e1) {
                result = new BigInteger(number);
            }
        }
        return result;
    }
}

