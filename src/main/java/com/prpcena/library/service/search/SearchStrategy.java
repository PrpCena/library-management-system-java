// src/main/java/com/prpcena/library/service/search/SearchStrategy.java
package com.prpcena.library.service.search;

import java.util.List;

/**
 * Generic interface for a search strategy.
 * 
 * @param <T> The type of item to search for.
 */
@FunctionalInterface // Good practice for single-method interfaces
public interface SearchStrategy<T> {
    /**
     * Filters a list of items based on a query string.
     * 
     * @param items The list of items to search within.
     * @param query The search query string.
     * @return A list of items matching the query.
     */
    List<T> search(List<T> items, String query);
}