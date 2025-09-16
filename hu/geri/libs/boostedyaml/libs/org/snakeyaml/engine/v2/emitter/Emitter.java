/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.emitter;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.DumpSettings;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.StreamDataWriter;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.comments.CommentEventsCollector;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.comments.CommentLine;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.comments.CommentType;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.Anchor;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.ArrayStack;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.CharConstants;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.SpecVersion;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.emitter.Emitable;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.emitter.EmitterState;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.emitter.ScalarAnalysis;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.AliasEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.CollectionEndEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.CollectionStartEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.CommentEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.DocumentEndEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.DocumentStartEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.Event;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.MappingStartEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.NodeEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.ScalarEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.SequenceStartEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.StreamEndEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.StreamStartEvent;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.EmitterException;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.scanner.StreamReader;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.TreeSet;
import java.util.regex.Pattern;

public final class Emitter
implements Emitable {
    private static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<Character, String>();
    public static final int MIN_INDENT = 1;
    public static final int MAX_INDENT = 10;
    private static final String SPACE = " ";
    private static final Map<String, String> DEFAULT_TAG_PREFIXES;
    private final StreamDataWriter stream;
    private final ArrayStack<EmitterState> states;
    private EmitterState state;
    private final Queue<Event> events;
    private Event event;
    private final ArrayStack<Integer> indents;
    private Integer indent;
    private int flowLevel;
    private boolean rootContext;
    private boolean mappingContext;
    private boolean simpleKeyContext;
    private int column;
    private boolean whitespace;
    private boolean indention;
    private boolean openEnded;
    private final Boolean canonical;
    private final Boolean multiLineFlow;
    private final boolean allowUnicode;
    private int bestIndent;
    private final int indicatorIndent;
    private final boolean indentWithIndicator;
    private int bestWidth;
    private final String bestLineBreak;
    private final boolean splitLines;
    private final int maxSimpleKeyLength;
    private final boolean emitComments;
    private Map<String, String> tagPrefixes;
    private Optional<Anchor> preparedAnchor;
    private String preparedTag;
    private ScalarAnalysis analysis;
    private ScalarStyle scalarStyle;
    private final CommentEventsCollector blockCommentsCollector;
    private final CommentEventsCollector inlineCommentsCollector;
    private static final Pattern HANDLE_FORMAT;

    public Emitter(DumpSettings opts, StreamDataWriter stream) {
        this.stream = stream;
        this.states = new ArrayStack(100);
        this.state = new ExpectStreamStart();
        this.events = new ArrayDeque<Event>(100);
        this.event = null;
        this.indents = new ArrayStack(10);
        this.indent = null;
        this.flowLevel = 0;
        this.mappingContext = false;
        this.simpleKeyContext = false;
        this.column = 0;
        this.whitespace = true;
        this.indention = true;
        this.openEnded = false;
        this.canonical = opts.isCanonical();
        this.multiLineFlow = opts.isMultiLineFlow();
        this.allowUnicode = opts.isUseUnicodeEncoding();
        this.bestIndent = 2;
        if (opts.getIndent() > 1 && opts.getIndent() < 10) {
            this.bestIndent = opts.getIndent();
        }
        this.indicatorIndent = opts.getIndicatorIndent();
        this.indentWithIndicator = opts.getIndentWithIndicator();
        this.bestWidth = 80;
        if (opts.getWidth() > this.bestIndent * 2) {
            this.bestWidth = opts.getWidth();
        }
        this.bestLineBreak = opts.getBestLineBreak();
        this.splitLines = opts.isSplitLines();
        this.maxSimpleKeyLength = opts.getMaxSimpleKeyLength();
        this.emitComments = opts.getDumpComments();
        this.tagPrefixes = new LinkedHashMap<String, String>();
        this.preparedAnchor = Optional.empty();
        this.preparedTag = null;
        this.analysis = null;
        this.scalarStyle = null;
        this.blockCommentsCollector = new CommentEventsCollector(this.events, CommentType.BLANK_LINE, CommentType.BLOCK);
        this.inlineCommentsCollector = new CommentEventsCollector(this.events, CommentType.IN_LINE);
    }

    @Override
    public void emit(Event event) {
        this.events.add(event);
        while (!this.needMoreEvents()) {
            this.event = this.events.poll();
            this.state.expect();
            this.event = null;
        }
    }

    private boolean needMoreEvents() {
        if (this.events.isEmpty()) {
            return true;
        }
        Iterator<Event> iter = this.events.iterator();
        Event event = (Event)iter.next();
        while (event instanceof CommentEvent) {
            if (!iter.hasNext()) {
                return true;
            }
            event = (Event)iter.next();
        }
        if (event instanceof DocumentStartEvent) {
            return this.needEvents(iter, 1);
        }
        if (event instanceof SequenceStartEvent) {
            return this.needEvents(iter, 2);
        }
        if (event instanceof MappingStartEvent) {
            return this.needEvents(iter, 3);
        }
        if (event instanceof StreamStartEvent) {
            return this.needEvents(iter, 2);
        }
        if (event instanceof StreamEndEvent) {
            return false;
        }
        if (this.emitComments) {
            return this.needEvents(iter, 1);
        }
        return false;
    }

    private boolean needEvents(Iterator<Event> iter, int count) {
        int level = 0;
        int actualCount = 0;
        while (iter.hasNext()) {
            Event event = iter.next();
            if (event instanceof CommentEvent) continue;
            ++actualCount;
            if (event instanceof DocumentStartEvent || event instanceof CollectionStartEvent) {
                ++level;
            } else if (event instanceof DocumentEndEvent || event instanceof CollectionEndEvent) {
                --level;
            } else if (event instanceof StreamEndEvent) {
                level = -1;
            }
            if (level >= 0) continue;
            return false;
        }
        return actualCount < count;
    }

    private void increaseIndent(boolean isFlow, boolean indentless) {
        this.indents.push(this.indent);
        if (this.indent == null) {
            this.indent = isFlow ? Integer.valueOf(this.bestIndent) : Integer.valueOf(0);
        } else if (!indentless) {
            this.indent = this.indent + this.bestIndent;
        }
    }

    private void expectNode(boolean root, boolean mapping, boolean simpleKey) {
        this.rootContext = root;
        this.mappingContext = mapping;
        this.simpleKeyContext = simpleKey;
        if (this.event.getEventId() == Event.ID.Alias) {
            this.expectAlias();
        } else if (this.event.getEventId() == Event.ID.Scalar || this.event.getEventId() == Event.ID.SequenceStart || this.event.getEventId() == Event.ID.MappingStart) {
            this.processAnchor("&");
            this.processTag();
            this.handleNodeEvent(this.event.getEventId());
        } else {
            throw new EmitterException("expected NodeEvent, but got " + (Object)((Object)this.event.getEventId()));
        }
    }

    private void handleNodeEvent(Event.ID id) {
        switch (id) {
            case Scalar: {
                this.expectScalar();
                break;
            }
            case SequenceStart: {
                if (this.flowLevel != 0 || this.canonical.booleanValue() || ((SequenceStartEvent)this.event).isFlow() || this.checkEmptySequence()) {
                    this.expectFlowSequence();
                    break;
                }
                this.expectBlockSequence();
                break;
            }
            case MappingStart: {
                if (this.flowLevel != 0 || this.canonical.booleanValue() || ((MappingStartEvent)this.event).isFlow() || this.checkEmptyMapping()) {
                    this.expectFlowMapping();
                    break;
                }
                this.expectBlockMapping();
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }

    private void expectAlias() {
        if (!(this.event instanceof AliasEvent)) {
            throw new EmitterException("Expecting Alias.");
        }
        this.processAnchor("*");
        this.state = this.states.pop();
    }

    private void expectScalar() {
        this.increaseIndent(true, false);
        this.processScalar();
        this.indent = this.indents.pop();
        this.state = this.states.pop();
    }

    private void expectFlowSequence() {
        this.writeIndicator("[", true, true, false);
        ++this.flowLevel;
        this.increaseIndent(true, false);
        if (this.multiLineFlow.booleanValue()) {
            this.writeIndent();
        }
        this.state = new ExpectFirstFlowSequenceItem();
    }

    private void expectFlowMapping() {
        this.writeIndicator("{", true, true, false);
        ++this.flowLevel;
        this.increaseIndent(true, false);
        if (this.multiLineFlow.booleanValue()) {
            this.writeIndent();
        }
        this.state = new ExpectFirstFlowMappingKey();
    }

    private void expectBlockSequence() {
        boolean indentless = this.mappingContext && !this.indention;
        this.increaseIndent(false, indentless);
        this.state = new ExpectFirstBlockSequenceItem();
    }

    private void expectBlockMapping() {
        this.increaseIndent(false, false);
        this.state = new ExpectFirstBlockMappingKey();
    }

    private boolean isFoldedOrLiteral(Event event) {
        if (event.getEventId() != Event.ID.Scalar) {
            return false;
        }
        ScalarEvent scalarEvent = (ScalarEvent)event;
        ScalarStyle style = scalarEvent.getScalarStyle();
        return style == ScalarStyle.FOLDED || style == ScalarStyle.LITERAL;
    }

    private boolean checkEmptySequence() {
        return this.event.getEventId() == Event.ID.SequenceStart && !this.events.isEmpty() && this.events.peek().getEventId() == Event.ID.SequenceEnd;
    }

    private boolean checkEmptyMapping() {
        return this.event.getEventId() == Event.ID.MappingStart && !this.events.isEmpty() && this.events.peek().getEventId() == Event.ID.MappingEnd;
    }

    private boolean checkSimpleKey() {
        Optional<Anchor> anchorOpt;
        int length = 0;
        if (this.event instanceof NodeEvent && (anchorOpt = ((NodeEvent)this.event).getAnchor()).isPresent()) {
            if (!this.preparedAnchor.isPresent()) {
                this.preparedAnchor = anchorOpt;
            }
            length += anchorOpt.get().getValue().length();
        }
        Optional<Object> tag = Optional.empty();
        if (this.event.getEventId() == Event.ID.Scalar) {
            tag = ((ScalarEvent)this.event).getTag();
        } else if (this.event instanceof CollectionStartEvent) {
            tag = ((CollectionStartEvent)this.event).getTag();
        }
        if (tag.isPresent()) {
            if (this.preparedTag == null) {
                this.preparedTag = this.prepareTag((String)tag.get());
            }
            length += this.preparedTag.length();
        }
        if (this.event.getEventId() == Event.ID.Scalar) {
            if (this.analysis == null) {
                this.analysis = this.analyzeScalar(((ScalarEvent)this.event).getValue());
            }
            length += this.analysis.getScalar().length();
        }
        return length < this.maxSimpleKeyLength && (this.event.getEventId() == Event.ID.Alias || this.event.getEventId() == Event.ID.Scalar && !this.analysis.isEmpty() && !this.analysis.isMultiline() || this.checkEmptySequence() || this.checkEmptyMapping());
    }

    private void processAnchor(String indicator) {
        NodeEvent ev = (NodeEvent)this.event;
        Optional<Anchor> anchorOption = ev.getAnchor();
        if (anchorOption.isPresent()) {
            Anchor anchor = anchorOption.get();
            if (!this.preparedAnchor.isPresent()) {
                this.preparedAnchor = anchorOption;
            }
            this.writeIndicator(indicator + anchor, true, false, false);
        }
        this.preparedAnchor = Optional.empty();
    }

    private void processTag() {
        Optional<String> tag;
        if (this.event.getEventId() == Event.ID.Scalar) {
            ScalarEvent ev = (ScalarEvent)this.event;
            tag = ev.getTag();
            if (this.scalarStyle == null) {
                this.scalarStyle = this.chooseScalarStyle(ev);
            }
            if ((!this.canonical.booleanValue() || !tag.isPresent()) && (this.scalarStyle == ScalarStyle.PLAIN && ev.getImplicit().canOmitTagInPlainScalar() || this.scalarStyle != ScalarStyle.PLAIN && ev.getImplicit().canOmitTagInNonPlainScalar())) {
                this.preparedTag = null;
                return;
            }
            if (ev.getImplicit().canOmitTagInPlainScalar() && !tag.isPresent()) {
                tag = Optional.of("!");
                this.preparedTag = null;
            }
        } else {
            CollectionStartEvent ev = (CollectionStartEvent)this.event;
            tag = ev.getTag();
            if (!(this.canonical.booleanValue() && tag.isPresent() || !ev.isImplicit())) {
                this.preparedTag = null;
                return;
            }
        }
        if (!tag.isPresent()) {
            throw new EmitterException("tag is not specified");
        }
        if (this.preparedTag == null) {
            this.preparedTag = this.prepareTag(tag.get());
        }
        this.writeIndicator(this.preparedTag, true, false, false);
        this.preparedTag = null;
    }

    private ScalarStyle chooseScalarStyle(ScalarEvent ev) {
        if (this.analysis == null) {
            this.analysis = this.analyzeScalar(ev.getValue());
        }
        if (!ev.isPlain() && ev.isDQuoted() || this.canonical.booleanValue()) {
            return ScalarStyle.DOUBLE_QUOTED;
        }
        if (ev.isJson() && Optional.of(Tag.STR.getValue()).equals(ev.getTag())) {
            return ScalarStyle.DOUBLE_QUOTED;
        }
        if ((ev.isPlain() || ev.isJson()) && ev.getImplicit().canOmitTagInPlainScalar() && (!this.simpleKeyContext || !this.analysis.isEmpty() && !this.analysis.isMultiline()) && (this.flowLevel != 0 && this.analysis.isAllowFlowPlain() || this.flowLevel == 0 && this.analysis.isAllowBlockPlain())) {
            return ScalarStyle.PLAIN;
        }
        if ((ev.isLiteral() || ev.isFolded()) && this.flowLevel == 0 && !this.simpleKeyContext && this.analysis.isAllowBlock()) {
            return ev.getScalarStyle();
        }
        if (!(!ev.isPlain() && !ev.isSQuoted() || !this.analysis.isAllowSingleQuoted() || this.simpleKeyContext && this.analysis.isMultiline())) {
            return ScalarStyle.SINGLE_QUOTED;
        }
        return ScalarStyle.DOUBLE_QUOTED;
    }

    private void processScalar() {
        ScalarEvent ev = (ScalarEvent)this.event;
        if (this.analysis == null) {
            this.analysis = this.analyzeScalar(ev.getValue());
        }
        boolean split = !this.simpleKeyContext && this.splitLines;
        switch (this.scalarStyle) {
            case PLAIN: {
                this.writePlain(this.analysis.getScalar(), split);
                break;
            }
            case DOUBLE_QUOTED: {
                this.writeDoubleQuoted(this.analysis.getScalar(), split);
                break;
            }
            case SINGLE_QUOTED: {
                this.writeSingleQuoted(this.analysis.getScalar(), split);
                break;
            }
            case FOLDED: {
                this.writeFolded(this.analysis.getScalar(), split);
                break;
            }
            case LITERAL: {
                this.writeLiteral(this.analysis.getScalar());
                break;
            }
            default: {
                throw new YamlEngineException("Unexpected scalarStyle: " + (Object)((Object)this.scalarStyle));
            }
        }
        this.analysis = null;
        this.scalarStyle = null;
    }

    private String prepareVersion(SpecVersion version) {
        if (version.getMajor() != 1) {
            throw new EmitterException("unsupported YAML version: " + version);
        }
        return version.getRepresentation();
    }

    private String prepareTagHandle(String handle) {
        if (handle.isEmpty()) {
            throw new EmitterException("tag handle must not be empty");
        }
        if (handle.charAt(0) != '!' || handle.charAt(handle.length() - 1) != '!') {
            throw new EmitterException("tag handle must start and end with '!': " + handle);
        }
        if (!"!".equals(handle) && !HANDLE_FORMAT.matcher(handle).matches()) {
            throw new EmitterException("invalid character in the tag handle: " + handle);
        }
        return handle;
    }

    private String prepareTagPrefix(String prefix) {
        if (prefix.isEmpty()) {
            throw new EmitterException("tag prefix must not be empty");
        }
        StringBuilder chunks = new StringBuilder();
        int end = 0;
        if (prefix.charAt(0) == '!') {
            end = 1;
        }
        while (end < prefix.length()) {
            ++end;
        }
        chunks.append(prefix, 0, end);
        return chunks.toString();
    }

    private String prepareTag(String tag) {
        if (tag.isEmpty()) {
            throw new EmitterException("tag must not be empty");
        }
        if ("!".equals(tag)) {
            return tag;
        }
        String handle = null;
        String suffix = tag;
        for (String prefix : this.tagPrefixes.keySet()) {
            if (!tag.startsWith(prefix) || !"!".equals(prefix) && prefix.length() >= tag.length()) continue;
            handle = prefix;
        }
        if (handle != null) {
            suffix = tag.substring(handle.length());
            handle = this.tagPrefixes.get(handle);
        }
        if (handle != null) {
            return handle + suffix;
        }
        return "!<" + suffix + ">";
    }

    private ScalarAnalysis analyzeScalar(String scalar) {
        if (scalar.isEmpty()) {
            return new ScalarAnalysis(scalar, true, false, false, true, true, false);
        }
        boolean blockIndicators = false;
        boolean flowIndicators = false;
        boolean lineBreaks = false;
        boolean specialCharacters = false;
        boolean leadingSpace = false;
        boolean leadingBreak = false;
        boolean trailingSpace = false;
        boolean trailingBreak = false;
        boolean breakSpace = false;
        boolean spaceBreak = false;
        if (scalar.startsWith("---") || scalar.startsWith("...")) {
            blockIndicators = true;
            flowIndicators = true;
        }
        boolean preceededByWhitespace = true;
        boolean followedByWhitespace = scalar.length() == 1 || CharConstants.NULL_BL_T_LINEBR.has(scalar.codePointAt(1));
        boolean previousSpace = false;
        boolean previousBreak = false;
        int index = 0;
        while (index < scalar.length()) {
            int nextIndex;
            boolean isLineBreak;
            int c = scalar.codePointAt(index);
            if (index == 0) {
                if ("#,[]{}&*!|>'\"%@`".indexOf(c) != -1) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
                if (c == 63 || c == 58) {
                    flowIndicators = true;
                    if (followedByWhitespace) {
                        blockIndicators = true;
                    }
                }
                if (c == 45 && followedByWhitespace) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
            } else {
                if (",?[]{}".indexOf(c) != -1) {
                    flowIndicators = true;
                }
                if (c == 58) {
                    flowIndicators = true;
                    if (followedByWhitespace) {
                        blockIndicators = true;
                    }
                }
                if (c == 35 && preceededByWhitespace) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
            }
            if (isLineBreak = CharConstants.LINEBR.has(c)) {
                lineBreaks = true;
            }
            if (c != 10 && (32 > c || c > 126)) {
                if (c == 133 || c >= 160 && c <= 55295 || c >= 57344 && c <= 65533 || c >= 65536 && c <= 0x10FFFF) {
                    if (!this.allowUnicode) {
                        specialCharacters = true;
                    }
                } else {
                    specialCharacters = true;
                }
            }
            if (c == 32) {
                if (index == 0) {
                    leadingSpace = true;
                }
                if (index == scalar.length() - 1) {
                    trailingSpace = true;
                }
                if (previousBreak) {
                    breakSpace = true;
                }
                previousSpace = true;
                previousBreak = false;
            } else if (isLineBreak) {
                if (index == 0) {
                    leadingBreak = true;
                }
                if (index == scalar.length() - 1) {
                    trailingBreak = true;
                }
                if (previousSpace) {
                    spaceBreak = true;
                }
                previousSpace = false;
                previousBreak = true;
            } else {
                previousSpace = false;
                previousBreak = false;
            }
            preceededByWhitespace = CharConstants.NULL_BL_T.has(c) || isLineBreak;
            followedByWhitespace = true;
            if ((index += Character.charCount(c)) + 1 >= scalar.length() || (nextIndex = index + Character.charCount(scalar.codePointAt(index))) >= scalar.length()) continue;
            followedByWhitespace = CharConstants.NULL_BL_T.has(scalar.codePointAt(nextIndex)) || isLineBreak;
        }
        boolean allowFlowPlain = true;
        boolean allowBlockPlain = true;
        boolean allowSingleQuoted = true;
        boolean allowBlock = true;
        if (leadingSpace || leadingBreak || trailingSpace || trailingBreak) {
            allowBlockPlain = false;
            allowFlowPlain = false;
        }
        if (trailingSpace) {
            allowBlock = false;
        }
        if (breakSpace) {
            allowSingleQuoted = false;
            allowBlockPlain = false;
            allowFlowPlain = false;
        }
        if (spaceBreak || specialCharacters) {
            allowBlock = false;
            allowSingleQuoted = false;
            allowBlockPlain = false;
            allowFlowPlain = false;
        }
        if (lineBreaks) {
            allowFlowPlain = false;
        }
        if (flowIndicators) {
            allowFlowPlain = false;
        }
        if (blockIndicators) {
            allowBlockPlain = false;
        }
        return new ScalarAnalysis(scalar, false, lineBreaks, allowFlowPlain, allowBlockPlain, allowSingleQuoted, allowBlock);
    }

    void flushStream() {
        this.stream.flush();
    }

    void writeStreamStart() {
    }

    void writeStreamEnd() {
        this.flushStream();
    }

    void writeIndicator(String indicator, boolean needWhitespace, boolean whitespace, boolean indentation) {
        if (!this.whitespace && needWhitespace) {
            ++this.column;
            this.stream.write(SPACE);
        }
        this.whitespace = whitespace;
        this.indention = this.indention && indentation;
        this.column += indicator.length();
        this.openEnded = false;
        this.stream.write(indicator);
    }

    int writeIndent() {
        int indentToWrite = this.indent != null ? this.indent : 0;
        if (!this.indention || this.column > indentToWrite || this.column == indentToWrite && !this.whitespace) {
            this.writeLineBreak(null);
        }
        int whitespaces = indentToWrite - this.column;
        this.writeWhitespace(whitespaces);
        return whitespaces;
    }

    private void writeWhitespace(int length) {
        if (length <= 0) {
            return;
        }
        this.whitespace = true;
        for (int i = 0; i < length; ++i) {
            this.stream.write(SPACE);
        }
        this.column += length;
    }

    private void writeLineBreak(String data) {
        this.whitespace = true;
        this.indention = true;
        this.column = 0;
        if (data == null) {
            this.stream.write(this.bestLineBreak);
        } else {
            this.stream.write(data);
        }
    }

    void writeVersionDirective(String versionText) {
        this.stream.write("%YAML ");
        this.stream.write(versionText);
        this.writeLineBreak(null);
    }

    void writeTagDirective(String handleText, String prefixText) {
        this.stream.write("%TAG ");
        this.stream.write(handleText);
        this.stream.write(SPACE);
        this.stream.write(prefixText);
        this.writeLineBreak(null);
    }

    private void writeSingleQuoted(String text, boolean split) {
        this.writeIndicator("'", true, false, false);
        boolean spaces = false;
        boolean breaks = false;
        int start = 0;
        for (int end = 0; end <= text.length(); ++end) {
            int len;
            char ch = '\u0000';
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (spaces) {
                if (ch != ' ') {
                    if (start + 1 == end && this.column > this.bestWidth && split && start != 0 && end != text.length()) {
                        this.writeIndent();
                    } else {
                        len = end - start;
                        this.column += len;
                        this.stream.write(text, start, len);
                    }
                    start = end;
                }
            } else if (breaks) {
                if (ch == '\u0000' || CharConstants.LINEBR.hasNo(ch)) {
                    if (text.charAt(start) == '\n') {
                        this.writeLineBreak(null);
                    }
                    String data = text.substring(start, end);
                    for (char br : data.toCharArray()) {
                        if (br == '\n') {
                            this.writeLineBreak(null);
                            continue;
                        }
                        this.writeLineBreak(String.valueOf(br));
                    }
                    this.writeIndent();
                    start = end;
                }
            } else if (CharConstants.LINEBR.has(ch, "\u0000 '") && start < end) {
                len = end - start;
                this.column += len;
                this.stream.write(text, start, len);
                start = end;
            }
            if (ch == '\'') {
                this.column += 2;
                this.stream.write("''");
                start = end + 1;
            }
            if (ch == '\u0000') continue;
            spaces = ch == ' ';
            breaks = CharConstants.LINEBR.has(ch);
        }
        this.writeIndicator("'", false, false, false);
    }

    private void writeDoubleQuoted(String text, boolean split) {
        this.writeIndicator("\"", true, false, false);
        int start = 0;
        for (int end = 0; end <= text.length(); ++end) {
            Character ch = null;
            if (end < text.length()) {
                ch = Character.valueOf(text.charAt(end));
            }
            if (ch == null || "\"\\\u0085\u2028\u2029\ufeff".indexOf(ch.charValue()) != -1 || ' ' > ch.charValue() || ch.charValue() > '~') {
                if (start < end) {
                    int len = end - start;
                    this.column += len;
                    this.stream.write(text, start, len);
                    start = end;
                }
                if (ch != null) {
                    String data;
                    if (ESCAPE_REPLACEMENTS.containsKey(ch)) {
                        data = "\\" + ESCAPE_REPLACEMENTS.get(ch);
                    } else {
                        int codePoint;
                        if (Character.isHighSurrogate(ch.charValue()) && end + 1 < text.length()) {
                            char ch2 = text.charAt(end + 1);
                            codePoint = Character.toCodePoint(ch.charValue(), ch2);
                        } else {
                            codePoint = ch.charValue();
                        }
                        if (this.allowUnicode && StreamReader.isPrintable(codePoint)) {
                            data = String.valueOf(Character.toChars(codePoint));
                            if (Character.charCount(codePoint) == 2) {
                                ++end;
                            }
                        } else if (ch.charValue() <= '\u00ff') {
                            String s = "0" + Integer.toString(ch.charValue(), 16);
                            data = "\\x" + s.substring(s.length() - 2);
                        } else if (Character.charCount(codePoint) == 2) {
                            ++end;
                            String s = "000" + Long.toHexString(codePoint);
                            data = "\\U" + s.substring(s.length() - 8);
                        } else {
                            String s = "000" + Integer.toString(ch.charValue(), 16);
                            data = "\\u" + s.substring(s.length() - 4);
                        }
                    }
                    this.column += data.length();
                    this.stream.write(data);
                    start = end + 1;
                }
            }
            if (0 >= end || end >= text.length() - 1 || ch.charValue() != ' ' && start < end || this.column + (end - start) <= this.bestWidth || !split) continue;
            String data = start >= end ? "\\" : text.substring(start, end) + "\\";
            if (start < end) {
                start = end;
            }
            this.column += data.length();
            this.stream.write(data);
            this.writeIndent();
            this.whitespace = false;
            this.indention = false;
            if (text.charAt(start) != ' ') continue;
            data = "\\";
            this.column += data.length();
            this.stream.write(data);
        }
        this.writeIndicator("\"", false, false, false);
    }

    private boolean writeCommentLines(List<CommentLine> commentLines) {
        boolean wroteComment = false;
        if (this.emitComments) {
            int indentColumns = 0;
            int prevColumns = 0;
            boolean firstComment = true;
            for (CommentLine commentLine : commentLines) {
                if (commentLine.getCommentType() != CommentType.BLANK_LINE) {
                    if (firstComment) {
                        firstComment = false;
                        this.writeIndicator("#", commentLine.getCommentType() == CommentType.IN_LINE, false, false);
                        indentColumns = this.column > 0 ? this.column - 1 : 0;
                    } else {
                        this.writeWhitespace(indentColumns - prevColumns);
                        this.writeIndicator("#", false, false, false);
                    }
                    this.stream.write(commentLine.getValue());
                    this.writeLineBreak(null);
                    prevColumns = 0;
                } else {
                    this.writeLineBreak(null);
                    prevColumns = this.writeIndent();
                }
                wroteComment = true;
            }
        }
        return wroteComment;
    }

    private void writeBlockComment() {
        if (!this.blockCommentsCollector.isEmpty()) {
            this.writeIndent();
            this.writeCommentLines(this.blockCommentsCollector.consume());
        }
    }

    private boolean writeInlineComments() {
        return this.writeCommentLines(this.inlineCommentsCollector.consume());
    }

    private String determineBlockHints(String text) {
        char ch1;
        StringBuilder hints = new StringBuilder();
        if (CharConstants.LINEBR.has(text.charAt(0), SPACE)) {
            hints.append(this.bestIndent);
        }
        if (CharConstants.LINEBR.hasNo(ch1 = text.charAt(text.length() - 1))) {
            hints.append("-");
        } else if (text.length() == 1 || CharConstants.LINEBR.has(text.charAt(text.length() - 2))) {
            hints.append("+");
        }
        return hints.toString();
    }

    void writeFolded(String text, boolean split) {
        String hints = this.determineBlockHints(text);
        this.writeIndicator(">" + hints, true, false, false);
        if (hints.length() > 0 && hints.charAt(hints.length() - 1) == '+') {
            this.openEnded = true;
        }
        if (!this.writeInlineComments()) {
            this.writeLineBreak(null);
        }
        boolean leadingSpace = true;
        boolean spaces = false;
        boolean breaks = true;
        int start = 0;
        for (int end = 0; end <= text.length(); ++end) {
            char ch = '\u0000';
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (breaks) {
                if (ch == '\u0000' || CharConstants.LINEBR.hasNo(ch)) {
                    if (!leadingSpace && ch != '\u0000' && ch != ' ' && text.charAt(start) == '\n') {
                        this.writeLineBreak(null);
                    }
                    leadingSpace = ch == ' ';
                    String data = text.substring(start, end);
                    for (char br : data.toCharArray()) {
                        if (br == '\n') {
                            this.writeLineBreak(null);
                            continue;
                        }
                        this.writeLineBreak(String.valueOf(br));
                    }
                    if (ch != '\u0000') {
                        this.writeIndent();
                    }
                    start = end;
                }
            } else if (spaces) {
                if (ch != ' ') {
                    if (start + 1 == end && this.column > this.bestWidth && split) {
                        this.writeIndent();
                    } else {
                        int len = end - start;
                        this.column += len;
                        this.stream.write(text, start, len);
                    }
                    start = end;
                }
            } else if (CharConstants.LINEBR.has(ch, "\u0000 ")) {
                int len = end - start;
                this.column += len;
                this.stream.write(text, start, len);
                if (ch == '\u0000') {
                    this.writeLineBreak(null);
                }
                start = end;
            }
            if (ch == '\u0000') continue;
            breaks = CharConstants.LINEBR.has(ch);
            spaces = ch == ' ';
        }
    }

    void writeLiteral(String text) {
        String hints = this.determineBlockHints(text);
        this.writeIndicator("|" + hints, true, false, false);
        if (hints.length() > 0 && hints.charAt(hints.length() - 1) == '+') {
            this.openEnded = true;
        }
        if (!this.writeInlineComments()) {
            this.writeLineBreak(null);
        }
        boolean breaks = true;
        int start = 0;
        for (int end = 0; end <= text.length(); ++end) {
            char ch = '\u0000';
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (breaks) {
                if (ch == '\u0000' || CharConstants.LINEBR.hasNo(ch)) {
                    String data = text.substring(start, end);
                    for (char br : data.toCharArray()) {
                        if (br == '\n') {
                            this.writeLineBreak(null);
                            continue;
                        }
                        this.writeLineBreak(String.valueOf(br));
                    }
                    if (ch != '\u0000') {
                        this.writeIndent();
                    }
                    start = end;
                }
            } else if (ch == '\u0000' || CharConstants.LINEBR.has(ch)) {
                this.stream.write(text, start, end - start);
                if (ch == '\u0000') {
                    this.writeLineBreak(null);
                }
                start = end;
            }
            if (ch == '\u0000') continue;
            breaks = CharConstants.LINEBR.has(ch);
        }
    }

    void writePlain(String text, boolean split) {
        if (this.rootContext) {
            this.openEnded = true;
        }
        if (text.isEmpty()) {
            return;
        }
        if (!this.whitespace) {
            ++this.column;
            this.stream.write(SPACE);
        }
        this.whitespace = false;
        this.indention = false;
        boolean spaces = false;
        boolean breaks = false;
        int start = 0;
        for (int end = 0; end <= text.length(); ++end) {
            int len;
            char ch = '\u0000';
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (spaces) {
                if (ch != ' ') {
                    if (start + 1 == end && this.column > this.bestWidth && split) {
                        this.writeIndent();
                        this.whitespace = false;
                        this.indention = false;
                    } else {
                        len = end - start;
                        this.column += len;
                        this.stream.write(text, start, len);
                    }
                    start = end;
                }
            } else if (breaks) {
                if (CharConstants.LINEBR.hasNo(ch)) {
                    if (text.charAt(start) == '\n') {
                        this.writeLineBreak(null);
                    }
                    String data = text.substring(start, end);
                    for (char br : data.toCharArray()) {
                        if (br == '\n') {
                            this.writeLineBreak(null);
                            continue;
                        }
                        this.writeLineBreak(String.valueOf(br));
                    }
                    this.writeIndent();
                    this.whitespace = false;
                    this.indention = false;
                    start = end;
                }
            } else if (CharConstants.LINEBR.has(ch, "\u0000 ")) {
                len = end - start;
                this.column += len;
                this.stream.write(text, start, len);
                start = end;
            }
            if (ch == '\u0000') continue;
            spaces = ch == ' ';
            breaks = CharConstants.LINEBR.has(ch);
        }
    }

    static {
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\u0000'), "0");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\u0007'), "a");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\b'), "b");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\t'), "t");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\n'), "n");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\u000b'), "v");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\f'), "f");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\r'), "r");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\u001b'), "e");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\"'), "\"");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\\'), "\\");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\u0085'), "N");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\u00a0'), "_");
        DEFAULT_TAG_PREFIXES = new LinkedHashMap<String, String>();
        DEFAULT_TAG_PREFIXES.put("!", "!");
        DEFAULT_TAG_PREFIXES.put("tag:yaml.org,2002:", "!!");
        HANDLE_FORMAT = Pattern.compile("^![-_\\w]*!$");
    }

    private class ExpectStreamStart
    implements EmitterState {
        private ExpectStreamStart() {
        }

        @Override
        public void expect() {
            if (Emitter.this.event.getEventId() != Event.ID.StreamStart) {
                throw new EmitterException("expected StreamStartEvent, but got " + Emitter.this.event);
            }
            Emitter.this.writeStreamStart();
            Emitter.this.state = new ExpectFirstDocumentStart();
        }
    }

    private class ExpectFirstFlowSequenceItem
    implements EmitterState {
        private ExpectFirstFlowSequenceItem() {
        }

        @Override
        public void expect() {
            if (Emitter.this.event.getEventId() == Event.ID.SequenceEnd) {
                Emitter.this.indent = (Integer)Emitter.this.indents.pop();
                Emitter.this.flowLevel--;
                Emitter.this.writeIndicator("]", false, false, false);
                Emitter.this.inlineCommentsCollector.collectEvents();
                Emitter.this.writeInlineComments();
                Emitter.this.state = (EmitterState)Emitter.this.states.pop();
            } else if (Emitter.this.event instanceof CommentEvent) {
                Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
                Emitter.this.writeBlockComment();
            } else {
                if (Emitter.this.canonical.booleanValue() || Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines || Emitter.this.multiLineFlow.booleanValue()) {
                    Emitter.this.writeIndent();
                }
                Emitter.this.states.push(new ExpectFlowSequenceItem());
                Emitter.this.expectNode(false, false, false);
                Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
                Emitter.this.writeInlineComments();
            }
        }
    }

    private class ExpectFirstFlowMappingKey
    implements EmitterState {
        private ExpectFirstFlowMappingKey() {
        }

        @Override
        public void expect() {
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeBlockComment();
            if (Emitter.this.event.getEventId() == Event.ID.MappingEnd) {
                Emitter.this.indent = (Integer)Emitter.this.indents.pop();
                Emitter.this.flowLevel--;
                Emitter.this.writeIndicator("}", false, false, false);
                Emitter.this.inlineCommentsCollector.collectEvents();
                Emitter.this.writeInlineComments();
                Emitter.this.state = (EmitterState)Emitter.this.states.pop();
            } else {
                if (Emitter.this.canonical.booleanValue() || Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines || Emitter.this.multiLineFlow.booleanValue()) {
                    Emitter.this.writeIndent();
                }
                if (!Emitter.this.canonical.booleanValue() && Emitter.this.checkSimpleKey()) {
                    Emitter.this.states.push(new ExpectFlowMappingSimpleValue());
                    Emitter.this.expectNode(false, true, true);
                } else {
                    Emitter.this.writeIndicator("?", true, false, false);
                    Emitter.this.states.push(new ExpectFlowMappingValue());
                    Emitter.this.expectNode(false, true, false);
                }
            }
        }
    }

    private class ExpectFirstBlockSequenceItem
    implements EmitterState {
        private ExpectFirstBlockSequenceItem() {
        }

        @Override
        public void expect() {
            new ExpectBlockSequenceItem(true).expect();
        }
    }

    private class ExpectFirstBlockMappingKey
    implements EmitterState {
        private ExpectFirstBlockMappingKey() {
        }

        @Override
        public void expect() {
            new ExpectBlockMappingKey(true).expect();
        }
    }

    private class ExpectBlockMappingValue
    implements EmitterState {
        private ExpectBlockMappingValue() {
        }

        @Override
        public void expect() {
            Emitter.this.writeIndent();
            Emitter.this.writeIndicator(":", true, false, true);
            Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeInlineComments();
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeBlockComment();
            Emitter.this.states.push(new ExpectBlockMappingKey(false));
            Emitter.this.expectNode(false, true, false);
            Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
            Emitter.this.writeInlineComments();
        }
    }

    private class ExpectBlockMappingSimpleValue
    implements EmitterState {
        private ExpectBlockMappingSimpleValue() {
        }

        @Override
        public void expect() {
            Emitter.this.writeIndicator(":", false, false, false);
            Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            if (!Emitter.this.isFoldedOrLiteral(Emitter.this.event) && Emitter.this.writeInlineComments()) {
                Emitter.this.increaseIndent(true, false);
                Emitter.this.writeIndent();
                Emitter.this.indent = (Integer)Emitter.this.indents.pop();
            }
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            if (!Emitter.this.blockCommentsCollector.isEmpty()) {
                Emitter.this.increaseIndent(true, false);
                Emitter.this.writeBlockComment();
                Emitter.this.writeIndent();
                Emitter.this.indent = (Integer)Emitter.this.indents.pop();
            }
            Emitter.this.states.push(new ExpectBlockMappingKey(false));
            Emitter.this.expectNode(false, true, false);
            Emitter.this.inlineCommentsCollector.collectEvents();
            Emitter.this.writeInlineComments();
        }
    }

    private class ExpectBlockMappingKey
    implements EmitterState {
        private final boolean first;

        public ExpectBlockMappingKey(boolean first) {
            this.first = first;
        }

        @Override
        public void expect() {
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeBlockComment();
            if (!this.first && Emitter.this.event.getEventId() == Event.ID.MappingEnd) {
                Emitter.this.indent = (Integer)Emitter.this.indents.pop();
                Emitter.this.state = (EmitterState)Emitter.this.states.pop();
            } else {
                Emitter.this.writeIndent();
                if (Emitter.this.checkSimpleKey()) {
                    Emitter.this.states.push(new ExpectBlockMappingSimpleValue());
                    Emitter.this.expectNode(false, true, true);
                } else {
                    Emitter.this.writeIndicator("?", true, false, true);
                    Emitter.this.states.push(new ExpectBlockMappingValue());
                    Emitter.this.expectNode(false, true, false);
                }
            }
        }
    }

    private class ExpectBlockSequenceItem
    implements EmitterState {
        private final boolean first;

        public ExpectBlockSequenceItem(boolean first) {
            this.first = first;
        }

        @Override
        public void expect() {
            if (!this.first && Emitter.this.event.getEventId() == Event.ID.SequenceEnd) {
                Emitter.this.indent = (Integer)Emitter.this.indents.pop();
                Emitter.this.state = (EmitterState)Emitter.this.states.pop();
            } else if (Emitter.this.event instanceof CommentEvent) {
                Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
            } else {
                Emitter.this.writeIndent();
                if (!Emitter.this.indentWithIndicator || this.first) {
                    Emitter.this.writeWhitespace(Emitter.this.indicatorIndent);
                }
                Emitter.this.writeIndicator("-", true, false, true);
                if (Emitter.this.indentWithIndicator && this.first) {
                    Emitter.this.indent = Emitter.this.indent + Emitter.this.indicatorIndent;
                }
                if (!Emitter.this.blockCommentsCollector.isEmpty()) {
                    Emitter.this.increaseIndent(false, false);
                    Emitter.this.writeBlockComment();
                    if (Emitter.this.event instanceof ScalarEvent) {
                        Emitter.this.analysis = Emitter.this.analyzeScalar(((ScalarEvent)Emitter.this.event).getValue());
                        if (!Emitter.this.analysis.isEmpty()) {
                            Emitter.this.writeIndent();
                        }
                    }
                    Emitter.this.indent = (Integer)Emitter.this.indents.pop();
                }
                Emitter.this.states.push(new ExpectBlockSequenceItem(false));
                Emitter.this.expectNode(false, false, false);
                Emitter.this.inlineCommentsCollector.collectEvents();
                Emitter.this.writeInlineComments();
            }
        }
    }

    private class ExpectFlowMappingValue
    implements EmitterState {
        private ExpectFlowMappingValue() {
        }

        @Override
        public void expect() {
            if (Emitter.this.canonical.booleanValue() || Emitter.this.column > Emitter.this.bestWidth || Emitter.this.multiLineFlow.booleanValue()) {
                Emitter.this.writeIndent();
            }
            Emitter.this.writeIndicator(":", true, false, false);
            Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeInlineComments();
            Emitter.this.states.push(new ExpectFlowMappingKey());
            Emitter.this.expectNode(false, true, false);
            Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
            Emitter.this.writeInlineComments();
        }
    }

    private class ExpectFlowMappingSimpleValue
    implements EmitterState {
        private ExpectFlowMappingSimpleValue() {
        }

        @Override
        public void expect() {
            Emitter.this.writeIndicator(":", false, false, false);
            Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeInlineComments();
            Emitter.this.states.push(new ExpectFlowMappingKey());
            Emitter.this.expectNode(false, true, false);
            Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
            Emitter.this.writeInlineComments();
        }
    }

    private class ExpectFlowMappingKey
    implements EmitterState {
        private ExpectFlowMappingKey() {
        }

        @Override
        public void expect() {
            if (Emitter.this.event.getEventId() == Event.ID.MappingEnd) {
                Emitter.this.indent = (Integer)Emitter.this.indents.pop();
                Emitter.this.flowLevel--;
                if (Emitter.this.canonical.booleanValue()) {
                    Emitter.this.writeIndicator(",", false, false, false);
                    Emitter.this.writeIndent();
                }
                if (Emitter.this.multiLineFlow.booleanValue()) {
                    Emitter.this.writeIndent();
                }
                Emitter.this.writeIndicator("}", false, false, false);
                Emitter.this.inlineCommentsCollector.collectEvents();
                Emitter.this.writeInlineComments();
                Emitter.this.state = (EmitterState)Emitter.this.states.pop();
            } else {
                Emitter.this.writeIndicator(",", false, false, false);
                Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
                Emitter.this.writeBlockComment();
                if (Emitter.this.canonical.booleanValue() || Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines || Emitter.this.multiLineFlow.booleanValue()) {
                    Emitter.this.writeIndent();
                }
                if (!Emitter.this.canonical.booleanValue() && Emitter.this.checkSimpleKey()) {
                    Emitter.this.states.push(new ExpectFlowMappingSimpleValue());
                    Emitter.this.expectNode(false, true, true);
                } else {
                    Emitter.this.writeIndicator("?", true, false, false);
                    Emitter.this.states.push(new ExpectFlowMappingValue());
                    Emitter.this.expectNode(false, true, false);
                }
            }
        }
    }

    private class ExpectFlowSequenceItem
    implements EmitterState {
        private ExpectFlowSequenceItem() {
        }

        @Override
        public void expect() {
            if (Emitter.this.event.getEventId() == Event.ID.SequenceEnd) {
                Emitter.this.indent = (Integer)Emitter.this.indents.pop();
                Emitter.this.flowLevel--;
                if (Emitter.this.canonical.booleanValue()) {
                    Emitter.this.writeIndicator(",", false, false, false);
                    Emitter.this.writeIndent();
                } else if (Emitter.this.multiLineFlow.booleanValue()) {
                    Emitter.this.writeIndent();
                }
                Emitter.this.writeIndicator("]", false, false, false);
                Emitter.this.inlineCommentsCollector.collectEvents();
                Emitter.this.writeInlineComments();
                if (Emitter.this.multiLineFlow.booleanValue()) {
                    Emitter.this.writeIndent();
                }
                Emitter.this.state = (EmitterState)Emitter.this.states.pop();
            } else if (Emitter.this.event instanceof CommentEvent) {
                Emitter.this.event = Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
            } else {
                Emitter.this.writeIndicator(",", false, false, false);
                Emitter.this.writeBlockComment();
                if (Emitter.this.canonical.booleanValue() || Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines || Emitter.this.multiLineFlow.booleanValue()) {
                    Emitter.this.writeIndent();
                }
                Emitter.this.states.push(new ExpectFlowSequenceItem());
                Emitter.this.expectNode(false, false, false);
                Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
                Emitter.this.writeInlineComments();
            }
        }
    }

    private class ExpectDocumentRoot
    implements EmitterState {
        private ExpectDocumentRoot() {
        }

        @Override
        public void expect() {
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            if (!Emitter.this.blockCommentsCollector.isEmpty()) {
                Emitter.this.writeBlockComment();
                if (Emitter.this.event instanceof DocumentEndEvent) {
                    new ExpectDocumentEnd().expect();
                    return;
                }
            }
            Emitter.this.states.push(new ExpectDocumentEnd());
            Emitter.this.expectNode(true, false, false);
        }
    }

    private class ExpectDocumentEnd
    implements EmitterState {
        private ExpectDocumentEnd() {
        }

        @Override
        public void expect() {
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeBlockComment();
            if (Emitter.this.event.getEventId() == Event.ID.DocumentEnd) {
                Emitter.this.writeIndent();
                if (((DocumentEndEvent)Emitter.this.event).isExplicit()) {
                    Emitter.this.writeIndicator("...", true, false, false);
                    Emitter.this.writeIndent();
                }
            } else {
                throw new EmitterException("expected DocumentEndEvent, but got " + Emitter.this.event);
            }
            Emitter.this.flushStream();
            Emitter.this.state = new ExpectDocumentStart(false);
        }
    }

    private class ExpectDocumentStart
    implements EmitterState {
        private final boolean first;

        public ExpectDocumentStart(boolean first) {
            this.first = first;
        }

        @Override
        public void expect() {
            if (Emitter.this.event.getEventId() == Event.ID.DocumentStart) {
                DocumentStartEvent ev = (DocumentStartEvent)Emitter.this.event;
                this.handleDocumentStartEvent(ev);
                Emitter.this.state = new ExpectDocumentRoot();
            } else if (Emitter.this.event.getEventId() == Event.ID.StreamEnd) {
                Emitter.this.writeStreamEnd();
                Emitter.this.state = new ExpectNothing();
            } else if (Emitter.this.event instanceof CommentEvent) {
                Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
                Emitter.this.writeBlockComment();
            } else {
                throw new EmitterException("expected DocumentStartEvent, but got " + Emitter.this.event);
            }
        }

        private void handleDocumentStartEvent(DocumentStartEvent ev) {
            boolean implicit;
            if ((ev.getSpecVersion().isPresent() || !ev.getTags().isEmpty()) && Emitter.this.openEnded) {
                Emitter.this.writeIndicator("...", true, false, false);
                Emitter.this.writeIndent();
            }
            ev.getSpecVersion().ifPresent(version -> Emitter.this.writeVersionDirective(Emitter.this.prepareVersion(version)));
            Emitter.this.tagPrefixes = new LinkedHashMap(DEFAULT_TAG_PREFIXES);
            if (!ev.getTags().isEmpty()) {
                this.handleTagDirectives(ev.getTags());
            }
            boolean bl = implicit = this.first && !ev.isExplicit() && Emitter.this.canonical == false && !ev.getSpecVersion().isPresent() && ev.getTags().isEmpty() && !this.checkEmptyDocument();
            if (!implicit) {
                Emitter.this.writeIndent();
                Emitter.this.writeIndicator("---", true, false, false);
                if (Emitter.this.canonical.booleanValue()) {
                    Emitter.this.writeIndent();
                }
            }
        }

        private void handleTagDirectives(Map<String, String> tags) {
            TreeSet<String> handles = new TreeSet<String>(tags.keySet());
            for (String handle : handles) {
                String prefix = tags.get(handle);
                Emitter.this.tagPrefixes.put(prefix, handle);
                String handleText = Emitter.this.prepareTagHandle(handle);
                String prefixText = Emitter.this.prepareTagPrefix(prefix);
                Emitter.this.writeTagDirective(handleText, prefixText);
            }
        }

        private boolean checkEmptyDocument() {
            if (Emitter.this.event.getEventId() != Event.ID.DocumentStart || Emitter.this.events.isEmpty()) {
                return false;
            }
            Event nextEvent = (Event)Emitter.this.events.peek();
            if (nextEvent.getEventId() == Event.ID.Scalar) {
                ScalarEvent e = (ScalarEvent)nextEvent;
                return !e.getAnchor().isPresent() && !e.getTag().isPresent() && e.getImplicit() != null && e.getValue().isEmpty();
            }
            return false;
        }
    }

    private class ExpectFirstDocumentStart
    implements EmitterState {
        private ExpectFirstDocumentStart() {
        }

        @Override
        public void expect() {
            new ExpectDocumentStart(true).expect();
        }
    }

    private class ExpectNothing
    implements EmitterState {
        private ExpectNothing() {
        }

        @Override
        public void expect() {
            throw new EmitterException("expecting nothing, but got " + Emitter.this.event);
        }
    }
}

