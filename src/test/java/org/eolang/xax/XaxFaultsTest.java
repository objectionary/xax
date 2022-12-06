/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.xax;

import org.eolang.jucs.ClasspathSource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test case for {@link XaxFaults}.
 *
 * @since 0.0.1
 */
final class XaxFaultsTest {

    @Test
    void detectsSkip() {
        MatcherAssert.assertThat(
            new XaxFaults("skip: true\n").skip(),
            Matchers.is(true)
        );
    }

    @Test
    void detectsAsserts() {
        MatcherAssert.assertThat(
            new XaxFaults("skip: true\n").asserts(),
            Matchers.is(Matchers.emptyIterable())
        );
    }

    @Test
    void detectsSheets() {
        MatcherAssert.assertThat(
            new XaxFaults("skip: true\n").train(),
            Matchers.is(Matchers.emptyIterable())
        );
    }

    @Test
    void detectsDocument() {
        MatcherAssert.assertThat(
            new XaxFaults("document: <foo/>\n").document().nodes("/foo"),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/xax/packs", glob = "**.yaml")
    void validatesSimpleScenario(final String yaml) {
        Assumptions.assumeFalse(new XaxFaults(yaml).skip());
        MatcherAssert.assertThat(
            new XaxFaults(yaml),
            Matchers.emptyIterable()
        );
    }

    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/xax/broken", glob = "**.yaml")
    void validatesBrokenScenario(final String yaml) {
        Assumptions.assumeFalse(new XaxFaults(yaml).skip());
        MatcherAssert.assertThat(
            new XaxFaults(yaml),
            Matchers.not(Matchers.emptyIterable())
        );
    }

}
