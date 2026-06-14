package com.devgabriel.bstrong.features.verses.domain.model;

import com.devgabriel.bstrong.features.verses.domain.model.WordToken;
import java.util.List;

public class Verse {
    private final int number;
    private final String sectionTitle;
    private final String rawText;
    private List<WordToken> tokens;

    public Verse(int number, String sectionTitle, String rawText) {
        this.number = number;
        this.sectionTitle = sectionTitle;
        this.rawText = rawText;
    }

    public int getNumber() { return number; }
    public String getSectionTitle() { return sectionTitle; }
    public String getRawText() { return rawText; }
    public List<WordToken> getTokens() { return tokens; }
    public void setTokens(List<WordToken> tokens) { this.tokens = tokens; }
}
