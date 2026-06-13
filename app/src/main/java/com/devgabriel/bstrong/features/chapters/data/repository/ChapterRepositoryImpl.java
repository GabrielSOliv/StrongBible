package com.devgabriel.bstrong.features.chapters.repository;
import java.util.List;
import com.devgabriel.bstrong.features.chapters.domain.repository.ChapterRepository;
import com.devgabriel.bstrong.features.chapters.data.datasource.ChapterLocalDataSource;

public class ChapterRepositoryImpl implements ChapterRepository{
    private final ChapterLocalDataSource localDataSource;
    public ChapterRepositoryImpl(ChapterLocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }
    @Override
    public List<Integer> getChapters(int bookId){
        return localDataSource.getChapters(bookId);   
    }
}