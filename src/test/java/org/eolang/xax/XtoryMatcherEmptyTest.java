/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.eolang.xax;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for stories without XPath assertions.
 *
 * @since 1.0
 */
final class XtoryMatcherEmptyTest {

    @ParameterizedTest
    @ValueSource(strings = {"", "assert: /doc", "assertions: /doc", "asserts:", "asserts: []"})
    void rejectsEmptyAssertionSets(final String entry) {
        final Xtory story = new XtYaml(String.format("document: <doc/>%n%s%n", entry));
        Assertions.assertAll(
            () -> MatcherAssert.assertThat(
                "A missing assertion set must explain why the story is rejected",
                Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> new XtoryMatcher().matches(story)
                ).getMessage(),
                Matchers.containsString("No XPath assertions")
            ),
            () -> Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new XtoryMatcher(XhtmlMatchers.hasXPath("/doc")).matches(story),
                "An extra matcher must not silently allow a missing assertion set"
            )
        );
    }

    @Test
    void acceptsAValidAssertionWithAnExtraMatcher() {
        MatcherAssert.assertThat(
            "A valid assertion set must still reach its extra matcher",
            new XtoryMatcher(XhtmlMatchers.hasXPath("/doc")).matches(
                new XtYaml(String.format("document: <doc/>%nasserts:%n  - /doc%n"))
            ),
            Matchers.is(true)
        );
    }
}
