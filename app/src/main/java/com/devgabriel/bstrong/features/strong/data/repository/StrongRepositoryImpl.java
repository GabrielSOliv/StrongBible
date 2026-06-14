package com.devgabriel.bstrong.features.strong.data.repository;
import com.devgabriel.bstrong.features.strong.domain.model.StrongEntry;
import com.devgabriel.bstrong.features.strong.domain.repository.StrongRepository;
import com.devgabriel.bstrong.features.strong.data.datasource.StrongLocalDataSource;

public class StrongRepositoryImpl implements StrongRepository{
    private final StrongLocalDataSource localDataSource;
    public StrongRepositoryImpl(StrongLocalDataSource localDataSource){
        this.localDataSource = localDataSource;
    }
    @Override
    public StrongEntry getStrong(String code){
        return localDataSource.getStrong(code);
    }
}
