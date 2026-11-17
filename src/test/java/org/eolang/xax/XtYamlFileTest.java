/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.eolang.xax;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for file URI stylesheets.
 *
 * @since 1.0
 */
final class XtYamlFileTest {

    @ParameterizedTest
    @ValueSource(strings = {"plain.xsl", "with space.xsl", "plus+hash#.xsl"})
    void transformsWithFileUri(final String name, @TempDir final Path directory)
        throws IOException {
        final Path sheet = directory.resolve(name);
        Files.writeString(
            sheet,
            String.join(
                "",
                "<xsl:stylesheet version='1.0' id='file-style' ",
                "xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>",
                "<xsl:template match='/'><resolved/></xsl:template>",
                "</xsl:stylesheet>"
            )
        );
        MatcherAssert.assertThat(
            "File URI must locate the stylesheet and decode its escaped filename",
            new XtYaml(
                String.format("document: <original/>%nsheets:%n  - '%s'%n", sheet.toUri())
            ).after(),
            XhtmlMatchers.hasXPath("/resolved")
        );
    }
}
