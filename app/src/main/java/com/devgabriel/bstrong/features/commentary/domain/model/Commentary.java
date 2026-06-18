package com.devgabriel.bstrong.features.commentary.domain.model;

import java.util.Objects;

public class Commentary {
    
    private final int bookId;
    private final int chapter;
    private final int verse;
    private final String text;

    public Commentary(int bookId, int chapter, int verse, String text) {
        this.bookId = bookId;
        this.chapter = chapter;
        this.verse = verse;
        this.text = text;
    }

    public int getBookId() {
        return bookId;
    }

    public int getChapter() {
        return chapter;
    }

    public int getVerse() {
        return verse;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commentary that = (Commentary) o;
        return bookId == that.bookId && 
               chapter == that.chapter && 
               verse == that.verse && 
               Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, chapter, verse, text);
    }
}
