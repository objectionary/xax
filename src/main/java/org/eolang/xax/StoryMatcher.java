/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.xax;

import com.jcabi.xml.StrictXML;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSLDocument;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.StClasspath;
import com.yegor256.xsline.StXSL;
import com.yegor256.xsline.TrDefault;
import com.yegor256.xsline.Train;
import com.yegor256.xsline.Xsline;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Assumptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Hamcrest matcher for a YAML story.
 *
 * @since 0.1.0
 */
public final class StoryMatcher extends BaseMatcher<String> {

    /**
     * The train to start with.
     */
    private final Train<Shift> train;

    /**
     * The parser to use, when {@code input} is provided in the YAML.
     */
    private final StoryMatcher.Parser parser;

    /**
     * It must be strict on XML?
     */
    private final boolean strict;

    /**
     * The header of the match.
     */
    private String header;

    /**
     * The summary of the match.
     */
    private String summary;

    /**
     * Ctor.
     */
    public StoryMatcher() {
        this(
            input -> {
                throw new UnsupportedOperationException(
                    "Parser is not provided, while YAML doesn't have the 'document' property"
                );
            }
        );
    }

    /**
     * Ctor.
     * @param prsr The parser to use
     */
    public StoryMatcher(final StoryMatcher.Parser prsr) {
        this(prsr, new TrDefault<>());
    }

    /**
     * Ctor.
     * @param prsr The parser to use
     * @param trn The train to start with
     * @since 0.1.1
     */
    public StoryMatcher(final StoryMatcher.Parser prsr, final Train<Shift> trn) {
        this(prsr, trn, false);
    }

    /**
     * Ctor.
     * @param prsr The parser to use
     * @param trn The train to start with
     * @param strct To be strict on XML?
     * @since 0.2.0
     */
    public StoryMatcher(final StoryMatcher.Parser prsr, final Train<Shift> trn,
        final boolean strct) {
        super();
        this.parser = prsr;
        this.train = trn;
        this.strict = strct;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean matches(final Object story) {
        final Map<String, Object> yaml = new Yaml().load(
            String.class.cast(story)
        );
        Assumptions.assumeTrue(yaml.get("skip") == null);
        final XML before = this.before(yaml);
        final XML after = this.valid(this.xsline(yaml).pass(before));
        Object asserts = yaml.get("asserts");
        if (asserts == null) {
            asserts = Arrays.asList();
        }
        final Collection<Map.Entry<String, Boolean>> xpaths =
            new LinkedList<>();
        int failures = 0;
        for (final String xpath : (Iterable<String>) asserts) {
            final boolean success = !after.nodes(xpath).isEmpty();
            xpaths.add(
                new AbstractMap.SimpleImmutableEntry<>(
                    xpath,
                    success
                )
            );
            if (!success) {
                ++failures;
            }
        }
        this.header = String.format(
            "All %d XPath expressions matched",
            xpaths.size()
        );
        final StringBuilder sum = new StringBuilder(1024)
            .append(String.format("%d XPath expression(s) failed:\n", failures));
        for (final Map.Entry<String, Boolean> ent : xpaths) {
            sum.append("  ");
            if (ent.getValue()) {
                sum.append("OK");
            } else {
                sum.append("FAIL");
            }
            sum.append(": ").append(ent.getKey()).append('\n');
        }
        sum
            .append("\nXML before XSL transformation:\n  ")
            .append(before.toString().replace("\n", "\n  "))
            .append(
                String.format(
                    "\nXML after XSL transformation (%d->%d chars):\n  ",
                    before.toString().length(),
                    after.toString().length()
                )
            )
            .append(after.toString().replace("\n", "\n  "));
        this.summary = sum.toString();
        boolean match = true;
        for (final Map.Entry<String, Boolean> ent : xpaths) {
            if (!ent.getValue()) {
                match = false;
                break;
            }
        }
        return match;
    }

    @Override
    public void describeTo(final Description desc) {
        desc.appendText(this.header);
    }

    @Override
    public void describeMismatch(final Object log, final Description desc) {
        desc.appendText("\n").appendText(this.summary);
    }

    /**
     * Build input XML document.
     * @param yaml The YAML
     * @return The XML
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private XML before(final Map<String, Object> yaml) {
        final Object doc = yaml.get("document");
        final XML xml;
        if (doc == null) {
            try {
                xml = this.parser.parse(yaml.get("input").toString());
                // @checkstyle IllegalCatchCheck (1 line)
            } catch (final Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        } else {
            xml = new XMLDocument(doc.toString());
        }
        return this.valid(xml);
    }

    /**
     * Build a transformation line.
     * @param yaml The YAML
     * @return The line
     */
    @SuppressWarnings("unchecked")
    private Xsline xsline(final Map<String, Object> yaml) {
        Object sheets = yaml.get("sheets");
        if (sheets == null) {
            sheets = Arrays.asList();
        }
        Train<Shift> trn = this.train;
        for (final String sheet : (Iterable<String>) sheets) {
            if (sheet.startsWith("file://")) {
                try {
                    trn = trn.with(
                        new StXSL(
                            new XSLDocument(Paths.get(sheet.substring(7)))
                        )
                    );
                } catch (final FileNotFoundException ex) {
                    throw new IllegalArgumentException(ex);
                }
            } else {
                trn = trn.with(new StClasspath(sheet));
            }
        }
        return new Xsline(trn);
    }

    /**
     * Build XML that is ready for processing.
     * @param xml Original XML
     * @return The XML after validation
     */
    private XML valid(final XML xml) {
        XML out = xml;
        if (this.strict) {
            out = new StrictXML(xml);
        }
        return out;
    }

    /**
     * The parser.
     *
     * @since 0.3.0
     */
    public interface Parser {
        /**
         * Parse the input.
         * @param input The incoming data
         * @return XML parsed
         * @throws Exception If fails
         */
        XML parse(String input) throws Exception;
    }

}
