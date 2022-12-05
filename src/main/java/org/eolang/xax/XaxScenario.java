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
import com.yegor256.xsline.Train;
import java.util.Arrays;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * Scenario in a YAML.
 *
 * @since 0.0.1
 */
public final class XaxScenario {

    /**
     * The YAML.
     */
    private final String yaml;

    /**
     * Ctor.
     * @param yml YAML
     */
    public XaxScenario(final String yml) {
        this.yaml = yml;
    }

    @Override
    public String toString() {
        return this.document().toString();
    }

    /**
     * Skip it?
     * @return TRUE if skip
     */
    public boolean skip() {
        final Map<String, Object> map = new Yaml().load(this.yaml);
        return (boolean) map.get("skip");
    }

    /**
     * Take the XML document.
     * @return XML
     */
    public XML document() {
        final Map<String, Object> map = new Yaml().load(this.yaml);
        return new XMLDocument(
            map.get("document").toString()
        );
    }

    /**
     * Take the processing train of XSL sheets.
     * @return The train
     */
    @SuppressWarnings("unchecked")
    public Train<Shift> train() {
        final Map<String, Object> map = new Yaml().load(this.yaml);
        TrClasspath<Shift> train = new TrClasspath<>();
        Object list = map.get("sheets");
        if (list == null) {
            list = Arrays.asList();
        }
        for (final String sheet : (Iterable<String>) list) {
            train = train.with(sheet);
        }
        return train.back();
    }

    /**
     * Take the list of XPath asserts.
     * @return The list
     */
    @SuppressWarnings("unchecked")
    public Iterable<String> asserts() {
        final Map<String, Object> map = new Yaml().load(this.yaml);
        Object list = map.get("asserts");
        if (list == null) {
            list = Arrays.asList();
        }
        return (Iterable<String>) list;
    }

}
