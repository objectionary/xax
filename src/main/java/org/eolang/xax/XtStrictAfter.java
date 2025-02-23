/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2022-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
