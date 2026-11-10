/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.eolang.xax;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.xsline.Xsline;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link XtSticky}.
 *
 * @since 0.1.0
 */
final class XtStickyTest {

    @Test
    void parsesAndTransforms() throws Exception {
        final Xtory xtory = new XtSticky(
            new XtYaml(
                new TextOf(
                    new ResourceOf("org/eolang/xax/packs/simple.yaml")
                ).asString()
            )
        );
        MatcherAssert.assertThat(
            "Fails to pass",
            XhtmlMatchers.xhtml(xtory.after()),
            XhtmlMatchers.hasXPaths(xtory.asserts())
        );
    }

    @Test
    void cachesNullResult() {
        final AtomicInteger counter = new AtomicInteger();
        final Xtory sticky = new XtSticky(new XtStickyTest.Fake(counter, true));
        sticky.map();
        sticky.map();
        MatcherAssert.assertThat(
            "Expected map() to be delegated to decoratee only once",
            counter.get(),
            Matchers.is(1)
        );
    }

    @Test
    void cachesNullResultInThreads() {
        final AtomicInteger counter = new AtomicInteger();
        final Xtory sticky = new XtSticky(new XtStickyTest.Fake(counter, true));
        IntStream.range(0, 50).parallel().forEach(idx -> sticky.map());
        MatcherAssert.assertThat(
            "Expected map() to be delegated only once under concurrency",
            counter.get(),
            Matchers.is(1)
        );
    }

    @Test
    void cachesBefore() {
        final Xtory sticky = new XtSticky(new XtStickyTest.Fake());
        sticky.before();
        MatcherAssert.assertThat(
            "Expected before() to return non-null XML",
            sticky.before(),
            Matchers.notNullValue()
        );
    }

    @Test
    void cachesAfter() {
        final Xtory sticky = new XtSticky(new XtStickyTest.Fake());
        sticky.after();
        MatcherAssert.assertThat(
            "Expected after() to return non-null XML",
            sticky.after(),
            Matchers.notNullValue()
        );
    }

    @Test
    void cachesXsline() {
        final Xtory sticky = new XtSticky(new XtStickyTest.Fake());
        sticky.xsline();
        MatcherAssert.assertThat(
            "Expected xsline() to return non-null Xsline",
            sticky.xsline(),
            Matchers.notNullValue()
        );
    }

    @Test
    void cachesMap() {
        final Xtory sticky = new XtSticky(new XtStickyTest.Fake());
        sticky.map();
        MatcherAssert.assertThat(
            "Expected map() to contain key",
            sticky.map(),
            Matchers.hasKey("k")
        );
    }

    @Test
    void cachesAsserts() {
        final Xtory sticky = new XtSticky(new XtStickyTest.Fake());
        sticky.asserts();
        MatcherAssert.assertThat(
            "Expected asserts() to contain sample xpath",
            sticky.asserts(),
            Matchers.hasItem("//sample")
        );
    }

    /**
     * Fake story for tests.
     *
     * @since 0.1.0
     */
    private static final class Fake implements Xtory {

        /**
         * Counter.
         */
        private final AtomicInteger counter;

        /**
         * Return nulls?
         */
        private final boolean nullable;

        /**
         * Ctor.
         */
        Fake() {
            this(new AtomicInteger(), false);
        }

        /**
         * Ctor.
         *
         * @param count Counter
         * @param nulls Return nulls?
         */
        Fake(final AtomicInteger count, final boolean nulls) {
            this.counter = count;
            this.nullable = nulls;
        }

        @Override
        public Map<String, Object> map() {
            this.counter.incrementAndGet();
            final Map<String, Object> res;
            if (this.nullable) {
                res = null;
            } else {
                res = Collections.singletonMap("k", "v");
            }
            return res;
        }

        @Override
        public XML before() {
            this.counter.incrementAndGet();
            final XML res;
            if (this.nullable) {
                res = null;
            } else {
                res = new XMLDocument("<sample/>");
            }
            return res;
        }

        @Override
        public XML after() {
            this.counter.incrementAndGet();
            final XML res;
            if (this.nullable) {
                res = null;
            } else {
                res = new XMLDocument("<sample/>");
            }
            return res;
        }

        @Override
        public Xsline xsline() {
            this.counter.incrementAndGet();
            final Xsline res;
            if (this.nullable) {
                res = null;
            } else {
                res = new Xsline(Collections.emptyList());
            }
            return res;
        }

        @Override
        public Collection<String> asserts() {
            this.counter.incrementAndGet();
            final Collection<String> res;
            if (this.nullable) {
                res = null;
            } else {
                res = Collections.singletonList("//sample");
            }
            return res;
        }
    }
}
