/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.eolang.xax;

import com.jcabi.xml.XML;
import com.yegor256.xsline.Xsline;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
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
     * Sentinel for null cached value.
     */
    private static final Object NULL = new Object();

    /**
     * Original story.
     */
    private final Xtory origin;

    /**
     * The cache.
     */
    private final Map<String, Object> cache;

    /**
     * Ctor.
     *
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
        final Object value = this.cache.computeIfAbsent(
            method,
            name -> {
                final Object res = supplier.get();
                final Object ret;
                if (res == null) {
                    ret = XtSticky.NULL;
                } else {
                    ret = res;
                }
                return ret;
            }
        );
        final T result;
        if (Objects.equals(value, XtSticky.NULL)) {
            result = null;
        } else {
            result = (T) value;
        }
        return result;
    }
}
