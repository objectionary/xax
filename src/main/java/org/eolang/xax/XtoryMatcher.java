/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.eolang.xax;

import com.jcabi.xml.XML;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Assumptions;

/**
 * Hamcrest matcher for a YAML story.
 *
 * @since 0.1.0
 */
@SuppressWarnings("PMD.ConstructorShouldDoInitialization")
public final class XtoryMatcher extends BaseMatcher<Xtory> {

    /**
     * The header of the match.
     */
    private String header;

    /**
     * The summary of the match.
     */
    private String summary;

    /**
     * Did it match?
     */
    private boolean match;

    /**
     * Extra matcher for the outcoming XML.
     * @since 0.6.0
     */
    private final Matcher<XML> extra;

    /**
     * Default ctor.
     * @since 0.6.0
     */
    public XtoryMatcher() {
        this(
            new BaseMatcher<XML>() {
                @Override
                public boolean matches(final Object input) {
                    return true;
                }

                @Override
                public void describeTo(final Description description) {
                    assert description != null;
                }
            }
        );
    }

    /**
     * With an extra matcher.
     * @param ext Extra matcher
     * @since 0.6.0
     */
    public XtoryMatcher(final Matcher<XML> ext) {
        super();
        this.extra = ext;
    }

    @Override
    public boolean matches(final Object object) {
        final Xtory xtory = Xtory.class.cast(object);
        Assumptions.assumeTrue(xtory.map().get("skip") == null);
        final XML after = xtory.xsline().pass(xtory.before());
        final Collection<Map.Entry<String, Boolean>> xpaths =
            new LinkedList<>();
        int failures = 0;
        for (final String xpath : xtory.asserts()) {
            final boolean success = !after.nodes(xpath).isEmpty();
            xpaths.add(new AbstractMap.SimpleImmutableEntry<>(xpath, success));
            if (!success) {
                ++failures;
            }
        }
        this.header = String.format(
            "All %d XPath expressions matched",
            xpaths.size()
        );
        final StringBuilder sum = new StringBuilder(1024)
            .append(String.format("%d XPath expression(s) failed:\n", failures));
        for (final Map.Entry<String, Boolean> ent : xpaths) {
            sum.append("  ");
            if (ent.getValue()) {
                sum.append("OK");
            } else {
                sum.append("FAIL");
            }
            sum.append(": ").append(ent.getKey()).append('\n');
        }
        sum
            .append("\nXML before XSL transformation:\n  ")
            .append(xtory.before().toString().replace("\n", "\n  "))
            .append(
                String.format(
                    "\nXML after XSL transformation (%d->%d chars):\n  ",
                    xtory.before().toString().length(),
                    after.toString().length()
                )
            )
            .append(after.toString().replace("\n", "\n  "));
        this.summary = sum.toString();
        this.match = true;
        for (final Map.Entry<String, Boolean> ent : xpaths) {
            if (!ent.getValue()) {
                this.match = false;
                break;
            }
        }
        return this.match && this.extra.matches(after);
    }

    @Override
    public void describeTo(final Description desc) {
        if (this.match) {
            this.extra.describeTo(desc);
        } else {
            desc.appendText(this.header);
        }
    }

    @Override
    public void describeMismatch(final Object story, final Description desc) {
        if (this.match) {
            this.extra.describeMismatch(story, desc);
        } else {
            desc.appendText("\n").appendText(this.summary);
        }
    }

}
