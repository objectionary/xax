/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.eolang.xax;

import com.jcabi.matchers.XhtmlMatchers;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Xtory}.
 *
 * @since 0.1.0
 */
final class XtoryTest {

    @Test
    void parsesAndTransforms() throws Exception {
        final Xtory xtory = new XtYaml(
            new TextOf(
                new ResourceOf("org/eolang/xax/packs/simple.yaml")
            ).asString()
        );
        MatcherAssert.assertThat(
            "Fails to find expected XPaths",
            XhtmlMatchers.xhtml(xtory.after()),
            XhtmlMatchers.hasXPaths(xtory.asserts())
        );
    }

}
