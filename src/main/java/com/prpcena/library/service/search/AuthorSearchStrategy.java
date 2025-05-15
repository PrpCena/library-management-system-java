// src/main/java/com/prpcena/library/service/search/AuthorSearchStrategy.java
package com.prpcena.library.service.search;

import java.util.List;
import java.util.stream.Collectors;

import com.prpcena.library.model.Book;

public class AuthorSearchStrategy implements SearchStrategy<Book> {
    @Override
    public List<Book> search(List<Book> books, String query) {
        if (query == null || query.trim().isEmpty()) {
            return books;
        }
        String lowerCaseQuery = query.toLowerCase();
        return books.stream()
                .filter(book -> book.getAuthor().getFullName().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());
    }
}