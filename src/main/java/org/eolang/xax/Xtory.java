/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2022-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
public interface Xtory {

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

    /**
     * A parser of a {@link String} to {@link XML}.
     *
     * @since 0.4.0
     */
    interface Parser {

        /**
         * Parse the input.
         * @param input The incoming data
         * @return XML parsed
         * @throws Exception If fails
         */
        XML parse(String input) throws Exception;

    }
}
