/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.eolang.xax;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XML;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.cactoos.io.InputOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.jucs.ClasspathSource;
import org.eolang.parser.EoSyntax;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test case for {@link XtoryMatcher}.
 *
 * @since 0.1.0
 */
final class XtoryMatcherTest {

    @Test
    void forwardsTransformedXmlToExtraMatcherMismatchDescription() {
        final AtomicReference<Object> evaluated = new AtomicReference<>();
        final AtomicReference<Object> mismatched = new AtomicReference<>();
        final Matcher<XML> extra = new BaseMatcher<XML>() {
            @Override
            public boolean matches(final Object item) {
                evaluated.set(item);
                return false;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("always fails");
            }

            @Override
            public void describeMismatch(
                final Object item,
                final Description description
            ) {
                mismatched.set(item);
            }
        };
        final Xtory story = new XtYaml(
            String.format(
                "sheets: []%ndocument: <doc/>%nasserts:%n  - /doc%n"
            )
        );
        final XtoryMatcher matcher = new XtoryMatcher(extra);
        matcher.matches(story);
        matcher.describeMismatch(story, new StringDescription());
        MatcherAssert.assertThat(
            "Mismatch object must be the evaluated XML instance",
            mismatched.get(),
            Matchers.allOf(
                Matchers.instanceOf(XML.class),
                Matchers.sameInstance(evaluated.get())
            )
        );
    }

    @Test
    void forwardsUpdatedXmlWhenMatcherIsReusedWithDifferentStory() {
        final AtomicReference<Object> mismatched = new AtomicReference<>();
        final XtoryMatcher matcher = new XtoryMatcher(
            new BaseMatcher<XML>() {
                @Override
                public boolean matches(final Object item) {
                    return false;
                }

                @Override
                public void describeTo(final Description description) {
                    description.appendText("always fails");
                }

                @Override
                public void describeMismatch(
                    final Object item,
                    final Description description
                ) {
                    mismatched.set(item);
                }
            }
        );
        matcher.matches(
            new XtYaml(
                String.format(
                    "sheets: []%ndocument: <first/>%nasserts:%n  - /first%n"
                )
            )
        );
        matcher.describeMismatch(null, new StringDescription());
        final String first = mismatched.get().toString();
        matcher.matches(
            new XtYaml(
                String.format(
                    "sheets: []%ndocument: <second/>%nasserts:%n  - /second%n"
                )
            )
        );
        matcher.describeMismatch(null, new StringDescription());
        MatcherAssert.assertThat(
            "Mismatch XML must update on reuse",
            Arrays.asList(first, mismatched.get().toString()),
            Matchers.contains(
                Matchers.containsString("<first"),
                Matchers.containsString("<second")
            )
        );
    }

    @Test
    void describesXpathMismatchWhenAssertionsFailBeforeExtraMatcherRuns() {
        final Xtory story = new XtYaml(
            String.format(
                "sheets: []%ndocument: <doc/>%nasserts:%n  - /absent%n"
            )
        );
        final XtoryMatcher matcher = new XtoryMatcher(
            new BaseMatcher<XML>() {
                @Override
                public boolean matches(final Object item) {
                    return false;
                }

                @Override
                public void describeTo(final Description description) {
                    description.appendText("should not be called");
                }
            }
        );
        matcher.matches(story);
        final Description desc = new StringDescription();
        matcher.describeMismatch(story, desc);
        MatcherAssert.assertThat(
            "Mismatch description must report failing XPath expressions",
            desc.toString(),
            Matchers.allOf(
                Matchers.containsString("1 XPath expression(s) failed"),
                Matchers.containsString("/absent")
            )
        );
    }

    @Test
    void skipsExtraMatcherWhenAssertionsFail() {
        final AtomicBoolean called = new AtomicBoolean();
        new XtoryMatcher(
            new BaseMatcher<XML>() {
                @Override
                public boolean matches(final Object item) {
                    called.set(true);
                    return false;
                }

                @Override
                public void describeTo(final Description description) {
                    description.appendText("unused");
                }
            }
        ).matches(
            new XtYaml(
                String.format(
                    "sheets: []%ndocument: <doc/>%nasserts:%n  - /absent%n"
                )
            )
        );
        MatcherAssert.assertThat(
            "Extra matcher must not be invoked when assertions fail",
            called.get(),
            Matchers.is(false)
        );
    }

    @Test
    void matchesSuccessfullyWithValidStoryAndPassingExtraMatcher() {
        MatcherAssert.assertThat(
            "Matcher should return true when assertions and extra pass",
            new XtoryMatcher(
                XhtmlMatchers.hasXPath("/doc/t[.='v']")
            ).matches(
                new XtYaml(
                    String.join(
                        System.lineSeparator(),
                        "sheets: []",
                        "document: <doc><t>v</t></doc>",
                        "asserts:",
                        "  - /doc/t",
                        ""
                    )
                )
            ),
            Matchers.is(true)
        );
    }

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
