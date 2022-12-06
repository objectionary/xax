/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Yegor Bugayenko
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

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.TrClasspath;
import com.yegor256.xsline.Xsline;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import org.cactoos.Scalar;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;
import org.yaml.snakeyaml.Yaml;

/**
 * The test scenario in YAML.
 *
 * @since 0.1.0
 */
public final class XaxStory {

    /**
     * The YAML.
     */
    private final Scalar<Map<String, Object>> yaml;

    /**
     * The document before processing.
     */
    private final Scalar<XML> before;

    /**
     * The document after processing.
     */
    private final Scalar<XML> after;

    /**
     * The document before processing.
     */
    private final Scalar<Iterable<Map.Entry<String, Boolean>>> asserts;

    /**
     * Shall we skip it?
     */
    private final Scalar<Boolean> skip;

    /**
     * Ctor.
     * @param txt YAML as a text
     */
    @SuppressWarnings("unchecked")
    public XaxStory(final String txt) {
        this.yaml = new Sticky<>(() -> new Yaml().load(txt));
        this.before = new Sticky<>(
            () -> new XMLDocument(
                this.yaml.value().get("document").toString()
            )
        );
        this.after = new Sticky<>(
            () -> {
                TrClasspath<Shift> train = new TrClasspath<>();
                Object list = this.yaml.value().get("sheets");
                if (list == null) {
                    list = Arrays.asList();
                }
                for (final String sheet : (Iterable<String>) list) {
                    train = train.with(sheet);
                }
                return new Xsline(train.back()).pass(this.before.value());
            }
        );
        this.asserts = new Sticky<>(
            () -> {
                Object list = this.yaml.value().get("asserts");
                if (list == null) {
                    list = Arrays.asList();
                }
                final Collection<Map.Entry<String, Boolean>> results =
                    new LinkedList<>();
                for (final String xpath : (Iterable<String>) list) {
                    results.add(
                        new AbstractMap.SimpleImmutableEntry<>(
                            xpath,
                            !this.after.value().nodes(xpath).isEmpty()
                        )
                    );
                }
                return results;
            }
        );
        this.skip = new Sticky<>(
            () -> {
                Object flag = this.yaml.value().get("skip");
                if (flag == null) {
                    flag = Boolean.FALSE;
                }
                return (Boolean) flag;
            }
        );
    }

    @Override
    public String toString() {
        final StringBuilder txt = new StringBuilder(1024);
        txt
            .append(
                String.format(
                    "\nXML after XSL transformation (%d->%d chars):\n  ",
                    this.before.toString().length(),
                    this.after.toString().length()
                )
            )
            .append(
                new Unchecked<>(this.after)
                    .value()
                    .toString()
                    .replace("\n", "\n  ")
            )
            .append("\nAsserts:\n");
        for (final Map.Entry<String, Boolean> ent
            : new Unchecked<>(this.asserts).value()) {
            txt.append(
                String.format(
                    "  %s: %s\n",
                    ent.getValue(),
                    ent.getKey()
                )
            );
        }
        return txt.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Boolean)) {
            throw new IllegalArgumentException(
                String.format(
                    "Can't compare with anything except Boolean: %s",
                    obj.getClass()
                )
            );
        }
        boolean good = true;
        for (final Map.Entry<String, Boolean> ent
            : new Unchecked<>(this.asserts).value()) {
            if (!ent.getValue()) {
                good = false;
                break;
            }
        }
        return good || new Unchecked<>(this.skip).value();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("#hashCode()");
    }
}
