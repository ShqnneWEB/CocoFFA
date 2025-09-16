/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.settings.dumper;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.DumpSettings;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.DumpSettingsBuilder;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.NonPrintableStyle;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.SpecVersion;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.schema.Schema;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.serializer.AnchorGenerator;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.serializer.NumberAnchorGenerator;
import hu.geri.libs.boostedyaml.settings.Settings;
import hu.geri.libs.boostedyaml.utils.format.Formatter;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DumperSettings
implements Settings {
    public static final DumperSettings DEFAULT = DumperSettings.builder().build();
    private final DumpSettingsBuilder builder;
    private final Supplier<AnchorGenerator> generatorSupplier;
    private final ScalarStyle stringStyle;
    private final Formatter<ScalarStyle, String> scalarFormatter;
    private final Formatter<FlowStyle, Iterable<?>> sequenceFormatter;
    private final Formatter<FlowStyle, Map<?, ?>> mappingFormatter;

    private DumperSettings(Builder builder) {
        this.builder = builder.builder;
        this.generatorSupplier = builder.anchorGeneratorSupplier;
        this.scalarFormatter = builder.scalarFormatter;
        this.sequenceFormatter = builder.sequenceFormatter;
        this.mappingFormatter = builder.mappingFormatter;
        this.stringStyle = builder.stringStyle;
    }

    public DumpSettings buildEngineSettings() {
        return this.builder.setAnchorGenerator(this.generatorSupplier.get()).setDumpComments(true).build();
    }

    public ScalarStyle getStringStyle() {
        return this.stringStyle;
    }

    public Formatter<ScalarStyle, String> getScalarFormatter() {
        return this.scalarFormatter;
    }

    public Formatter<FlowStyle, Iterable<?>> getSequenceFormatter() {
        return this.sequenceFormatter;
    }

    public Formatter<FlowStyle, Map<?, ?>> getMappingFormatter() {
        return this.mappingFormatter;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(DumpSettingsBuilder builder) {
        return new Builder(builder);
    }

    public static Builder builder(DumperSettings settings) {
        return DumperSettings.builder(settings.builder).setAnchorGenerator(settings.generatorSupplier);
    }

    public static class Builder {
        public static final Supplier<AnchorGenerator> DEFAULT_ANCHOR_GENERATOR = () -> new NumberAnchorGenerator(0);
        public static final FlowStyle DEFAULT_FLOW_STYLE = FlowStyle.BLOCK;
        public static final ScalarStyle DEFAULT_SCALAR_STYLE = ScalarStyle.PLAIN;
        public static final Formatter<ScalarStyle, String> DEFAULT_SCALAR_FORMATTER = Formatter.identity();
        public static final Formatter<FlowStyle, Iterable<?>> DEFAULT_SEQUENCE_FORMATTER = Formatter.identity();
        public static final Formatter<FlowStyle, Map<?, ?>> DEFAULT_MAPPING_FORMATTER = Formatter.identity();
        public static final ScalarStyle DEFAULT_STRING_STYLE = ScalarStyle.PLAIN;
        public static final boolean DEFAULT_START_MARKER = false;
        public static final boolean DEFAULT_END_MARKER = false;
        public static final Tag DEFAULT_ROOT_TAG = null;
        public static final boolean DEFAULT_CANONICAL = false;
        public static final boolean DEFAULT_MULTILINE_FORMAT = false;
        public static final Encoding DEFAULT_ENCODING = Encoding.UNICODE;
        public static final int DEFAULT_INDENTATION = 2;
        public static final int DEFAULT_INDICATOR_INDENTATION = 0;
        public static final int DEFAULT_MAX_LINE_WIDTH = 0;
        public static final int DEFAULT_MAX_SIMPLE_KEY_LENGTH = 0;
        public static final boolean DEFAULT_ESCAPE_UNPRINTABLE = true;
        private final DumpSettingsBuilder builder;
        private Supplier<AnchorGenerator> anchorGeneratorSupplier = DEFAULT_ANCHOR_GENERATOR;
        private Formatter<ScalarStyle, String> scalarFormatter = DEFAULT_SCALAR_FORMATTER;
        private Formatter<FlowStyle, Iterable<?>> sequenceFormatter = DEFAULT_SEQUENCE_FORMATTER;
        private Formatter<FlowStyle, Map<?, ?>> mappingFormatter = DEFAULT_MAPPING_FORMATTER;
        private ScalarStyle stringStyle = DEFAULT_STRING_STYLE;

        private Builder(DumpSettingsBuilder builder) {
            this.builder = builder;
        }

        private Builder() {
            this.builder = DumpSettings.builder();
            this.setFlowStyle(DEFAULT_FLOW_STYLE);
            this.setScalarStyle(DEFAULT_SCALAR_STYLE);
            this.setStringStyle(DEFAULT_STRING_STYLE);
            this.setStartMarker(false);
            this.setEndMarker(false);
            this.setRootTag(DEFAULT_ROOT_TAG);
            this.setCanonicalForm(false);
            this.setMultilineStyle(false);
            this.setEncoding(DEFAULT_ENCODING);
            this.setIndentation(2);
            this.setIndicatorIndentation(0);
            this.setLineWidth(0);
            this.setMaxSimpleKeyLength(0);
            this.setEscapeUnprintable(true);
        }

        public Builder setAnchorGenerator(@NotNull Supplier<AnchorGenerator> generator) {
            this.anchorGeneratorSupplier = generator;
            return this;
        }

        public Builder setFlowStyle(@NotNull FlowStyle flowStyle) {
            this.builder.setDefaultFlowStyle(flowStyle);
            return this;
        }

        public Builder setScalarStyle(@NotNull ScalarStyle scalarStyle) {
            this.builder.setDefaultScalarStyle(scalarStyle);
            return this;
        }

        public Builder setScalarFormatter(@NotNull Formatter<ScalarStyle, String> formatter) {
            this.scalarFormatter = formatter;
            return this;
        }

        public Builder setSequenceFormatter(@NotNull Formatter<FlowStyle, Iterable<?>> formatter) {
            this.sequenceFormatter = formatter;
            return this;
        }

        public Builder setMappingFormatter(@NotNull Formatter<FlowStyle, Map<?, ?>> formatter) {
            this.mappingFormatter = formatter;
            return this;
        }

        @Deprecated
        public Builder setStringStyle(@NotNull ScalarStyle stringStyle) {
            this.stringStyle = stringStyle;
            return this;
        }

        public Builder setStartMarker(boolean startMarker) {
            this.builder.setExplicitStart(startMarker);
            return this;
        }

        public Builder setEndMarker(boolean endMarker) {
            this.builder.setExplicitEnd(endMarker);
            return this;
        }

        public Builder setSchema(@NotNull Schema schema) {
            this.builder.setSchema(schema);
            return this;
        }

        public Builder setRootTag(@Nullable Tag rootTag) {
            this.builder.setExplicitRootTag(Optional.ofNullable(rootTag));
            return this;
        }

        public Builder setYamlDirective(@Nullable SpecVersion directive) {
            this.builder.setYamlDirective(Optional.ofNullable(directive));
            return this;
        }

        public Builder setTagDirectives(@NotNull Map<String, String> directives) {
            this.builder.setTagDirective(directives);
            return this;
        }

        public Builder setCanonicalForm(boolean canonical) {
            this.builder.setCanonical(canonical);
            return this;
        }

        public Builder setMultilineStyle(boolean multilineStyle) {
            this.builder.setMultiLineFlow(multilineStyle);
            return this;
        }

        public Builder setEncoding(@NotNull Encoding encoding) {
            this.builder.setUseUnicodeEncoding(encoding.isUnicode());
            return this;
        }

        public Builder setIndentation(int spaces) {
            this.builder.setIndent(spaces);
            return this;
        }

        public Builder setIndicatorIndentation(int spaces) {
            this.builder.setIndentWithIndicator(spaces > 0);
            this.builder.setIndicatorIndent(Math.max(spaces, 0));
            return this;
        }

        public Builder setLineWidth(int width) {
            this.builder.setWidth(width <= 0 ? Integer.MAX_VALUE : width);
            return this;
        }

        public Builder setLineBreak(@NotNull String lineBreak) {
            this.builder.setBestLineBreak(lineBreak);
            return this;
        }

        public Builder setMaxSimpleKeyLength(int length) {
            if (length > 1018) {
                throw new IllegalArgumentException("Maximum simple key length is limited to 1018!");
            }
            this.builder.setMaxSimpleKeyLength(length <= 0 ? 1024 : length + 6);
            return this;
        }

        public Builder setEscapeUnprintable(boolean escape) {
            return this.setUnprintableStyle(escape ? NonPrintableStyle.ESCAPE : NonPrintableStyle.BINARY);
        }

        public Builder setUnprintableStyle(@NotNull NonPrintableStyle style) {
            this.builder.setNonPrintableStyle(style);
            return this;
        }

        public DumperSettings build() {
            return new DumperSettings(this);
        }
    }

    public static enum Encoding {
        UNICODE,
        ASCII;


        boolean isUnicode() {
            return this == UNICODE;
        }
    }
}

