/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.DumpSettingsBuilder;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.SettingKey;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.NonPrintableStyle;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.SpecVersion;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.schema.Schema;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.serializer.AnchorGenerator;
import java.util.Map;
import java.util.Optional;

public final class DumpSettings {
    private final boolean explicitStart;
    private final boolean explicitEnd;
    private final NonPrintableStyle nonPrintableStyle;
    private final Optional<Tag> explicitRootTag;
    private final AnchorGenerator anchorGenerator;
    private final Optional<SpecVersion> yamlDirective;
    private final Map<String, String> tagDirective;
    private final FlowStyle defaultFlowStyle;
    private final ScalarStyle defaultScalarStyle;
    private final Boolean dereferenceAliases;
    private final boolean canonical;
    private final boolean multiLineFlow;
    private final boolean useUnicodeEncoding;
    private final int indent;
    private final int indicatorIndent;
    private final int width;
    private final String bestLineBreak;
    private final boolean splitLines;
    private final int maxSimpleKeyLength;
    private final boolean indentWithIndicator;
    private final boolean dumpComments;
    private final Schema schema;
    private final Map<SettingKey, Object> customProperties;

    DumpSettings(boolean explicitStart, boolean explicitEnd, Optional<Tag> explicitRootTag, AnchorGenerator anchorGenerator, Optional<SpecVersion> yamlDirective, Map<String, String> tagDirective, FlowStyle defaultFlowStyle, ScalarStyle defaultScalarStyle, NonPrintableStyle nonPrintableStyle, Schema schema, Boolean dereferenceAliases, boolean canonical, boolean multiLineFlow, boolean useUnicodeEncoding, int indent, int indicatorIndent, int width, String bestLineBreak, boolean splitLines, int maxSimpleKeyLength, Map<SettingKey, Object> customProperties, boolean indentWithIndicator, boolean dumpComments) {
        this.explicitStart = explicitStart;
        this.explicitEnd = explicitEnd;
        this.nonPrintableStyle = nonPrintableStyle;
        this.explicitRootTag = explicitRootTag;
        this.anchorGenerator = anchorGenerator;
        this.yamlDirective = yamlDirective;
        this.tagDirective = tagDirective;
        this.defaultFlowStyle = defaultFlowStyle;
        this.defaultScalarStyle = defaultScalarStyle;
        this.schema = schema;
        this.canonical = canonical;
        this.multiLineFlow = multiLineFlow;
        this.useUnicodeEncoding = useUnicodeEncoding;
        this.indent = indent;
        this.indicatorIndent = indicatorIndent;
        this.width = width;
        this.bestLineBreak = bestLineBreak;
        this.splitLines = splitLines;
        this.maxSimpleKeyLength = maxSimpleKeyLength;
        this.customProperties = customProperties;
        this.indentWithIndicator = indentWithIndicator;
        this.dumpComments = dumpComments;
        this.dereferenceAliases = dereferenceAliases;
    }

    public static DumpSettingsBuilder builder() {
        return new DumpSettingsBuilder();
    }

    public FlowStyle getDefaultFlowStyle() {
        return this.defaultFlowStyle;
    }

    public ScalarStyle getDefaultScalarStyle() {
        return this.defaultScalarStyle;
    }

    public boolean isExplicitStart() {
        return this.explicitStart;
    }

    public AnchorGenerator getAnchorGenerator() {
        return this.anchorGenerator;
    }

    public boolean isExplicitEnd() {
        return this.explicitEnd;
    }

    public Optional<Tag> getExplicitRootTag() {
        return this.explicitRootTag;
    }

    public Optional<SpecVersion> getYamlDirective() {
        return this.yamlDirective;
    }

    public Map<String, String> getTagDirective() {
        return this.tagDirective;
    }

    public boolean isCanonical() {
        return this.canonical;
    }

    public boolean isMultiLineFlow() {
        return this.multiLineFlow;
    }

    public boolean isUseUnicodeEncoding() {
        return this.useUnicodeEncoding;
    }

    public int getIndent() {
        return this.indent;
    }

    public int getIndicatorIndent() {
        return this.indicatorIndent;
    }

    public int getWidth() {
        return this.width;
    }

    public String getBestLineBreak() {
        return this.bestLineBreak;
    }

    public boolean isSplitLines() {
        return this.splitLines;
    }

    public int getMaxSimpleKeyLength() {
        return this.maxSimpleKeyLength;
    }

    public NonPrintableStyle getNonPrintableStyle() {
        return this.nonPrintableStyle;
    }

    public Object getCustomProperty(SettingKey key) {
        return this.customProperties.get(key);
    }

    public boolean getIndentWithIndicator() {
        return this.indentWithIndicator;
    }

    public boolean getDumpComments() {
        return this.dumpComments;
    }

    public Schema getSchema() {
        return this.schema;
    }

    public Boolean isDereferenceAliases() {
        return this.dereferenceAliases;
    }
}

