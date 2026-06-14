package com.devgabriel.bstrong.features.verses.domain.repository;
import com.devgabriel.bstrong.features.verses.domain.model.Verse;
import java.util.List;

public interface VerseRepository {
    List<Verse> getVerses(int bookId, int chapter);    
} 