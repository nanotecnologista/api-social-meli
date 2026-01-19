package com.api.social.meli.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtils {

    private PaginationUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Pageable resolvePageable(Integer page, Integer size, Integer offset, Integer limit) {
        return resolvePageable(page, size, offset, limit, Sort.unsorted());
    }

    public static Pageable resolvePageable(Integer page, Integer size, Integer offset, Integer limit, Sort sort) {
        int resolvedSize = (size != null ? size : (limit != null ? limit : 10));
        if (resolvedSize <= 0) {
            throw new IllegalArgumentException("size must be greater than 0");
        }

        int resolvedPage;
        if (page != null) {
            resolvedPage = page;
        } else if (offset != null) {
            if (offset < 0) {
                throw new IllegalArgumentException("offset must be greater than or equal to 0");
            }
            resolvedPage = offset / resolvedSize;
        } else {
            resolvedPage = 0;
        }

        if (resolvedPage < 0) {
            throw new IllegalArgumentException("page must be greater than or equal to 0");
        }

        return PageRequest.of(resolvedPage, resolvedSize, sort);
    }
}
