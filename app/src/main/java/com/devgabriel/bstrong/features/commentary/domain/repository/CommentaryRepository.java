package com.devgabriel.bstrong.features.commentary.domain.repository;
import com.devgabriel.bstrong.features.commentary.domain.model.Commentary;
import java.util.List;

public interface CommentaryRepository {
    List<Commentary> getCommentariesForChapter(int bookId, int chapter);
    Commentary getCommentaryForVerse(int bookId, int chapter, int verse);
}
