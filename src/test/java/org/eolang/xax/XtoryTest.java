/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.eolang.xax;

import com.jcabi.matchers.XhtmlMatchers;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Xtory}.
 *
 * @since 0.1.0
 */
final class XtoryTest {

    @Test
    void parsesAndTransforms() throws Exception {
        final Xtory xtory = new XtYaml(
            new TextOf(
                new ResourceOf("org/eolang/xax/packs/simple.yaml")
            ).asString()
        );
        MatcherAssert.assertThat(
            "Fails to find expected XPaths",
            XhtmlMatchers.xhtml(xtory.after()),
            XhtmlMatchers.hasXPaths(xtory.asserts())
        );
    }

    @Test
    void throwsOnInvalidSheetsAndAsserts() {
        Assertions.assertAll(
            () -> MatcherAssert.assertThat(
                "Throws IllegalArgumentException naming 'sheets'",
                Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> new XtYaml(
                        String.format("document: <a/>%nsheets: /x.xsl%n")
                    ).xsline()
                ).getMessage(),
                Matchers.containsString("sheets")
            ),
            () -> MatcherAssert.assertThat(
                "Throws IllegalArgumentException naming 'sheets' on item",
                Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> new XtYaml(
                        String.format("document: <a/>%nsheets:%n  - 42%n")
                    ).xsline()
                ).getMessage(),
                Matchers.containsString("sheets")
            ),
            () -> MatcherAssert.assertThat(
                "Throws IllegalArgumentException naming 'asserts' on scalar",
                Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> new XtYaml(
                        String.format("document: <a/>%nasserts: /a%n")
                    ).asserts()
                ).getMessage(),
                Matchers.containsString("asserts")
            ),
            () -> MatcherAssert.assertThat(
                "Throws IllegalArgumentException naming 'asserts'",
                Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> new XtYaml(
                        String.format("document: <a/>%nasserts:%n  - 42%n")
                    ).asserts()
                ).getMessage(),
                Matchers.containsString("asserts")
            )
        );
    }
}
