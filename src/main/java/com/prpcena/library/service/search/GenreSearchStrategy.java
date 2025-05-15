// src/main/java/com/prpcena/library/service/search/GenreSearchStrategy.java
package com.prpcena.library.service.search;

import java.util.List;
import java.util.stream.Collectors;

import com.prpcena.library.model.Book;

public class GenreSearchStrategy implements SearchStrategy<Book> {
    @Override
    public List<Book> search(List<Book> books, String query) {
        if (query == null || query.trim().isEmpty()) {
            return books;
        }
        String lowerCaseQuery = query.toLowerCase();
        // For genre, you might want an exact match or a contains, depending on how
        // genres are stored/entered.
        // Using 'contains' for flexibility here.
        return books.stream()
                .filter(book -> book.getGenre().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());
    }
}