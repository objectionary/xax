/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.eolang.xax;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.opentest4j.TestAbortedException;

/**
 * Tests for the optional skip flag.
 * @since 1.0
 */
final class XtoryMatcherSkipTest {
    @ParameterizedTest
    @ValueSource(strings = {"", "skip: false", "skip: 'false'"})
    void runsEnabledStories(final String flag) {
        MatcherAssert.assertThat(
            "A false or absent skip flag must allow XPath evaluation",
            Assertions.assertDoesNotThrow(
                () -> new XtoryMatcher().matches(
                    new XtYaml(
                        String.format("document: <doc/>%nasserts:%n  - /missing%n%s%n", flag)
                    )
                ),
                "An enabled story must not be reported as skipped"
            ),
            Matchers.is(false)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"skip: true", "skip: 'true'"})
    void skipsDisabledStories(final String flag) {
        Assertions.assertThrows(
            TestAbortedException.class,
            () -> new XtoryMatcher().matches(new XtYaml(flag)),
            "A true skip flag must abort before parsing the missing document"
        );
    }
}
