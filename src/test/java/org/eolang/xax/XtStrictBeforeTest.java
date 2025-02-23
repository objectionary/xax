/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
 * Test case for {@link XtStrictBefore}.
 *
 * @since 0.1.0
 */
final class XtStrictBeforeTest {

    @Test
    @ExtendWith(WeAreOnline.class)
    void parsesAndTransforms() throws Exception {
        final Xtory xtory = new XtStrictBefore(
            new XtYaml(
                new TextOf(
                    new ResourceOf("org/eolang/xax/packs/with-schema.yaml")
                ).asString()
            )
        );
        MatcherAssert.assertThat(
            "Fails to pass",
            XhtmlMatchers.xhtml(xtory.before()),
            XhtmlMatchers.hasXPaths(xtory.asserts())
        );
    }

}
