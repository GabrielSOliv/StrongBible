package com.devgabriel.bstrong.features.chapters.domain.repository;
import java.util.List;

public interface ChapterRepository {
    List<Integer> getChapters(int bookId);
}