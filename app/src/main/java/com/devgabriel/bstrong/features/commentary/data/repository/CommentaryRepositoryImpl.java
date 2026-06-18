package com.devgabriel.bstrong.features.commentary.data.repository;
import java.util.List;
import com.devgabriel.bstrong.features.commentary.domain.repository.CommentaryRepository;
import com.devgabriel.bstrong.features.commentary.data.datasource.CommentaryLocalDataSource;
import com.devgabriel.bstrong.features.commentary.domain.model.Commentary;

public class CommentaryRepositoryImpl implements CommentaryRepository {

    private final CommentaryLocalDataSource localDataSource;

    public CommentaryRepositoryImpl(CommentaryLocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    @Override
    public List<Commentary> getCommentariesForChapter(int bookId, int chapter) {
        return localDataSource.getCommentariesForChapter(bookId, chapter);
    }

    @Override
    public Commentary getCommentaryForVerse(int bookId, int chapter, int verse) {
        return localDataSource.getCommentaryForVerse(bookId, chapter, verse);
    }
}