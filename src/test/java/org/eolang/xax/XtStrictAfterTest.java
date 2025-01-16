/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 Yegor Bugayenko
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

import com.jcabi.matchers.XhtmlMatchers;
import com.yegor256.WeAreOnline;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link XtStrictAfter}.
 *
 * @since 0.1.0
 */
final class XtStrictAfterTest {

    @Test
    @ExtendWith(WeAreOnline.class)
    void parsesAndTransforms() throws Exception {
        final Xtory xtory = new XtStrictAfter(
            new XtYaml(
                new TextOf(
                    new ResourceOf("org/eolang/xax/packs/with-schema.yaml")
                ).asString()
            )
        );
        MatcherAssert.assertThat(
            "Fails to pass",
            XhtmlMatchers.xhtml(xtory.after()),
            XhtmlMatchers.hasXPaths(xtory.asserts())
        );
    }

}
