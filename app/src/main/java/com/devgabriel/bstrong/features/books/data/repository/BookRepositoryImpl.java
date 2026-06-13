package com.devgabriel.bstrong.features.books.data.repository;
import java.util.List;
import com.devgabriel.bstrong.features.books.domain.model.Book;
import com.devgabriel.bstrong.features.books.domain.repository.BookRepository;
import com.devgabriel.bstrong.features.books.data.datasource.BookLocalDataSource;

public class BookRepositoryImpl implements BookRepository {
    private final BookLocalDataSource localDataSource;
    public BookRepositoryImpl(BookLocalDataSource localDataSource){
        this.localDataSource = localDataSource;
    }
    @Override
    public List<Book> getAllBooks(){
        return localDataSource.getAllBooks();
    }
}