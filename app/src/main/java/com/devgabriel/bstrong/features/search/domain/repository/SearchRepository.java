package com.devgabriel.bstrong.features.search.domain.repository;
import java.util.List;
import com.devgabriel.bstrong.features.search.domain.model.SearchResult;

public interface SearchRepository {
    List<SearchResult> search(String query, Integer testament);    
}