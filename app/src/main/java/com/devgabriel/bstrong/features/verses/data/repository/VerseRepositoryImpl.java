package com.devgabriel.bstrong.features.verses.data.repository;
import java.util.List;
import com.devgabriel.bstrong.features.verses.domain.repository.VerseRepository;
import com.devgabriel.bstrong.features.verses.data.datasource.VerseLocalDataSource;
import com.devgabriel.bstrong.features.verses.domain.model.Verse;

public class VerseRepositoryImpl implements VerseRepository{
    private final VerseLocalDataSource localDataSource;
    public VerseRepositoryImpl(VerseLocalDataSource localDataSource){
        this.localDataSource = localDataSource;
    }
    @Override
    public List<Verse> getVerses(int bookId, int chapter){
        return localDataSource.getVerses(bookId, chapter);
    }
}