package com.devgabriel.bstrong.features.books.domain.repository;
import java.util.List;
import com.devgabriel.bstrong.features.books.domain.model.Book;

public interface BookRepository {
    List<Book> getAllBooks();
}