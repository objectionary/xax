/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 Yegor Bugayenko
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
import com.yegor256.xsline.Xsline;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * A decorator of {@link Xtory} that guarantees any method is only
 * delegated to the decoratee only once.
 *
 * @since 0.1.0
 */
public final class XtSticky implements Xtory {

    /**
     * Original story.
     */
    private final Xtory origin;

    /**
     * The cache.
     */
    private final ConcurrentHashMap<String, Object> cache;

    /**
     * Ctor.
     * @param xtory Original story
     */
    public XtSticky(final Xtory xtory) {
        this.origin = xtory;
        this.cache = new ConcurrentHashMap<>(0);
    }

    @Override
    public Map<String, Object> map() {
        return this.through("map", this.origin::map);
    }

    @Override
    public XML before() {
        return this.through("before", this.origin::before);
    }

    @Override
    public XML after() {
        return this.through("after", this.origin::after);
    }

    @Override
    public Xsline xsline() {
        return this.through("xsline", this.origin::xsline);
    }

    @Override
    public Collection<String> asserts() {
        return this.through("asserts", this.origin::asserts);
    }

    @SuppressWarnings("unchecked")
    private <T> T through(final String method, final Supplier<T> supplier) {
        return (T) this.cache.computeIfAbsent(
            method,
            s -> supplier.get()
        );
    }

}
