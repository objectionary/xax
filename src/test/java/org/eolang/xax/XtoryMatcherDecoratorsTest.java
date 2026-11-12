/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.eolang.xax;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for story decorators evaluated through the matcher.
 *
 * @since 1.0
 */
final class XtoryMatcherDecoratorsTest {

    @ParameterizedTest
    @MethodSource("strictStories")
    void validatesDecoratedOutput(final Xtory story) {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new XtoryMatcher().matches(story),
            "The matcher must reject output that violates its decorator's XSD"
        );
    }

    @Test
    void reusesTheStickyOutputForRepeatedMatches() {
        final Xtory story = new XtSticky(
            new XtYaml(String.format("document: <doc/>%nasserts:%n  - /doc%n"))
        );
        final List<XML> results = new ArrayList<>(2);
        final XtoryMatcher matcher = new XtoryMatcher(
            new BaseMatcher<XML>() {
                @Override
                public boolean matches(final Object item) {
                    results.add(XML.class.cast(item));
                    return true;
                }

                @Override
                public void describeTo(final Description description) {
                    description.appendText("the cached output");
                }
            }
        );
        matcher.matches(story);
        matcher.matches(story);
        MatcherAssert.assertThat(
            "Both matches must reuse the exact output cached by XtSticky",
            results,
            Matchers.allOf(
                Matchers.hasSize(2),
                Matchers.everyItem(Matchers.sameInstance(story.after()))
            )
        );
    }

    private static Stream<Xtory> strictStories() {
        final Xtory story = new XtYaml(
            String.format(
                "document: <doc/>%nsheets:%n  - org/eolang/xax/invalid-output.xsl%n"
                    + "asserts:%n  - /invalid%n"
            )
        );
        final XML schema = new XMLDocument(
            "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
                + "<xs:element name='doc' type='xs:string'/></xs:schema>"
        );
        return Stream.of(new XtStrict(story, schema), new XtStrictAfter(story, schema));
    }
}
