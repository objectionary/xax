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
import com.yegor256.xsline.Xsline;
import java.util.Collection;
import java.util.Map;

/**
 * A decorator of {@link Xtory} that checks the validity
 * of XML against the XSD schema attached to it, right before
 * returning it at {@link Xtory#after()}.
 *
 * @since 0.4.0
 */
public final class XtStrictAfter implements Xtory {

    /**
     * Original story.
     */
    private final Xtory origin;

    /**
     * The XSD schema or NULL.
     */
    private final XML schema;

    /**
     * Ctor.
     * @param xtory Original story
     */
    public XtStrictAfter(final Xtory xtory) {
        this(xtory, null);
    }

    /**
     * Ctor.
     * @param xtory Original story
     * @param xsd XSD schema
     */
    public XtStrictAfter(final Xtory xtory, final XML xsd) {
        this.origin = xtory;
        this.schema = xsd;
    }

    @Override
    public Map<String, Object> map() {
        return this.origin.map();
    }

    @Override
    public XML before() {
        return this.origin.before();
    }

    @Override
    public XML after() {
        XML out = this.origin.after();
        if (this.schema == null) {
            out = new StrictXML(out);
        } else {
            out = new StrictXML(out, this.schema);
        }
        return out;
    }

    @Override
    public Xsline xsline() {
        return this.origin.xsline();
    }

    @Override
    public Collection<String> asserts() {
        return this.origin.asserts();
    }
}
