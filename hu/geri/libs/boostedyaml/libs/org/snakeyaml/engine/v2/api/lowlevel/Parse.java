/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.lowlevel;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.LoadSettings;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.YamlUnicodeReader;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.events.Event;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.parser.ParserImpl;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.scanner.StreamReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Objects;

public class Parse {
    private final LoadSettings settings;

    public Parse(LoadSettings settings) {
        Objects.requireNonNull(settings, "LoadSettings cannot be null");
        this.settings = settings;
    }

    public Iterable<Event> parseInputStream(InputStream yaml) {
        Objects.requireNonNull(yaml, "InputStream cannot be null");
        return () -> new ParserImpl(this.settings, new StreamReader(this.settings, new YamlUnicodeReader(yaml)));
    }

    public Iterable<Event> parseReader(Reader yaml) {
        Objects.requireNonNull(yaml, "Reader cannot be null");
        return () -> new ParserImpl(this.settings, new StreamReader(this.settings, yaml));
    }

    public Iterable<Event> parseString(final String yaml) {
        Objects.requireNonNull(yaml, "String cannot be null");
        return new Iterable<Event>(){

            @Override
            public Iterator<Event> iterator() {
                return new ParserImpl(Parse.this.settings, new StreamReader(Parse.this.settings, new StringReader(yaml)));
            }
        };
    }
}

