package com.devgabriel.bstrong.features.books.presentation.viewmodel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MediatorLiveData;
import java.util.ArrayList;
import java.util.List;
import com.devgabriel.bstrong.features.books.domain.repository.BookRepository;
import com.devgabriel.bstrong.features.books.domain.model.Book;

public class BookViewModel extends ViewModel{
    private final BookRepository repository;
    private final MutableLiveData<List<Book>> allBooks = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedTab = new MutableLiveData<>(1);
    private final MediatorLiveData<List<Book>> filteredBooks = new MediatorLiveData<>();

    public BookViewModel(BookRepository repository){
        this.repository = repository;
        filteredBooks.addSource(allBooks, books -> applyFilter(filteredBooks, books, selectedTab.getValue()));
        filteredBooks.addSource(selectedTab, tab -> applyFilter(filteredBooks, allBooks.getValue(), tab));
        loadBooks();
    }
    public void loadBooks(){
        List<Book> result = repository.getAllBooks();
        allBooks.setValue(result);
    }
    public void setTab(int tabId) {
        selectedTab.setValue(tabId);
    }
    public LiveData<List<Book>> getFilteredBooks() {
        return filteredBooks;
    }

    private void applyFilter(MutableLiveData<List<Book>> result, List<Book> books, Integer tab) {
        if (books == null) return;
        List<Book> filtered = new ArrayList<>();
        for (Book b : books) {
            if (b.getTestamentId() == tab) {
                filtered.add(b);
            }
        }
        result.setValue(filtered);
    }
}