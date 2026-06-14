package com.devgabriel.bstrong.features.search.domain.model;
import com.devgabriel.bstrong.features.verses.domain.model.WordToken;
import java.util.List;

public class SearchResult {
    private final int bookId;
    private final String bookShortName;
    private final int chapter;
    private final int verse;
    private final String text;
    private List<WordToken> tokens;

    public SearchResult(int bookId, String bookShortName, int chapter, int verse, String text) {
        this.bookId = bookId;
        this.bookShortName = bookShortName;
        this.chapter = chapter;
        this.verse = verse;
        this.text = text;
    }

    public int getBookId() { return bookId; }
    public String getBookShortName() { return bookShortName; }
    public int getChapter() { return chapter; }
    public int getVerse() { return verse; }
    public String getText() { return text; }
    public List<WordToken> getTokens() { return tokens; }
    public void setTokens(List<WordToken> tokens) { this.tokens = tokens; }
}
