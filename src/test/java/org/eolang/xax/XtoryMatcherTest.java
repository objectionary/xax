/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.eolang.xax;

import com.jcabi.matchers.XhtmlMatchers;
import org.cactoos.io.InputOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.jucs.ClasspathSource;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test case for {@link XtoryMatcher}.
 *
 * @since 0.1.0
 */
final class XtoryMatcherTest {

    @Test
    void printsItself() {
        MatcherAssert.assertThat(
            "No errors found in the story",
            new XtYaml(
                new UncheckedText(
                    new TextOf(
                        new ResourceOf("org/eolang/xax/packs/simple.yaml")
                    )
                ).asString()
            ),
            new XtoryMatcher()
        );
    }

    @Test
    void appliesExtraMatcher() {
        MatcherAssert.assertThat(
            "Extra matcher is not applied",
            new XtYaml(
                new UncheckedText(
                    new TextOf(
                        new ResourceOf("org/eolang/xax/packs/simple.yaml")
                    )
                ).asString()
            ),
            new XtoryMatcher(
                XhtmlMatchers.hasXPath("/doc/a/foo")
            )
        );
    }

    @Test
    void appliesExtraMatcherAndFails() {
        MatcherAssert.assertThat(
            "Extra matcher is not applied",
            new XtYaml(
                new UncheckedText(
                    new TextOf(
                        new ResourceOf("org/eolang/xax/packs/simple.yaml")
                    )
                ).asString()
            ),
            Matchers.not(
                new XtoryMatcher(
                    XhtmlMatchers.hasXPath("/invalid-xpath")
                )
            )
        );
    }

    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/xax/packs", glob = "**.yaml")
    void validatesSimpleScenario(final String yaml) {
        MatcherAssert.assertThat(
            "passes with no exceptions",
            new XtYaml(
                yaml,
                eo -> new EoSyntax(new InputOf(eo)).parsed()
            ),
            new XtoryMatcher()
        );
    }

    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/xax/broken", glob = "**.yaml")
    void validatesBrokenScenario(final String yaml) {
        MatcherAssert.assertThat(
            "reports ",
            new XtYaml(yaml),
            Matchers.not(new XtoryMatcher())
        );
    }

}
