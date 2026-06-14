package com.devgabriel.bstrong.features.search.data.repository;
import java.util.List;
import com.devgabriel.bstrong.features.search.domain.model.SearchResult;
import com.devgabriel.bstrong.features.search.domain.repository.SearchRepository;
import com.devgabriel.bstrong.features.search.data.datasource.SearchLocalDataSource;

public class SearchRepositoryImpl implements SearchRepository{
    private final SearchLocalDataSource localDataSource;
    public SearchRepositoryImpl(SearchLocalDataSource localDataSource){
        this.localDataSource = localDataSource;
    }
    @Override
    public List<SearchResult> search(String query){
        return localDataSource.search(query);
    }
}
