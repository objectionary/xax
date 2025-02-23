/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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

    /**
     * Through the cache.
     * @param method The method
     * @param supplier The supplier
     * @param <T> Type of the object
     * @return The object calculated
     */
    @SuppressWarnings("unchecked")
    private <T> T through(final String method, final Supplier<T> supplier) {
        return (T) this.cache.computeIfAbsent(
            method,
            s -> supplier.get()
        );
    }

}
