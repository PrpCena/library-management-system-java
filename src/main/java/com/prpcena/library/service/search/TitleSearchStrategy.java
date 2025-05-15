// src/main/java/com/prpcena/library/service/search/TitleSearchStrategy.java
package com.prpcena.library.service.search;

import java.util.List;
import java.util.stream.Collectors;

import com.prpcena.library.model.Book;

public class TitleSearchStrategy implements SearchStrategy<Book> {
    @Override
    public List<Book> search(List<Book> books, String query) {
        if (query == null || query.trim().isEmpty()) {
            return books; // Or an empty list, depending on desired behavior for empty query
        }
        String lowerCaseQuery = query.toLowerCase();
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());
    }
}