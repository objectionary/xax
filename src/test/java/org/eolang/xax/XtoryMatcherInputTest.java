/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.eolang.xax;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for descriptions and incompatible matcher inputs.
 * @since 1.0
 */
final class XtoryMatcherInputTest {
    @Test
    void describesBeforeMatching() {
        MatcherAssert.assertThat(
            "A fresh matcher must describe its expectation",
            new XtoryMatcher().toString(),
            Matchers.containsString("story")
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"another value"})
    void rejectsIncompatibleValues(final String value) {
        MatcherAssert.assertThat(
            "An incompatible value must produce a non-match",
            new XtoryMatcher().matches(value),
            Matchers.is(false)
        );
    }

    @Test
    void clearsDiagnosticsWhenReusedWithAnIncompatibleValue() {
        final XtoryMatcher matcher = new XtoryMatcher();
        matcher.matches(new XtYaml(String.format("document: <doc/>%nasserts:%n  - /doc%n")));
        matcher.matches("another value");
        final StringDescription description = new StringDescription();
        matcher.describeMismatch("another value", description);
        MatcherAssert.assertThat(
            "Mismatch diagnostics must describe the current incompatible value",
            description.toString(),
            Matchers.containsString("another value")
        );
    }
}
