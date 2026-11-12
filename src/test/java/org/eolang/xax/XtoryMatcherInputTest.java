/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.eolang.xax;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XML;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for descriptions and incompatible matcher inputs.
 *
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

    @Test
    void describesDirectlyBeforeMatching() {
        final StringDescription description = new StringDescription();
        new XtoryMatcher().describeTo(description);
        MatcherAssert.assertThat(
            "Direct description must work before evaluation",
            description.toString(),
            Matchers.containsString("story")
        );
    }

    @Test
    void describesExtraMatcherBeforeMatching() {
        MatcherAssert.assertThat(
            "An extra XML matcher must retain a valid initial expectation",
            new XtoryMatcher(XhtmlMatchers.hasXPath("/doc")).toString(),
            Matchers.containsString("story")
        );
    }

    @ParameterizedTest
    @MethodSource("incompatibleValues")
    void rejectsIncompatibleValues(final Object value, final boolean reused) {
        final AtomicBoolean called = new AtomicBoolean();
        final XtoryMatcher matcher = new XtoryMatcher(
            new BaseMatcher<XML>() {
                @Override
                public boolean matches(final Object item) {
                    called.set(true);
                    return true;
                }

                @Override
                public void describeTo(final Description description) {
                    description.appendText("an XML document");
                }
            }
        );
        if (reused) {
            matcher.matches(new XtYaml(String.format("document: <doc/>%nasserts:%n  - /doc%n")));
            called.set(false);
        }
        final boolean matches = matcher.matches(value);
        final StringDescription description = new StringDescription();
        matcher.describeMismatch(value, description);
        Assertions.assertAll(
            () -> Assertions.assertFalse(matches, "An incompatible value must not match"),
            () -> MatcherAssert.assertThat(
                "The mismatch must identify the current value",
                description.toString(),
                Matchers.allOf(
                    Matchers.containsString("Xtory"),
                    Matchers.containsString(String.valueOf(value))
                )
            ),
            () -> Assertions.assertFalse(called.get(), "Rejected inputs must not reach the XML matcher")
        );
    }

    private static Stream<Arguments> incompatibleValues() {
        return Stream.of(
            Arguments.of("another value", false),
            Arguments.of(new Object(), false),
            Arguments.of(null, false),
            Arguments.of("another value", true),
            Arguments.of(new Object(), true),
            Arguments.of(null, true)
        );
    }
}
