package com.devgabriel.bstrong.features.search.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.devgabriel.bstrong.features.search.domain.model.SearchResult;
import com.devgabriel.bstrong.features.search.domain.repository.SearchRepository;
import java.util.List;

public class SearchViewModel extends ViewModel{
    private final SearchRepository repository;
    private final MutableLiveData<List<SearchResult>> search = new MutableLiveData<>();

    public SearchViewModel(SearchRepository repository){
        this.repository = repository;
    }
    public void loadSearch(String code){
        List<SearchResult> result = repository.search(code);
        search.setValue(result);
    }
    public LiveData<List<SearchResult>> getResults(){
        return search;
    }
}
