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

import com.jcabi.xml.XML;
import com.yegor256.xsline.Xsline;
import java.util.Collection;
import java.util.Map;

/**
 * A story about XML processed through XSL stylesheets.
 *
 * @since 0.4.0
 */
public interface Story {

    /**
     * Get YAML as a map.
     * @return The map
     */
    Map<String, Object> map();

    /**
     * Build input XML document.
     * @return The XML
     */
    XML before();

    /**
     * The XML after transformations.
     * @return The XML
     */
    XML after();

    /**
     * The transformation line.
     * @return The line
     */
    Xsline xsline();

    /**
     * Full list of XPath asserts.
     * @return List of XPath expressions
     */
    Collection<String> asserts();

}
