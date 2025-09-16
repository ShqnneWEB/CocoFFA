/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.serializer;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.Anchor;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.serializer.AnchorGenerator;
import java.text.NumberFormat;

public class NumberAnchorGenerator
implements AnchorGenerator {
    private int lastAnchorId = 0;

    public NumberAnchorGenerator(int lastAnchorId) {
        this.lastAnchorId = lastAnchorId;
    }

    @Override
    public Anchor nextAnchor(Node node) {
        ++this.lastAnchorId;
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumIntegerDigits(3);
        format.setMaximumFractionDigits(0);
        format.setGroupingUsed(false);
        String anchorId = format.format(this.lastAnchorId);
        return new Anchor("id" + anchorId);
    }
}

