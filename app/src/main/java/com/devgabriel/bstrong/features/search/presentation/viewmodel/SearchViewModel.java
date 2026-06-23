package com.devgabriel.bstrong.features.search.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.devgabriel.bstrong.features.search.domain.model.SearchResult;
import com.devgabriel.bstrong.features.search.domain.repository.SearchRepository;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchViewModel extends ViewModel {
    
    private final SearchRepository repository;
    private final MutableLiveData<List<SearchResult>> search = new MutableLiveData<>();
    
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public SearchViewModel(SearchRepository repository) {
        this.repository = repository;
    }

    public void loadSearch(String code, Integer testament) {
        executorService.execute(() -> {
            List<SearchResult> result = repository.search(code, testament);
            search.postValue(result);
        });
    }

    public LiveData<List<SearchResult>> getResults() {
        return search;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}