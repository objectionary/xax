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

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.TrClasspath;
import com.yegor256.xsline.Xsline;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.yaml.snakeyaml.Yaml;

/**
 * Hamcrest matcher.
 *
 * @since 0.0.1
 */
public final class XaxMatcher extends TypeSafeMatcher<String> {

    /**
     * List of failures.
     */
    private final Collection<String> failures = new LinkedList<>();

    @Override
    @SuppressWarnings("unchecked")
    public boolean matchesSafely(final String yaml) {
        final Map<String, Object> map = new Yaml().load(yaml);
        TrClasspath<Shift> train = new TrClasspath<>();
        for (final String sheet : (Iterable<String>) map.get("sheets")) {
            train = train.with(sheet);
        }
        final XML before = new XMLDocument(map.get("document").toString());
        final XML after = new Xsline(train.back()).pass(before);
        boolean good = true;
        for (final String xpath : (Iterable<String>) map.get("asserts")) {
            if (after.nodes(xpath).isEmpty()) {
                good = false;
                this.failures.add(xpath);
            }
        }
        return good;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText(
            String.format("%d XPaths assertions failed: ", this.failures.size())
        );
        description.appendText(this.failures.toString());
    }
}
