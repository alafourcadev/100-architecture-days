package com.architecturedays.day004.dto;

import java.util.List;

/**
 * Record para Cursor Pagination response
 * Java 25 - Records para datos inmutables
 */
public record CursorPage<T>(
    List<T> content,
    String nextCursor,
    String previousCursor,
    boolean hasNext,
    boolean hasPrevious,
    int size
) {
    public static <T> CursorPage<T> of(List<T> content, String nextCursor, String prevCursor, boolean hasNext) {
        return new CursorPage<>(
            content,
            nextCursor,
            prevCursor,
            hasNext,
            prevCursor != null,
            content.size()
        );
    }
}
